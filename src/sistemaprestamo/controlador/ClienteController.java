package sistemaprestamo.controlador;

import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import sistemaprestamo.modelo.Cliente;
import sistemaprestamo.persistencia.ClienteDAO;
import sistemaprestamo.vista.FrmClientes;

/**
 * Controlador de la ventana de clientes (HU-01).
 * Coordina el flujo: lee los datos de la vista, consulta la persistencia, pide
 * a la clase Cliente que aplique sus reglas de negocio y guarda. No contiene
 * reglas, cálculos ni consultas SQL.
 */
public class ClienteController {

    private final FrmClientes vista;
    private final ClienteDAO clienteDAO;

    public ClienteController(FrmClientes vista) {
        this(vista, new ClienteDAO());
    }

    /** Permite indicar el acceso a datos (útil para probar el flujo sin base de datos). */
    public ClienteController(FrmClientes vista, ClienteDAO clienteDAO) {
        this.vista = vista;
        this.clienteDAO = clienteDAO;
    }

    /** Muestra la lista de clientes al abrir el módulo (HU-01 CA-01). */
    public void iniciar() {
        listar();
    }

    /** HU-01 CA-02, CA-03, CA-05: valida el cliente y su documento y lo registra. */
    public void registrar() {
        try {
            Cliente cliente = vista.obtenerClienteFormulario();
            cliente.validar();
            cliente.validarDocumentoUnico(clienteDAO.buscarPorDocumento(cliente.getNumeroDocumento()));
            String codigo = clienteDAO.registrar(cliente);
            vista.mostrarMensaje("Cliente registrado correctamente. Código: " + codigo,
                    "Registro", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo registrar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** HU-01 CA-04, CA-05 y RN-05: solo se actualiza un cliente registrado. */
    public void actualizar() {
        try {
            Cliente cliente = vista.obtenerClienteFormulario();
            if (cliente.getCodigo().isEmpty()) {
                throw new IllegalArgumentException("Seleccione un cliente de la lista antes de actualizarlo.");
            }
            cliente.validar();
            Cliente.validarRegistrado(clienteDAO.buscarPorCodigo(cliente.getCodigo()));
            cliente.validarDocumentoUnico(clienteDAO.buscarPorDocumento(cliente.getNumeroDocumento()));
            clienteDAO.actualizar(cliente);
            vista.mostrarMensaje("La información del cliente fue actualizada correctamente.",
                    "Actualización", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo actualizar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** HU-01 RN-06 y CA-06: desactiva al cliente solo si no tiene préstamos vigentes. */
    public void desactivar() {
        try {
            String codigo = vista.obtenerCodigoFormulario();
            if (codigo.isEmpty()) {
                throw new IllegalArgumentException("Seleccione un cliente de la lista antes de desactivarlo.");
            }
            Cliente cliente = clienteDAO.buscarPorCodigo(codigo);
            Cliente.validarRegistrado(cliente);
            cliente.validarPuedeDesactivarse(clienteDAO.contarPrestamosVigentes(codigo));

            if (!vista.confirmar("¿Desea desactivar al cliente " + vista.obtenerNombreCompletoFormulario() + "?",
                    "Confirmar desactivación")) {
                return;
            }
            clienteDAO.desactivar(codigo);
            vista.mostrarMensaje("Cliente desactivado correctamente.",
                    "Desactivación", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            listar();
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo desactivar el cliente: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void listar() {
        try {
            List<Cliente> clientes = clienteDAO.listar(vista.obtenerBusqueda());
            vista.mostrarClientes(clientes);
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("Error al consultar los clientes: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
