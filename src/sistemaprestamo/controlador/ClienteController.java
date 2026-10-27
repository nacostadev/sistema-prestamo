package sistemaprestamo.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import sistemaprestamo.modelo.CatalogosCliente;
import sistemaprestamo.modelo.Cliente;
import sistemaprestamo.modelo.ClienteService;
import sistemaprestamo.modelo.ReglaNegocioException;
import sistemaprestamo.vista.FrmCliente;

/**
 * Controlador de la ventana de clientes.
 * Coordina las acciones de la vista y llama a la lógica de negocio (ClienteService).
 * No contiene consultas SQL.
 */
public class ClienteController implements ActionListener {

    public static final String ACCION_REGISTRAR = "REGISTRAR";
    public static final String ACCION_ACTUALIZAR = "ACTUALIZAR";
    public static final String ACCION_DESACTIVAR = "DESACTIVAR";
    public static final String ACCION_LISTAR = "LISTAR";
    public static final String ACCION_LIMPIAR = "LIMPIAR";

    private final FrmCliente vista;
    private final ClienteService servicio;

    public ClienteController(FrmCliente vista, ClienteService servicio) {
        this.vista = vista;
        this.servicio = servicio;
    }

    /** Carga los combos y muestra la lista de clientes al abrir el módulo (HU-01 CA-01). */
    public void iniciar() {
        try {
            CatalogosCliente catalogos = servicio.cargarCatalogos();
            vista.cargarCatalogos(catalogos);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al cargar los catálogos: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
        listar();
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        switch (evento.getActionCommand()) {
            case ACCION_REGISTRAR:
                registrar();
                break;
            case ACCION_ACTUALIZAR:
                actualizar();
                break;
            case ACCION_DESACTIVAR:
                desactivar();
                break;
            case ACCION_LISTAR:
                listar();
                break;
            case ACCION_LIMPIAR:
                vista.limpiarFormulario();
                break;
            default:
                break;
        }
    }

    public void registrar() {
        try {
            Cliente cliente = vista.obtenerClienteFormulario();
            String codigo = servicio.registrarCliente(cliente);
            vista.mostrarMensaje("Cliente registrado correctamente. Código: " + codigo,
                    "Registro", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException | ReglaNegocioException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo registrar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizar() {
        try {
            Cliente cliente = vista.obtenerClienteFormulario();
            servicio.actualizarCliente(cliente);
            vista.mostrarMensaje("La información del cliente fue actualizada correctamente.",
                    "Actualización", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException | ReglaNegocioException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo actualizar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void desactivar() {
        try {
            String codigo = vista.obtenerCodigoFormulario();
            if (codigo.isEmpty()) {
                throw new IllegalArgumentException("Seleccione un cliente de la lista antes de desactivarlo.");
            }
            int confirmacion = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Desea desactivar al cliente " + vista.obtenerNombreCompletoFormulario() + "?",
                    "Confirmar desactivación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            servicio.desactivarCliente(codigo);
            vista.mostrarMensaje("Cliente desactivado correctamente.",
                    "Desactivación", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException | ReglaNegocioException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo desactivar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void listar() {
        try {
            List<Cliente> clientes = servicio.listarClientes(vista.obtenerBusqueda());
            vista.mostrarClientes(clientes);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al consultar los clientes: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
