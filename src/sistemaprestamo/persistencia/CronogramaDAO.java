package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.modelo.Cronograma;
import sistemaprestamo.modelo.CronogramaInconsistenteException;
import sistemaprestamo.modelo.DetalleCronograma;
import sistemaprestamo.modelo.Prestamo;


public class CronogramaDAO {

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

    private static final String SQL_CONSULTAR =
            "SELECT c.CronogramasCodigo, d.DetalleNumeroCuota, d.DetalleFechaVencimiento, "
            + "d.DetalleSaldoAnterior, d.DetalleInteres, d.DetalleAmortizacion, d.DetalleCuota, "
            + "d.DetalleSaldoPendiente "
            + "FROM Cronograma.Cronogramas AS c "
            + "INNER JOIN Cronograma.DetalleCronograma AS d ON d.CronogramasCodigo = c.CronogramasCodigo "
            + "WHERE c.PrestamosCodigo = ? ORDER BY d.DetalleNumeroCuota";


    public String guardar(Cronograma cronograma) throws SQLException {
        if (!cronograma.esConsistente()) {
            throw new CronogramaInconsistenteException(
                    "El cronograma no es consistente: el total de amortizaciones no coincide con el monto "
                    + "del préstamo. No se guardó el cronograma.");
        }
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
            if (excepcion.getErrorCode() == ERROR_VALOR_DUPLICADO) {
                throw new IllegalArgumentException(
                        "El préstamo ya tiene un cronograma generado y no puede modificarse.", excepcion);
            }
            throw excepcion;
        }
    }

    public Cronograma buscarPorPrestamo(Prestamo prestamo) throws SQLException {
        String codigoCronograma = null;
        List<DetalleCronograma> cuotas = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR)) {
            sentencia.setString(1, prestamo.getCodigo());
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    codigoCronograma = resultado.getString("CronogramasCodigo").trim();
                    cuotas.add(new DetalleCronograma(
                            resultado.getInt("DetalleNumeroCuota"),
                            resultado.getDate("DetalleFechaVencimiento").toLocalDate(),
                            resultado.getBigDecimal("DetalleSaldoAnterior"),
                            resultado.getBigDecimal("DetalleInteres"),
                            resultado.getBigDecimal("DetalleAmortizacion"),
                            resultado.getBigDecimal("DetalleCuota"),
                            resultado.getBigDecimal("DetalleSaldoPendiente")));
                }
            }
        }
        return codigoCronograma == null ? null : Cronograma.reconstruir(codigoCronograma, prestamo, cuotas);
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
