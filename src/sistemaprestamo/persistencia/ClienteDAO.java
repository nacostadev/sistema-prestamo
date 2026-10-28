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

/**
 * Acceso a datos de clientes. Todas las operaciones se ejecutan mediante los
 * procedimientos almacenados del esquema Cliente (sp_Cliente_xxxx), que solo
 * insertan, actualizan y consultan. Las reglas de negocio están en
 * la clase Cliente (modelo).
 */
public class ClienteDAO {

    private static final String SP_LISTAR = "{call Cliente.sp_Cliente_Listar(?,?)}";
    private static final String SP_REGISTRAR =
            "{call Cliente.sp_Cliente_Registrar(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static final String SP_ACTUALIZAR =
            "{call Cliente.sp_Cliente_Actualizar(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static final String SP_DESACTIVAR = "{call Cliente.sp_Cliente_Desactivar(?)}";

    /** Nombre del estado que define un préstamo activo (HU-01 RN-06). */
    private static final String ESTADO_PRESTAMO_VIGENTE = "Vigente";

    /** Consulta directa: cuenta los préstamos vigentes del cliente (HU-01 RN-06). */
    private static final String SQL_CONTAR_PRESTAMOS_VIGENTES =
            "SELECT COUNT(*) FROM Prestamo.Prestamos AS p "
            + "INNER JOIN Prestamo.EstadosPrestamo AS e ON e.EstadosPrestamoCodigo = p.EstadosPrestamoCodigo "
            + "WHERE p.ClientesCodigo = ? AND e.EstadosPrestamoNombre = ?";

    /** Error de SQL Server por violar una restricción UNIQUE (documento repetido). */
    private static final int ERROR_VALOR_DUPLICADO = 2627;

    /** Registra el cliente y devuelve el código generado por la base de datos. */
    public String registrar(Cliente cliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_REGISTRAR)) {

            // Parámetros 1 a 14: mismos datos y orden que sp_Cliente_Registrar.
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

    /** Actualiza los datos de un cliente registrado (el código va primero). */
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

    /** Desactiva al cliente (cambia su estado a inactivo). La regla de préstamos se valida antes, en Cliente. */
    public void desactivar(String codigoCliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_DESACTIVAR)) {

            sentencia.setString(1, codigoCliente);
            sentencia.execute();
        }
    }

    /** Lista todos los clientes; con un filtro de texto, los que lo contengan en documento, apellidos o nombres. */
    public List<Cliente> listar(String busqueda) throws SQLException {
        return consultar(null, busqueda);
    }

    /** Devuelve el cliente con ese código, o null si no existe. */
    public Cliente buscarPorCodigo(String codigoCliente) throws SQLException {
        List<Cliente> encontrados = consultar(codigoCliente, null);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    /** Devuelve los clientes cuyo número de documento es exactamente el indicado. */
    public List<Cliente> buscarPorDocumento(String numeroDocumento) throws SQLException {
        List<Cliente> exactos = new ArrayList<>();
        for (Cliente cliente : consultar(null, numeroDocumento)) {
            if (cliente.getNumeroDocumento().equals(numeroDocumento)) {
                exactos.add(cliente);
            }
        }
        return exactos;
    }

    /** Cantidad de préstamos vigentes del cliente. */
    public int contarPrestamosVigentes(String codigoCliente) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR_PRESTAMOS_VIGENTES)) {

            sentencia.setString(1, codigoCliente);
            sentencia.setString(2, ESTADO_PRESTAMO_VIGENTE);
            try (ResultSet resultado = sentencia.executeQuery()) {
                resultado.next();
                return resultado.getInt(1);
            }
        }
    }

    /**
     * HU-01 RN-02: la tabla tiene UNIQUE en el número de documento. Si otro
     * usuario registró el mismo documento entre la validación y el guardado,
     * el error se informa como regla de negocio y no como falla técnica.
     */
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

    /**
     * Asigna los 14 datos del cliente a partir de la posición indicada, en el
     * orden de los procedimientos: tipo de documento, número, apellidos,
     * nombres, fecha de nacimiento, estado civil, dirección, teléfono, correo,
     * condición laboral, ingresos, cónyuge, referencias y observaciones.
     */
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
