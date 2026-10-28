package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import sistemaprestamo.modelo.Cronograma;
import sistemaprestamo.modelo.DetalleCronograma;

/**
 * Acceso a datos de cronogramas con SQL directo (INSERT).
 * Los cálculos y las reglas de negocio están en las clases Prestamo y Cronograma.
 */
public class CronogramaDAO {

    /** Error de SQL Server por violar una restricción UNIQUE. */
    private static final int ERROR_VALOR_DUPLICADO = 2627;

    private static final String SQL_MAXIMO_CRONOGRAMA = "SELECT MAX(CronogramasCodigo) FROM Cronograma.Cronogramas";
    private static final String SQL_MAXIMO_DETALLE =
            "SELECT MAX(DetalleCronogramaCodigo) FROM Cronograma.DetalleCronograma";

    private static final String SQL_INSERTAR_CRONOGRAMA =
            "INSERT INTO Cronograma.Cronogramas (CronogramasCodigo, PrestamosCodigo) VALUES (?, ?)";

    private static final String SQL_INSERTAR_DETALLE =
            "INSERT INTO Cronograma.DetalleCronograma "
            + "(DetalleCronogramaCodigo, CronogramasCodigo, DetalleNumeroCuota, DetalleFechaVencimiento, "
            + "DetalleSaldoAnterior, DetalleInteres, DetalleAmortizacion, DetalleCuota, DetalleSaldoPendiente) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Guarda la cabecera y todas las cuotas del cronograma en una sola
     * transacción: si algo falla no se guarda nada (HU-03 CA-06).
     * Asigna el código generado (CR0001...) al cronograma y lo devuelve.
     */
    public String guardar(Cronograma cronograma) throws SQLException {
        try (Connection conexion = Conexion.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                String codigoCronograma = Conexion.siguienteCodigo(conexion, SQL_MAXIMO_CRONOGRAMA, "CR", 4);
                insertarCabecera(conexion, codigoCronograma, cronograma.getPrestamo().getCodigo());
                insertarDetalle(conexion, codigoCronograma, cronograma);
                conexion.commit();
                cronograma.setCodigo(codigoCronograma);
                return codigoCronograma;
            } catch (SQLException | RuntimeException excepcion) {
                conexion.rollback();
                throw excepcion;
            }
        } catch (SQLException excepcion) {
            // HU-03 RN-09: la tabla solo admite un cronograma por préstamo (UNIQUE).
            if (excepcion.getErrorCode() == ERROR_VALOR_DUPLICADO) {
                throw new IllegalArgumentException(
                        "El préstamo ya tiene un cronograma generado y no puede modificarse.", excepcion);
            }
            throw excepcion;
        }
    }

    private void insertarCabecera(Connection conexion, String codigoCronograma, String codigoPrestamo)
            throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR_CRONOGRAMA)) {
            sentencia.setString(1, codigoCronograma);
            sentencia.setString(2, codigoPrestamo);
            sentencia.executeUpdate();
        }
    }

    private void insertarDetalle(Connection conexion, String codigoCronograma, Cronograma cronograma)
            throws SQLException {
        String ultimoCodigo = Conexion.siguienteCodigo(conexion, SQL_MAXIMO_DETALLE, "DC", 6);
        int numero = Integer.parseInt(ultimoCodigo.substring(2));

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR_DETALLE)) {
            for (DetalleCronograma cuota : cronograma.getDetalle()) {
                sentencia.setString(1, String.format("DC%06d", numero++));
                sentencia.setString(2, codigoCronograma);
                sentencia.setInt(3, cuota.getNumeroCuota());
                sentencia.setDate(4, java.sql.Date.valueOf(cuota.getFechaVencimiento()));
                sentencia.setBigDecimal(5, cuota.getSaldoAnterior());
                sentencia.setBigDecimal(6, cuota.getInteres());
                sentencia.setBigDecimal(7, cuota.getAmortizacion());
                sentencia.setBigDecimal(8, cuota.getCuota());
                sentencia.setBigDecimal(9, cuota.getSaldoPendiente());
                sentencia.addBatch();
            }
            sentencia.executeBatch();
        }
    }
}
