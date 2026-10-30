package sistemaprestamo.persistencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.modelo.Cliente;


public class ClienteDAO {

    private static final String SP_LISTAR = "{call Cliente.sp_Cliente_Listar(?,?)}";
    private static final String SP_REGISTRAR =
            "{call Cliente.sp_Cliente_Registrar(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static final String SP_ACTUALIZAR =
            "{call Cliente.sp_Cliente_Actualizar(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static final String SP_DESACTIVAR = "{call Cliente.sp_Cliente_Desactivar(?)}";

    private static final String SQL_ESTADOS_PRESTAMOS =
            "SELECT EstadosPrestamoCodigo FROM Prestamo.Prestamos WHERE ClientesCodigo = ?";

    private static final int ERROR_VALOR_DUPLICADO = 2627;

    public String registrar(Cliente cliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_REGISTRAR)) {

            asignarDatos(sentencia, cliente, 1);
            sentencia.registerOutParameter(15, Types.NCHAR);
            sentencia.execute();

            String codigo = sentencia.getString(15).trim();
            cliente.setCodigo(codigo);
            return codigo;
        } catch (SQLException excepcion) {
            throw traducirDocumentoDuplicado(excepcion);
        }
    }

    public void actualizar(Cliente cliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_ACTUALIZAR)) {

            sentencia.setString(1, cliente.getCodigo());
            asignarDatos(sentencia, cliente, 2);
            sentencia.execute();
        } catch (SQLException excepcion) {
            throw traducirDocumentoDuplicado(excepcion);
        }
    }

    public void desactivar(String codigoCliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_DESACTIVAR)) {

            sentencia.setString(1, codigoCliente);
            sentencia.execute();
        }
    }

    public List<Cliente> listar(String busqueda) throws SQLException {
        return consultar(null, busqueda);
    }

    public Cliente buscarPorCodigo(String codigoCliente) throws SQLException {
        List<Cliente> encontrados = consultar(codigoCliente, null);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    public List<Cliente> buscarPorDocumento(String numeroDocumento) throws SQLException {
        List<Cliente> exactos = new ArrayList<>();
        for (Cliente cliente : consultar(null, numeroDocumento)) {
            if (cliente.getNumeroDocumento().equals(numeroDocumento)) {
                exactos.add(cliente);
            }
        }
        return exactos;
    }

    /**
     * Código de estado de cada préstamo del cliente. La regla de qué estados impiden
     * desactivar al cliente se aplica en Java (Cliente.validarPuedeDesactivarse).
     */
    public List<String> listarEstadosPrestamos(String codigoCliente) throws SQLException {
        List<String> estados = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ESTADOS_PRESTAMOS)) {

            sentencia.setString(1, codigoCliente);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    estados.add(resultado.getString(1).trim());
                }
            }
        }
        return estados;
    }


    private SQLException traducirDocumentoDuplicado(SQLException excepcion) {
        if (excepcion.getErrorCode() == ERROR_VALOR_DUPLICADO) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con ese número de documento.", excepcion);
        }
        return excepcion;
    }

    private List<Cliente> consultar(String codigoCliente, String busqueda) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_LISTAR)) {

            asignarOpcional(sentencia, 1, codigoCliente);
            asignarOpcional(sentencia, 2, busqueda);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    clientes.add(mapearCliente(resultado));
                }
            }
        }
        return clientes;
    }


    private void asignarDatos(CallableStatement sentencia, Cliente cliente, int inicio)
            throws SQLException {
        int i = inicio;
        sentencia.setString(i++, cliente.getTipoDocumentoCodigo());
        sentencia.setString(i++, cliente.getNumeroDocumento());
        sentencia.setString(i++, cliente.getApellidos());
        sentencia.setString(i++, cliente.getNombres());
        sentencia.setDate(i++, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
        sentencia.setString(i++, cliente.getEstadoCivilCodigo());
        sentencia.setString(i++, cliente.getDireccion());
        sentencia.setString(i++, cliente.getTelefono());
        sentencia.setString(i++, cliente.getCorreo());
        sentencia.setString(i++, cliente.getCondicionLaboral());
        sentencia.setBigDecimal(i++, cliente.getNivelIngresos());
        asignarOpcional(sentencia, i++, cliente.getNombreConyuge());
        asignarOpcional(sentencia, i++, cliente.getReferenciasPersonales());
        asignarOpcional(sentencia, i, cliente.getObservaciones());
    }

    private void asignarOpcional(CallableStatement sentencia, int posicion, String valor)
            throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            sentencia.setNull(posicion, Types.NVARCHAR);
        } else {
            sentencia.setString(posicion, valor.trim());
        }
    }

    private Cliente mapearCliente(ResultSet resultado) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setCodigo(resultado.getString("ClientesCodigo").trim());
        cliente.setTipoDocumentoCodigo(resultado.getString("TiposDocumentoCodigo").trim());
        cliente.setTipoDocumentoNombre(resultado.getString("TiposDocumentoNombre"));
        cliente.setNumeroDocumento(resultado.getString("ClientesNumeroDocumento"));
        cliente.setApellidos(resultado.getString("ClientesApellidos"));
        cliente.setNombres(resultado.getString("ClientesNombres"));
        cliente.setFechaNacimiento(resultado.getDate("ClientesFechaNacimiento").toLocalDate());
        cliente.setEstadoCivilCodigo(resultado.getString("EstadosCivilesCodigo").trim());
        cliente.setEstadoCivilNombre(resultado.getString("EstadosCivilesNombre"));
        cliente.setDireccion(resultado.getString("ClientesDireccion"));
        cliente.setTelefono(resultado.getString("ClientesTelefono").trim());
        cliente.setCorreo(resultado.getString("ClientesCorreo"));
        cliente.setCondicionLaboral(resultado.getString("ClientesCondicionLaboral"));
        cliente.setNivelIngresos(resultado.getBigDecimal("ClientesNivelIngresos"));
        cliente.setNombreConyuge(resultado.getString("ClientesNombreConyuge"));
        cliente.setReferenciasPersonales(resultado.getString("ClientesReferenciasPersonales"));
        cliente.setObservaciones(resultado.getString("ClientesObservaciones"));
        cliente.setEstado(resultado.getString("ClientesEstado").trim());
        return cliente;
    }
}
