package sistemaprestamo.controlador;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JOptionPane;
import sistemaprestamo.modelo.Cronograma;
import sistemaprestamo.modelo.MontoInvalidoException;
import sistemaprestamo.persistencia.CronogramaDAO;
import sistemaprestamo.vista.FrmRegistroPrestamo;
import java.sql.SQLException;
import java.util.List;
import sistemaprestamo.modelo.Cliente;
import sistemaprestamo.modelo.ItemCatalogo;
import sistemaprestamo.modelo.Prestamo;
import sistemaprestamo.modelo.Sesion;
import sistemaprestamo.modelo.TipoPrestamo;
import sistemaprestamo.modelo.Usuario;
import sistemaprestamo.persistencia.CatalogoDAO;
import sistemaprestamo.persistencia.ClienteDAO;
import sistemaprestamo.persistencia.PrestamoDAO;
import sistemaprestamo.persistencia.UsuarioDAO;

/**
 * Coordina el registro de préstamos financieros (HU-02). Las reglas de negocio están en la
 * clase Prestamo; aquí solo se ordenan los pasos y se consulta la base de datos. La vista
 * (FrmRegistroPrestamo) decide qué mensaje mostrar según la excepción que se lance:
 * MontoInvalidoException (CA-04), IllegalArgumentException (CA-03, CA-05, CA-06) o
 * SQLException (CA-07).
 */
public class PrestamoController {

    private FrmRegistroPrestamo vista;
    private final CronogramaDAO cronogramaDAO = new CronogramaDAO();
    private List<TipoPrestamo> tiposPrestamo = new ArrayList<>();

    public PrestamoController(FrmRegistroPrestamo vista) {
        this();
        this.vista = vista;
    }

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;
    private final CatalogoDAO catalogoDAO;
    private final UsuarioDAO usuarioDAO;

    public PrestamoController() {
        this(new PrestamoDAO(), new ClienteDAO(), new CatalogoDAO());
    }

    public PrestamoController(PrestamoDAO prestamoDAO, ClienteDAO clienteDAO, CatalogoDAO catalogoDAO) {
        this.prestamoDAO = prestamoDAO;
        this.clienteDAO = clienteDAO;
        this.catalogoDAO = catalogoDAO;
        this.usuarioDAO = new UsuarioDAO();
    }

    // ---- Datos del formulario (CA-01) ----

    /** Tipos de préstamo con su plazo, TEA y límites de monto (RN-04 y RN-05). */
    public List<TipoPrestamo> listarTiposPrestamo() throws SQLException {
        return prestamoDAO.listarTiposPrestamo();
    }

    public List<ItemCatalogo> listarMonedas() throws SQLException {
        return catalogoDAO.listarMonedas();
    }

    public List<ItemCatalogo> listarModalidadesPago() throws SQLException {
        return catalogoDAO.listarModalidadesPago();
    }

    public List<ItemCatalogo> listarGarantias() throws SQLException {
        return catalogoDAO.listarGarantias();
    }

    // ---- Cliente (RN-01 y RN-06) ----

