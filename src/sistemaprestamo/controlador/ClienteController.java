package sistemaprestamo.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import sistemaprestamo.modelo.Cliente;
import sistemaprestamo.modelo.ClienteService;
import sistemaprestamo.vista.FrmCliente;

/**
 * Controlador de la ventana de clientes.
 * Coordina las acciones de la vista y llama a la lógica de negocio (ClienteService).
 * No contiene consultas SQL.
 */
public class ClienteController implements ActionListener {

    public static final String ACCION_REGISTRAR = "REGISTRAR";
    public static final String ACCION_ACTUALIZAR = "ACTUALIZAR";
    public static final String ACCION_ELIMINAR = "ELIMINAR";
    public static final String ACCION_LISTAR = "LISTAR";
    public static final String ACCION_LIMPIAR = "LIMPIAR";

    private final FrmCliente vista;
    private final ClienteService servicio;

    public ClienteController(FrmCliente vista, ClienteService servicio) {
        this.vista = vista;
        this.servicio = servicio;
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
            case ACCION_ELIMINAR:
                eliminar();
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
            servicio.registrarCliente(cliente);
            vista.mostrarMensaje("Cliente registrado correctamente. ID: " + cliente.getIdCliente(),
                    "Registro", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al registrar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizar() {
        try {
            Cliente cliente = vista.obtenerClienteFormulario();
            servicio.actualizarCliente(cliente);
            vista.mostrarMensaje("Cliente actualizado correctamente.",
                    "Actualización", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al actualizar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminar() {
        try {
            Cliente cliente = vista.obtenerClienteFormulario();
            int confirmacion = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Desea eliminar al cliente " + cliente.getNombres() + " " + cliente.getApellidos() + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            servicio.eliminarCliente(cliente.getIdCliente());
            vista.mostrarMensaje("Cliente eliminado correctamente.",
                    "Eliminación", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al eliminar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void listar() {
        try {
            List<Cliente> clientes = servicio.listarClientes();
            vista.mostrarClientes(clientes);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al consultar los clientes: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
