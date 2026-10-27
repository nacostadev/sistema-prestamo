package sistemaprestamo.persistencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import sistemaprestamo.modelo.DetalleCronograma;

/**
 * Acceso a datos de cronogramas mediante los procedimientos almacenados del
 * esquema Cronograma (sp_Cronograma_xxxx), que solo insertan.
 * Los cálculos y las reglas de negocio están en CronogramaService.
 */
public class CronogramaRepository {

    private static final String SP_REGISTRAR = "{call Cronograma.sp_Cronograma_Registrar(?,?)}";
    private static final String SP_REGISTRAR_DETALLE =
            "{call Cronograma.sp_Cronograma_RegistrarDetalle(?,?,?,?,?,?,?,?)}";

    /**
     * Guarda la cabecera y todas las cuotas del cronograma en una sola
     * transacción: si algo falla no se guarda nada (HU-03 CA-06).
     * Devuelve el código del cronograma.
     */
    public String guardar(String codigoPrestamo, List<DetalleCronograma> detalle) throws SQLException {
        try (Connection conexion = Conexion.getConnection()) {
            conexion.setAutoCommit(false);
            try (CallableStatement cabecera = conexion.prepareCall(SP_REGISTRAR);
                 CallableStatement cuotas = conexion.prepareCall(SP_REGISTRAR_DETALLE)) {

                cabecera.setString(1, codigoPrestamo);
                cabecera.registerOutParameter(2, Types.NCHAR);
                cabecera.execute();
                String codigoCronograma = cabecera.getString(2).trim();

                for (DetalleCronograma cuota : detalle) {
                    cuotas.setString(1, codigoCronograma);
                    cuotas.setInt(2, cuota.getNumeroCuota());
                    cuotas.setDate(3, java.sql.Date.valueOf(cuota.getFechaVencimiento()));
                    cuotas.setBigDecimal(4, cuota.getSaldoAnterior());
                    cuotas.setBigDecimal(5, cuota.getInteres());
                    cuotas.setBigDecimal(6, cuota.getAmortizacion());
                    cuotas.setBigDecimal(7, cuota.getCuota());
                    cuotas.setBigDecimal(8, cuota.getSaldoPendiente());
                    cuotas.addBatch();
                }
                cuotas.executeBatch();

                conexion.commit();
                return codigoCronograma;
            } catch (SQLException | RuntimeException excepcion) {
                conexion.rollback();
                throw excepcion;
            }
        } catch (SQLException excepcion) {
            throw TraductorErrores.traducir(excepcion);
        }
    }
}
