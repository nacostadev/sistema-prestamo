/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package sistemaprestamo.controlador;

import java.sql.SQLException;
import java.util.function.BiPredicate;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;
import sistemaprestamo.modelo.Sesion;
import sistemaprestamo.modelo.Usuario;
import sistemaprestamo.persistencia.UsuarioDAO;
import sistemaprestamo.vista.FrmLogin;
import sistemaprestamo.vista.FrmMenuOpcion;

/**
 * Coordina el inicio de sesión del Analista de Crédito (HU-01 RN-07).
 * Las reglas (campos vacíos, credenciales, usuario inactivo) están en Usuario.iniciarSesion.
 */
public class LoginController {

    private final FrmLogin vista;
    private final UsuarioDAO usuarioDAO;
    private final BiPredicate<String, String> verificadorClave;

    public LoginController(FrmLogin vista) {
        this(vista, new UsuarioDAO(), LoginController::verificarBCrypt);
    }

    public LoginController(FrmLogin vista, UsuarioDAO usuarioDAO,
                           BiPredicate<String, String> verificadorClave) {
        this.vista = vista;
        this.usuarioDAO = usuarioDAO;
        this.verificadorClave = verificadorClave;
    }

    public void iniciarSesion() {
        try {
            String nombre = vista.obtenerUsuario();
            String clave = vista.obtenerClave();

            Usuario registrado = nombre.isEmpty() ? null : usuarioDAO.buscarPorNombre(nombre);
            Usuario usuario = Usuario.iniciarSesion(nombre, clave, registrado, verificadorClave);

            Sesion.iniciar(usuario);
            vista.dispose();
            new FrmMenuOpcion().setVisible(true);
        } catch (IllegalArgumentException excepcion) {
            vista.mostrarMensaje(excepcion.getMessage(), "Inicio de sesión", JOptionPane.WARNING_MESSAGE);
            vista.limpiarClave();
        } catch (SQLException excepcion) {
            vista.mostrarMensaje("No se pudo verificar el usuario: " + excepcion.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Compara la contraseña escrita con el hash BCrypt guardado en Seguridad.Usuarios. */
    static boolean verificarBCrypt(String clave, String hash) {
        try {
            return BCrypt.checkpw(clave, hash);
        } catch (IllegalArgumentException hashInvalido) {
            return false;
        }
    }
}
