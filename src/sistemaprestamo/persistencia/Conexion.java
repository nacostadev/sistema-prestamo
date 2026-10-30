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