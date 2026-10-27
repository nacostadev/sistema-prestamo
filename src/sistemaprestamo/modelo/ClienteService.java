package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import sistemaprestamo.persistencia.CatalogoRepository;
import sistemaprestamo.persistencia.ClienteRepository;
import sistemaprestamo.persistencia.PrestamoRepository;

/**
 * Lógica de negocio de clientes (HU-01).
 * Valida los campos obligatorios y sus formatos y aplica las reglas de negocio
 * (documento único, cliente registrado, cliente sin préstamos) antes de llamar
 * a los repositorios, cuyos procedimientos almacenados solo hacen CRUD.
 * No contiene consultas SQL ni código Swing.
 */
public class ClienteService {

    /** Código de "DNI" en Cliente.TiposDocumento (datos iniciales del script). */
    private static final String TIPO_DOCUMENTO_DNI = "TD01";

    private static final String EXPRESION_LETRAS = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$";
    private static final String EXPRESION_DNI = "^\\d{8}$";
    private static final String EXPRESION_OTRO_DOCUMENTO = "^[A-Za-z0-9]{6,12}$";
    private static final String EXPRESION_CORREO = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";
    private static final String EXPRESION_TELEFONO = "^\\d{9}$";

    private final ClienteRepository repositorio;
    private final PrestamoRepository prestamoRepositorio;
    private final CatalogoRepository catalogoRepositorio;

    public ClienteService() {
        this.repositorio = new ClienteRepository();
        this.prestamoRepositorio = new PrestamoRepository();
        this.catalogoRepositorio = new CatalogoRepository();
    }

    /** Registra un cliente (HU-01 CA-02, CA-03) y devuelve el código asignado. */
    public String registrarCliente(Cliente cliente) throws SQLException {
        validarCliente(cliente);
        verificarDocumentoDisponible(cliente.getNumeroDocumento(), null);
        return repositorio.registrar(cliente);
    }

    /** Actualiza un cliente ya registrado (HU-01 RN-05, CA-04). */
    public void actualizarCliente(Cliente cliente) throws SQLException {
        if (cliente == null || esVacio(cliente.getCodigo())) {
            throw new IllegalArgumentException("Seleccione un cliente de la lista antes de actualizarlo.");
        }
        validarCliente(cliente);
        exigirClienteRegistrado(cliente.getCodigo());
        verificarDocumentoDisponible(cliente.getNumeroDocumento(), cliente.getCodigo());
        repositorio.actualizar(cliente);
    }

    /**
     * Desactiva un cliente sin eliminarlo (HU-01 RN-06, CA-06).
     * SUPUESTO: las HU no definen "préstamo activo" y el sistema no tiene
     * estados como Cancelado o Pagado, así que se bloquea si el cliente tiene
     * CUALQUIER préstamo asociado.
     */
    public void desactivarCliente(String codigoCliente) throws SQLException {
        if (esVacio(codigoCliente)) {
            throw new IllegalArgumentException("Seleccione un cliente de la lista antes de desactivarlo.");
        }
        exigirClienteRegistrado(codigoCliente);
        if (!prestamoRepositorio.listarPorCliente(codigoCliente).isEmpty()) {
            throw new ReglaNegocioException(
                    "No se puede desactivar al cliente porque tiene préstamos asociados.");
        }
        repositorio.desactivar(codigoCliente);
    }

    /** Lista los clientes, con filtro opcional (HU-01 CA-01). */
    public List<Cliente> listarClientes(String busqueda) throws SQLException {
        return repositorio.listar(busqueda);
    }

    public CatalogosCliente cargarCatalogos() throws SQLException {
        return new CatalogosCliente(
                catalogoRepositorio.listarTiposDocumento(),
                catalogoRepositorio.listarEstadosCiviles(),
                catalogoRepositorio.listarCondicionesLaborales());
    }

    /** HU-01 RN-05: solo se opera sobre clientes registrados. */
    private void exigirClienteRegistrado(String codigoCliente) throws SQLException {
        if (repositorio.buscarPorCodigo(codigoCliente) == null) {
            throw new ReglaNegocioException("El cliente no está registrado en el sistema.");
        }
    }

    /** HU-01 RN-02: el número de documento no puede repetirse en otro cliente. */
    private void verificarDocumentoDisponible(String numeroDocumento, String codigoActual)
            throws SQLException {
        for (Cliente existente : repositorio.buscarPorDocumento(numeroDocumento)) {
            if (!existente.getCodigo().equals(codigoActual)) {
                throw new ReglaNegocioException(
                        "Ya existe un cliente registrado con ese número de documento.");
            }
        }
    }

