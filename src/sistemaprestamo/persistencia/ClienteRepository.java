package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.modelo.Cliente;

/**
 * Acceso a datos de la tabla clientes mediante JDBC.
 * Supone que la tabla ya existe en PostgreSQL con la siguiente estructura:
 * id_cliente (SERIAL), nombres, apellidos, dni, telefono, correo y direccion.
 * No crea tablas ni ejecuta scripts de inicialización.
 */
public class ClienteRepository {

    private static final String SQL_REGISTRAR =
            "INSERT INTO clientes (nombres, apellidos, dni, telefono, correo, direccion) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE clientes SET nombres = ?, apellidos = ?, dni = ?, telefono = ?, correo = ?, direccion = ? "
            + "WHERE id_cliente = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM clientes WHERE id_cliente = ?";

    private static final String SQL_LISTAR =
            "SELECT id_cliente, nombres, apellidos, dni, telefono, correo, direccion "
            + "FROM clientes ORDER BY id_cliente";

    public int registrar(Cliente cliente) throws SQLException {
        int filasAfectadas;
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_REGISTRAR, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, cliente.getNombres());
            sentencia.setString(2, cliente.getApellidos());
            sentencia.setString(3, cliente.getDni());
            sentencia.setString(4, cliente.getTelefono());
            sentencia.setString(5, cliente.getCorreo());
            sentencia.setString(6, cliente.getDireccion());

            filasAfectadas = sentencia.executeUpdate();

            try (ResultSet clavesGeneradas = sentencia.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    cliente.setIdCliente(clavesGeneradas.getInt(1));
                }
            }
        }
        return filasAfectadas;
    }

    public int actualizar(Cliente cliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            sentencia.setString(1, cliente.getNombres());
            sentencia.setString(2, cliente.getApellidos());
            sentencia.setString(3, cliente.getDni());
            sentencia.setString(4, cliente.getTelefono());
            sentencia.setString(5, cliente.getCorreo());
            sentencia.setString(6, cliente.getDireccion());
            sentencia.setInt(7, cliente.getIdCliente());

            return sentencia.executeUpdate();
        }
    }

    public int eliminar(int idCliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, idCliente);
            return sentencia.executeUpdate();
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(resultado.getInt("id_cliente"));
                cliente.setNombres(resultado.getString("nombres"));
                cliente.setApellidos(resultado.getString("apellidos"));
                cliente.setDni(resultado.getString("dni"));
                cliente.setTelefono(resultado.getString("telefono"));
                cliente.setCorreo(resultado.getString("correo"));
                cliente.setDireccion(resultado.getString("direccion"));
                clientes.add(cliente);
            }
        }
        return clientes;
    }
}
