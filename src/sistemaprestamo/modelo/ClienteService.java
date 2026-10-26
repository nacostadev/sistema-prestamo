package sistemaprestamo.modelo;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import sistemaprestamo.persistencia.ClienteRepository;

/**
 * Lógica de negocio de clientes.
 * Valida los datos y coordina las operaciones mediante ClienteRepository.
 * No contiene consultas SQL ni código Swing.
 */
public class ClienteService {

    private static final String EXPRESION_LETRAS = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$";
    private static final String EXPRESION_DNI = "^\\d{8}$";
    private static final String EXPRESION_CORREO = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";
    private static final String EXPRESION_TELEFONO = "^[0-9+\\-\\s]{6,15}$";

    private final ClienteRepository repositorio;

    public ClienteService() {
        this.repositorio = new ClienteRepository();
    }

    public void registrarCliente(Cliente cliente) throws SQLException {
        validarCliente(cliente);
        repositorio.registrar(cliente);
    }

    public void actualizarCliente(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("No hay datos del cliente para actualizar.");
        }
        if (cliente.getIdCliente() <= 0) {
            throw new IllegalArgumentException("Seleccione un cliente de la lista antes de actualizarlo.");
        }
        validarCliente(cliente);
        int filas = repositorio.actualizar(cliente);
        if (filas == 0) {
            throw new SQLException("No se encontró el cliente con identificador " + cliente.getIdCliente() + ".");
        }
    }

    public void eliminarCliente(int idCliente) throws SQLException {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("Seleccione un cliente de la lista antes de eliminarlo.");
        }
        int filas = repositorio.eliminar(idCliente);
        if (filas == 0) {
            throw new SQLException("No se encontró el cliente con identificador " + idCliente + ".");
        }
    }

    public List<Cliente> listarClientes() throws SQLException {
        return repositorio.listar();
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Los datos del cliente no pueden estar vacíos.");
        }
        validarTextoObligatorio(cliente.getNombres(), "nombres");
        validarTextoObligatorio(cliente.getApellidos(), "apellidos");

        if (cliente.getDni() == null || !cliente.getDni().trim().matches(EXPRESION_DNI)) {
            throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos.");
        }
        if (cliente.getCorreo() == null || !cliente.getCorreo().trim().matches(EXPRESION_CORREO)) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        if (cliente.getTelefono() != null && !cliente.getTelefono().trim().isEmpty()
                && !cliente.getTelefono().trim().matches(EXPRESION_TELEFONO)) {
            throw new IllegalArgumentException("El teléfono solo puede contener dígitos, espacios, '+' y '-' (6 a 15 caracteres).");
        }
    }

    private void validarTextoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        if (valor.trim().length() < 2) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe tener al menos 2 caracteres.");
        }
        if (!valor.trim().matches(EXPRESION_LETRAS)) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' solo debe contener letras y espacios.");
        }
    }
}
