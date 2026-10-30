    package sistemaprestamo.controlador;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import sistemaprestamo.modelo.Cronograma;
import sistemaprestamo.modelo.CronogramaInconsistenteException;
import sistemaprestamo.modelo.Prestamo;
import sistemaprestamo.modelo.Sesion;
import sistemaprestamo.modelo.Usuario;
import sistemaprestamo.persistencia.CronogramaDAO;
import sistemaprestamo.persistencia.PrestamoDAO;
import sistemaprestamo.persistencia.UsuarioDAO;
import sistemaprestamo.vista.FrmCronogramaPagos;

public class CronogramaController {
    private final FrmCronogramaPagos vista;
    private final PrestamoDAO prestamoDAO;
    private final CronogramaDAO cronogramaDAO;
    private final UsuarioDAO usuarioDAO;

    public CronogramaController(FrmCronogramaPagos vista) {
        this(vista, new PrestamoDAO(), new CronogramaDAO(), new UsuarioDAO());
    }

    public CronogramaController(FrmCronogramaPagos vista, PrestamoDAO prestamoDAO,
                                CronogramaDAO cronogramaDAO, UsuarioDAO usuarioDAO) {
        this.vista = vista;
        this.prestamoDAO = prestamoDAO;
        this.cronogramaDAO = cronogramaDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public void iniciar() {
        try {
            vista.mostrarPrestamos(prestamoDAO.listar());
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudieron cargar los préstamos: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seleccionar() {
        String codigo = vista.obtenerCodigoPrestamo();
        if (codigo.isEmpty()) {
            vista.mostrarPrestamo(null);
            vista.limpiarCronograma();
            return;
        }
        try {
            Prestamo prestamo = prestamoDAO.buscarPorCodigo(codigo);
            vista.mostrarPrestamo(prestamo);
            if (prestamo == null) {
                vista.limpiarCronograma();
                vista.mostrarMensaje("El préstamo no se encuentra registrado.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cronograma guardado = cronogramaDAO.buscarPorPrestamo(prestamo);
            if (guardado == null) {
                vista.limpiarCronograma();
            } else {
                vista.mostrarCronograma(guardado);
            }
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo consultar el préstamo: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void generar() {
        String codigo = vista.obtenerCodigoPrestamo();
        if (codigo.isEmpty()) {
            vista.mostrarMensaje("Seleccione un préstamo registrado para generar su cronograma de pagos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            validarAnalista();
            Prestamo prestamo = prestamoDAO.buscarPorCodigo(codigo);
            if (prestamo == null) {
                vista.mostrarMensaje("El préstamo no se encuentra registrado.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cronograma existente = cronogramaDAO.buscarPorPrestamo(prestamo);
            if (existente != null) {
                vista.mostrarCronograma(existente);
                vista.mostrarMensaje("El préstamo ya tiene un cronograma generado y no puede modificarse.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            prestamo.validarPuedeGenerarCronograma();
            if (!vista.confirmar("¿Desea generar y guardar el cronograma de pagos del préstamo "
                    + prestamo.getCodigo() + "?", "Generar cronograma")) {
                return;
            }

            Cronograma cronograma = prestamo.generarCronograma();
            cronogramaDAO.guardar(cronograma);

            vista.mostrarCronograma(cronograma);
            vista.mostrarMensaje("El cronograma de pagos fue generado correctamente. Código: "
                    + cronograma.getCodigo(), "Cronograma", JOptionPane.INFORMATION_MESSAGE);
        } catch (CronogramaInconsistenteException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Error en el cronograma",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (ArithmeticException | SQLException excepcion) {
            vista.mostrarMensaje("No fue posible generar el cronograma de pagos. Detalle: "
                    + excepcion.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarAnalista() throws SQLException {
        Usuario usuario = Sesion.getUsuario();
        Usuario.validarSesionAutorizada(usuario);
        if (!usuarioDAO.estaAutorizado(usuario)) {
            throw new IllegalArgumentException(
                    "El usuario ya no está activo o no está autorizado como Analista de Crédito.");
        }
    }
    
    public void consultar() {
        String codigo = vista.obtenerCodigoPrestamo();
        if (codigo.isEmpty()) {
            vista.mostrarMensaje("Seleccione un préstamo registrado para consultar su cronograma de pagos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Prestamo prestamo = prestamoDAO.buscarPorCodigo(codigo);
            if (prestamo == null) {
                vista.limpiarCronograma();
                vista.mostrarMensaje("El préstamo no se encuentra registrado.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cronograma guardado = cronogramaDAO.buscarPorPrestamo(prestamo);
            if (guardado == null) {
                vista.limpiarCronograma();
                vista.mostrarMensaje("El préstamo no cuenta con un cronograma de pagos generado.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                vista.mostrarCronograma(guardado);
            }
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo consultar el cronograma de pagos: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}

