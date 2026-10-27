package sistemaprestamo.persistencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.modelo.Prestamo;

/**
 * Acceso a datos de préstamos mediante los procedimientos almacenados del
 * esquema Prestamo (sp_Prestamo_xxxx), que solo insertan y consultan. Las
 * reglas de negocio están en PrestamoService.
 */
public class PrestamoRepository {

    private static final String SP_REGISTRAR =
            "{call Prestamo.sp_Prestamo_Registrar(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static final String SP_LISTAR = "{call Prestamo.sp_Prestamo_Listar(?,?)}";

    /** Registra el préstamo con el estado indicado en el objeto y devuelve el código generado. */
    public String registrar(Prestamo prestamo) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_REGISTRAR)) {

            sentencia.setString(1, prestamo.getClienteCodigo());
            sentencia.setString(2, prestamo.getTipoPrestamoCodigo());
            sentencia.setBigDecimal(3, prestamo.getMontoSolicitado());
            sentencia.setInt(4, prestamo.getPlazoCuotas());
            sentencia.setBigDecimal(5, prestamo.getTea());
            sentencia.setString(6, prestamo.getModalidadPagoCodigo());
            sentencia.setDate(7, java.sql.Date.valueOf(prestamo.getFechaDesembolso()));
            sentencia.setString(8, prestamo.getMonedaCodigo());
            asignarOpcional(sentencia, 9, prestamo.getGarantiaCodigo(), Types.NCHAR);
            asignarOpcional(sentencia, 10, prestamo.getObservaciones(), Types.NVARCHAR);
            asignarOpcional(sentencia, 11, prestamo.getDescripcionDestino(), Types.NVARCHAR);
            sentencia.setString(12, prestamo.getEstadoCodigo());
            sentencia.registerOutParameter(13, Types.NCHAR);
            sentencia.execute();

            String codigo = sentencia.getString(13).trim();
            prestamo.setCodigo(codigo);
            return codigo;
        } catch (SQLException excepcion) {
            throw TraductorErrores.traducir(excepcion);
        }
    }

    /** Lista todos los préstamos. */
    public List<Prestamo> listar() throws SQLException {
        return consultar(null, null);
    }

    /** Devuelve el préstamo con ese código, o null si no existe. */
    public Prestamo buscarPorCodigo(String codigoPrestamo) throws SQLException {
        List<Prestamo> encontrados = consultar(codigoPrestamo, null);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    /** Lista los préstamos de un cliente, en cualquier estado. */
    public List<Prestamo> listarPorCliente(String codigoCliente) throws SQLException {
        return consultar(null, codigoCliente);
    }

    private List<Prestamo> consultar(String codigoPrestamo, String codigoCliente) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             CallableStatement sentencia = conexion.prepareCall(SP_LISTAR)) {

            asignarOpcional(sentencia, 1, codigoPrestamo, Types.NCHAR);
            asignarOpcional(sentencia, 2, codigoCliente, Types.NCHAR);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    prestamos.add(mapearPrestamo(resultado));
                }
            }
        } catch (SQLException excepcion) {
            throw TraductorErrores.traducir(excepcion);
        }
        return prestamos;
    }

    private void asignarOpcional(CallableStatement sentencia, int posicion, String valor, int tipo)
            throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            sentencia.setNull(posicion, tipo);
        } else {
            sentencia.setString(posicion, valor.trim());
        }
    }

    private Prestamo mapearPrestamo(ResultSet resultado) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setCodigo(resultado.getString("PrestamosCodigo").trim());
        prestamo.setClienteCodigo(resultado.getString("ClientesCodigo").trim());
        prestamo.setClienteNombre(resultado.getString("ClientesNombres") + " "
                + resultado.getString("ClientesApellidos"));
        prestamo.setTipoPrestamoCodigo(resultado.getString("TiposPrestamoCodigo").trim());
        prestamo.setTipoPrestamoNombre(resultado.getString("TiposPrestamoNombre"));
        prestamo.setMontoSolicitado(resultado.getBigDecimal("PrestamosMontoSolicitado"));
        prestamo.setPlazoCuotas(resultado.getInt("PrestamosPlazoCuotas"));
        prestamo.setTea(resultado.getBigDecimal("PrestamosTEA"));
        prestamo.setModalidadPagoCodigo(resultado.getString("ModalidadesPagoCodigo").trim());
        prestamo.setModalidadPagoNombre(resultado.getString("ModalidadesPagoNombre"));
        prestamo.setFechaDesembolso(resultado.getDate("PrestamosFechaDesembolso").toLocalDate());
        prestamo.setMonedaCodigo(resultado.getString("MonedasCodigo").trim());
        prestamo.setMonedaSimbolo(resultado.getString("MonedasSimbolo"));
        String garantia = resultado.getString("GarantiasCodigo");
        prestamo.setGarantiaCodigo(garantia == null ? null : garantia.trim());
        prestamo.setObservaciones(resultado.getString("PrestamosObservaciones"));
        prestamo.setDescripcionDestino(resultado.getString("PrestamosDescripcionDestino"));
        prestamo.setEstadoCodigo(resultado.getString("EstadosPrestamoCodigo").trim());
        prestamo.setEstadoNombre(resultado.getString("EstadosPrestamoNombre"));
        return prestamo;
    }
}
