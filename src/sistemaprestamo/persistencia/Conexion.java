package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de proporcionar la conexión JDBC con PostgreSQL.
 * Los parámetros de conexión son de ejemplo: ajuste la base de datos,
 * el usuario y la contraseña a su entorno. No contiene credenciales reales.
 */
public class Conexion {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/bd_sistemaprestamo";
    private static final String USUARIO = "postgres";
    private static final String CONTRASENA = "postgres";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException excepcion) {
            throw new SQLException(
                    "No se encontró el driver de PostgreSQL. "
                    + "Agregue el archivo .jar del driver a las bibliotecas del proyecto.", excepcion);
        }
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
