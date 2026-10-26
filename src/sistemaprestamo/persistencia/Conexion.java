
package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de proporcionar la conexión JDBC con MySQL.
 */
public class Conexion {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL =
            "jdbc:mysql://localhost:3306/bd_sistemaprestamo";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException excepcion) {
            throw new SQLException(
                    "No se encontró el driver JDBC de MySQL. "
                    + "Agregue MySQL Connector/J a las bibliotecas del proyecto.",
                    excepcion
            );
        }

        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}