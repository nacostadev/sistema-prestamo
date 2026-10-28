
package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:sqlserver://DESKTOP-0D6JM9P\\SQL2025;"
            + "databaseName=BDGestionCliente;"
            + "integratedSecurity=true;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Calcula el siguiente código de una tabla con formato prefijo + número
     * (por ejemplo PR0004 -> PR0005). Se ejecuta con la misma conexión del
     * INSERT para que, dentro de una transacción, vea las filas ya insertadas.
     *
     * @param sqlMaximo consulta que devuelve el mayor código actual (SELECT MAX(...))
     * @param prefijo letras iniciales del código, por ejemplo "PR"
     * @param cifras cantidad de dígitos del número, por ejemplo 4
     */
    public static String siguienteCodigo(Connection conexion, String sqlMaximo,
                                         String prefijo, int cifras) throws SQLException {
        int siguiente = 1;
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlMaximo);
             ResultSet resultado = sentencia.executeQuery()) {
            if (resultado.next() && resultado.getString(1) != null) {
                siguiente = Integer.parseInt(resultado.getString(1).trim().substring(prefijo.length())) + 1;
            }
        }
        return prefijo + String.format("%0" + cifras + "d", siguiente);
    }
}
