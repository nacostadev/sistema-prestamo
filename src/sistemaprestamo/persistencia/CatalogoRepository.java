package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.modelo.ItemCatalogo;
import sistemaprestamo.modelo.TipoPrestamo;

/**
 * Lectura de los catálogos (tablas de apoyo) que usan los combos y las
 * validaciones. Son consultas de solo lectura sobre tablas existentes.
 */
public class CatalogoRepository {

    private static final String SQL_TIPOS_DOCUMENTO =
            "SELECT TiposDocumentoCodigo, TiposDocumentoNombre FROM Cliente.TiposDocumento ORDER BY 1";
    private static final String SQL_ESTADOS_CIVILES =
            "SELECT EstadosCivilesCodigo, EstadosCivilesNombre FROM Cliente.EstadosCiviles ORDER BY 1";
    private static final String SQL_CONDICIONES_LABORALES =
            "SELECT CondicionesLaboralesCodigo, CondicionesLaboralesNombre "
            + "FROM Cliente.CondicionesLaborales ORDER BY 1";
    private static final String SQL_MODALIDADES_PAGO =
            "SELECT ModalidadesPagoCodigo, ModalidadesPagoNombre FROM Prestamo.ModalidadesPago ORDER BY 1";
    private static final String SQL_MONEDAS =
            "SELECT MonedasCodigo, MonedasNombre FROM Prestamo.Monedas ORDER BY 1";
    private static final String SQL_GARANTIAS =
            "SELECT GarantiasCodigo, GarantiasDescripcion FROM Prestamo.Garantias ORDER BY 1";
    private static final String SQL_ESTADOS_PRESTAMO =
            "SELECT EstadosPrestamoCodigo, EstadosPrestamoNombre FROM Prestamo.EstadosPrestamo ORDER BY 1";
    private static final String SQL_TIPOS_PRESTAMO =
            "SELECT t.TiposPrestamoCodigo, t.TiposPrestamoNombre, "
            + "t.TiposPrestamoPlazoMinimo, t.TiposPrestamoPlazoMaximo, "
            + "t.TiposPrestamoTasaMinima, t.TiposPrestamoTasaMaxima, "
            + "p.PoliticaCreditoMontoMinimo, p.PoliticaCreditoMontoMaximo "
            + "FROM Prestamo.TiposPrestamo AS t "
            + "LEFT JOIN Prestamo.PoliticaCredito AS p ON p.TiposPrestamoCodigo = t.TiposPrestamoCodigo "
            + "ORDER BY t.TiposPrestamoCodigo";

    public List<ItemCatalogo> listarTiposDocumento() throws SQLException {
        return leerItems(SQL_TIPOS_DOCUMENTO);
    }

    public List<ItemCatalogo> listarEstadosCiviles() throws SQLException {
        return leerItems(SQL_ESTADOS_CIVILES);
    }

    public List<ItemCatalogo> listarCondicionesLaborales() throws SQLException {
        return leerItems(SQL_CONDICIONES_LABORALES);
    }

    public List<ItemCatalogo> listarModalidadesPago() throws SQLException {
        return leerItems(SQL_MODALIDADES_PAGO);
    }

    public List<ItemCatalogo> listarMonedas() throws SQLException {
        return leerItems(SQL_MONEDAS);
    }

    public List<ItemCatalogo> listarGarantias() throws SQLException {
        return leerItems(SQL_GARANTIAS);
    }

    public List<ItemCatalogo> listarEstadosPrestamo() throws SQLException {
        return leerItems(SQL_ESTADOS_PRESTAMO);
    }

    /** Tipos de préstamo con sus rangos de plazo y TEA y los límites de monto de la política. */
    public List<TipoPrestamo> listarTiposPrestamo() throws SQLException {
        List<TipoPrestamo> tipos = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_TIPOS_PRESTAMO);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                tipos.add(new TipoPrestamo(
                        resultado.getString("TiposPrestamoCodigo").trim(),
                        resultado.getString("TiposPrestamoNombre"),
                        resultado.getInt("TiposPrestamoPlazoMinimo"),
                        resultado.getInt("TiposPrestamoPlazoMaximo"),
                        resultado.getBigDecimal("TiposPrestamoTasaMinima"),
                        resultado.getBigDecimal("TiposPrestamoTasaMaxima"),
                        resultado.getBigDecimal("PoliticaCreditoMontoMinimo"),
                        resultado.getBigDecimal("PoliticaCreditoMontoMaximo")));
            }
        }
        return tipos;
    }

    private List<ItemCatalogo> leerItems(String sql) throws SQLException {
        List<ItemCatalogo> items = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                items.add(new ItemCatalogo(resultado.getString(1).trim(), resultado.getString(2)));
            }
        }
        return items;
    }
}