    /** Valida campos obligatorios (RN-03) y sus formatos (CA-05). */
    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Los datos del cliente no pueden estar vacíos.");
        }

        StringBuilder faltantes = new StringBuilder();
        agregarSiVacio(faltantes, cliente.getTipoDocumentoCodigo(), "tipo de documento");
        agregarSiVacio(faltantes, cliente.getNumeroDocumento(), "número de documento");
        agregarSiVacio(faltantes, cliente.getNombres(), "nombres");
        agregarSiVacio(faltantes, cliente.getApellidos(), "apellidos");
        if (cliente.getFechaNacimiento() == null) {
            agregarFaltante(faltantes, "fecha de nacimiento");
        }
        agregarSiVacio(faltantes, cliente.getEstadoCivilCodigo(), "estado civil");
        agregarSiVacio(faltantes, cliente.getDireccion(), "dirección");
        agregarSiVacio(faltantes, cliente.getTelefono(), "teléfono");
        agregarSiVacio(faltantes, cliente.getCorreo(), "correo electrónico");
        agregarSiVacio(faltantes, cliente.getCondicionLaboralCodigo(), "condición laboral");
        if (cliente.getNivelIngresos() == null) {
            agregarFaltante(faltantes, "nivel de ingresos");
        }
        if (faltantes.length() > 0) {
            throw new IllegalArgumentException("Complete los campos obligatorios: " + faltantes + ".");
        }

        normalizar(cliente);

        validarDocumento(cliente);
        validarTexto(cliente.getNombres(), "nombres", 100);
        validarTexto(cliente.getApellidos(), "apellidos", 100);
        validarFechaNacimiento(cliente.getFechaNacimiento());
        validarLongitud(cliente.getDireccion(), "dirección", 150);
        if (!cliente.getTelefono().matches(EXPRESION_TELEFONO)) {
            throw new IllegalArgumentException("El teléfono debe contener exactamente 9 dígitos.");
        }
        if (cliente.getCorreo().length() > 100 || !cliente.getCorreo().matches(EXPRESION_CORREO)) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        if (cliente.getNivelIngresos().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El nivel de ingresos no puede ser negativo.");
        }
        validarLongitud(cliente.getNombreConyuge(), "nombre del cónyuge", 200);
        validarLongitud(cliente.getReferenciasPersonales(), "referencias personales", 255);
        validarLongitud(cliente.getObservaciones(), "observaciones", 255);
    }

    private void validarDocumento(Cliente cliente) {
        String documento = cliente.getNumeroDocumento();
        if (TIPO_DOCUMENTO_DNI.equals(cliente.getTipoDocumentoCodigo())) {
            if (!documento.matches(EXPRESION_DNI)) {
                throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos.");
            }
        } else if (!documento.matches(EXPRESION_OTRO_DOCUMENTO)) {
            throw new IllegalArgumentException(
                    "El número de documento debe ser alfanumérico, de 6 a 12 caracteres.");
        }
    }

    private void validarTexto(String valor, String campo, int maximo) {
        if (valor.length() < 2) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe tener al menos 2 caracteres.");
        }
        validarLongitud(valor, campo, maximo);
        if (!valor.matches(EXPRESION_LETRAS)) {
            throw new IllegalArgumentException("El campo '" + campo + "' solo debe contener letras y espacios.");
        }
    }

    private void validarFechaNacimiento(LocalDate fecha) {
        if (fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
    }

    private void validarLongitud(String valor, String campo, int maximo) {
        if (valor != null && valor.length() > maximo) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' no puede superar los " + maximo + " caracteres.");
        }
    }

    /** Quita espacios sobrantes de los textos antes de validarlos y guardarlos. */
    private void normalizar(Cliente cliente) {
        cliente.setNumeroDocumento(cliente.getNumeroDocumento().trim());
        cliente.setNombres(cliente.getNombres().trim());
        cliente.setApellidos(cliente.getApellidos().trim());
        cliente.setDireccion(cliente.getDireccion().trim());
        cliente.setTelefono(cliente.getTelefono().trim());
        cliente.setCorreo(cliente.getCorreo().trim());
        cliente.setNombreConyuge(recortarOpcional(cliente.getNombreConyuge()));
        cliente.setReferenciasPersonales(recortarOpcional(cliente.getReferenciasPersonales()));
        cliente.setObservaciones(recortarOpcional(cliente.getObservaciones()));
    }

    private String recortarOpcional(String valor) {
        return valor == null ? null : valor.trim();
    }

    private void agregarSiVacio(StringBuilder faltantes, String valor, String campo) {
        if (esVacio(valor)) {
            agregarFaltante(faltantes, campo);
        }
    }

    private void agregarFaltante(StringBuilder faltantes, String campo) {
        if (faltantes.length() > 0) {
            faltantes.append(", ");
        }
        faltantes.append(campo);
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
