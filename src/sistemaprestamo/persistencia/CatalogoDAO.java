package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.modelo.ItemCatalogo;

/** Catálogos del esquema Prestamo que se muestran en los combos del formulario de préstamos. */
public class CatalogoDAO {

    private static final String SQL_MODALIDADES =
            "SELECT ModalidadesPagoCodigo, ModalidadesPagoNombre FROM Prestamo.ModalidadesPago "
            + "ORDER BY ModalidadesPagoCodigo";
    private static final String SQL_MONEDAS =
            "SELECT MonedasCodigo, MonedasNombre FROM Prestamo.Monedas ORDER BY MonedasCodigo";
    private static final String SQL_GARANTIAS =
            "SELECT GarantiasCodigo, GarantiasDescripcion FROM Prestamo.Garantias ORDER BY GarantiasCodigo";

    public List<ItemCatalogo> listarModalidadesPago() throws SQLException {
        return consultar(SQL_MODALIDADES);
    }

    public List<ItemCatalogo> listarMonedas() throws SQLException {
        return consultar(SQL_MONEDAS);
    }

    public List<ItemCatalogo> listarGarantias() throws SQLException {
        return consultar(SQL_GARANTIAS);
    }

    private List<ItemCatalogo> consultar(String sql) throws SQLException {
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