    /** Cliente con ese número de documento, o null si no está registrado. */
    public Cliente buscarClientePorDocumento(String numeroDocumento) throws SQLException {
        List<Cliente> encontrados = clienteDAO.buscarPorDocumento(numeroDocumento);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    /** True si el cliente tiene algún préstamo moroso o en incumplimiento (RN-06). */
    public boolean tieneMora(Cliente cliente) throws SQLException {
        for (Prestamo existente : prestamoDAO.listarPorCliente(cliente.getCodigo())) {
            if (existente.estaEnMora()) {
                return true;
            }
        }
        return false;
    }

    // ---- Registro (CA-02) ----

    /**
     * Valida todas las reglas de la HU-02 y guarda el préstamo como Vigente.
     *
     * @return el código del préstamo registrado
     * @throws MontoInvalidoException (subclase de IllegalArgumentException) si el monto está fuera de la política
     * @throws IllegalArgumentException si no se cumple alguna otra regla de negocio
     * @throws SQLException si ocurre un error al guardar
     */
    public String registrar(Prestamo prestamo) throws SQLException {
        Usuario usuario = Sesion.getUsuario();
        Usuario.validarSesionAutorizada(usuario);                                // RN-07
        if (!usuarioDAO.estaAutorizado(usuario)) {
            throw new IllegalArgumentException(
                    "El usuario ya no está activo o no está autorizado como Analista de Crédito.");
        }
        prestamo.validar();                                                       // RN-02 (CA-05)

        Cliente cliente = clienteDAO.buscarPorCodigo(prestamo.getClienteCodigo());
        prestamo.validarCliente(cliente,                                          // RN-01 y RN-06 (CA-03)
                prestamoDAO.listarPorCliente(prestamo.getClienteCodigo()));

        TipoPrestamo tipo = prestamoDAO.buscarTipoPrestamo(prestamo.getTipoPrestamoCodigo());
        prestamo.validarCondiciones(tipo);                                        // RN-04 y RN-05 (CA-04, CA-06)

        return prestamoDAO.registrar(prestamo);                                   // CA-02
    }
    /** HU-02 CA-01 / HU-03 CA-01: muestra el formulario con los datos de la base de datos. */
    public void iniciar() {
        try {
            cargarClientesActivos();
            tiposPrestamo = prestamoDAO.listarTiposPrestamo();
            vista.cargarTiposPrestamo(convertirTipos(tiposPrestamo));
            vista.cargarModalidadesPago(catalogoDAO.listarModalidadesPago());
            vista.cargarMonedas(catalogoDAO.listarMonedas());
            vista.cargarGarantias(catalogoDAO.listarGarantias());
            listar();
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudieron cargar los datos del formulario: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void listar() {
        try {
            vista.mostrarPrestamos(prestamoDAO.listar());
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al consultar los préstamos: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Muestra el plazo, la TEA y el monto permitidos para el tipo de préstamo elegido. */
    public void mostrarCondiciones() {
        String codigoTipo = vista.obtenerCodigoTipoPrestamo();
        for (TipoPrestamo tipo : tiposPrestamo) {
            if (tipo.getCodigo().equals(codigoTipo)) {
                vista.mostrarCondiciones("Condiciones de " + tipo.getNombre() + ": plazo de "
                        + tipo.getPlazoMinimo() + " a " + tipo.getPlazoMaximo() + " cuotas, TEA de "
                        + porcentaje(tipo.getTasaMinima()) + " a " + porcentaje(tipo.getTasaMaxima())
                        + " y monto de " + dinero(tipo.getMontoMinimo()) + " a " + dinero(tipo.getMontoMaximo()) + ".");
                return;
            }
        }
        vista.mostrarCondiciones("Seleccione un tipo de préstamo para ver el plazo, la TEA y el monto permitidos.");
    }

    // ------------------------------------------------------------------
    // HU-02: Registrar préstamo financiero
    // ------------------------------------------------------------------
    public void registrar() {
        try {
            Prestamo prestamo = vista.obtenerPrestamoFormulario();
            String codigo = registrar(prestamo);
            vista.mostrarMensaje("El préstamo fue registrado correctamente. Código: " + codigo,
                    "Registro", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (MontoInvalidoException excepcion) {
            // CA-04: mensaje de ERROR indicando que el monto no es válido
            vista.mostrarMensaje(excepcion.getMessage(), "Monto no válido", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException excepcion) {
            // CA-03, CA-05 y CA-06: mensaje de ADVERTENCIA y no se registra
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            // CA-07: error durante la transacción
            vista.mostrarMensaje("No fue posible registrar el préstamo financiero. Detalle: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // HU-03: Generar cronograma de pagos
    // ------------------------------------------------------------------
    public void generarCronograma() {
        try {
            Usuario.validarSesionIniciada(Sesion.getUsuario());
            String codigo = vista.obtenerCodigoPrestamoSeleccionado();
            if (codigo.isEmpty()) {
                throw new IllegalArgumentException(
                        "Seleccione un préstamo registrado de la lista para generar su cronograma.");
            }
            Prestamo prestamo = prestamoDAO.buscarPorCodigo(codigo);
            if (prestamo == null) {
                throw new IllegalArgumentException("El préstamo seleccionado no está registrado.");
            }
            prestamo.validarPuedeGenerarCronograma();                          // RN-01 (CA-05) y RN-02 (CA-04)
            TipoPrestamo tipo = prestamoDAO.buscarTipoPrestamo(prestamo.getTipoPrestamoCodigo());
            prestamo.validarCondiciones(tipo);                                 // RN-08 (CA-04)

            if (!vista.confirmar("¿Desea generar el cronograma de pagos del préstamo " + codigo + "?",
                    "Generar cronograma")) {
                return;
            }
            Cronograma cronograma = prestamo.generarCronograma();              // RN-03 a RN-05 y CA-07
            cronogramaDAO.guardar(cronograma);                                 // RN-06 y RN-09

            vista.mostrarCronograma(cronograma);                               // CA-03 (RN-07)
            vista.mostrarMensaje("El cronograma de pagos fue generado correctamente.",
                    "Cronograma", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException excepcion) {
            // CA-04 y CA-05: advertencia y no se genera
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException | ArithmeticException excepcion) {
            // CA-06 y CA-07: error de cálculo o de consistencia, no se guarda
            vista.mostrarMensaje("Error durante la generación del cronograma: " + excepcion.getMessage(),
                    "Error de cálculo", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al guardar el cronograma (" + excepcion.getMessage()
                    + "). No se guardó el cronograma.", "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    private void cargarClientesActivos() throws SQLException {
        List<ItemCatalogo> clientes = new ArrayList<>();
        for (Cliente cliente : clienteDAO.listar("")) {
            if (cliente.estaActivo()) {                                        // RN-01
                clientes.add(new ItemCatalogo(cliente.getCodigo(), cliente.getCodigo() + " - "
                        + cliente.getApellidos() + ", " + cliente.getNombres()));
            }
        }
        vista.cargarClientes(clientes);
    }

    private List<ItemCatalogo> convertirTipos(List<TipoPrestamo> tipos) {
        List<ItemCatalogo> items = new ArrayList<>();
        for (TipoPrestamo tipo : tipos) {
            items.add(new ItemCatalogo(tipo.getCodigo(), tipo.getNombre()));
        }
        return items;
    }

    private String porcentaje(BigDecimal fraccion) {
        return fraccion.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + " %";
    }

    private String dinero(BigDecimal monto) {
        return monto == null ? "-" : String.format(Locale.US, "S/ %,.2f", monto);
    }
}
