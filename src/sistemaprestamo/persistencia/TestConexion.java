package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {

    public static void main(String[] args) {
        System.out.println("Intentando conectar a la base de datos...");
        
        try (Connection conexion = Conexion.getConnection()) {
            
            if (conexion != null) {
                System.out.println("✅ ¡Conexión exitosa a la base de datos BDGestionCliente!");
                System.out.println("Catálogo actual: " + conexion.getCatalog());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos.");
            System.err.println("Motivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}