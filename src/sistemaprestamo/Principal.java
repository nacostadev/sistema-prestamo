package sistemaprestamo;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sistemaprestamo.controlador.LoginController;
import sistemaprestamo.vista.FrmLogin;

/**
 * Punto de entrada de la aplicación de escritorio.
 * Abre el login; al iniciar sesión, LoginController abre el menú principal.
 */
public class Principal {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Button.arc", 8);

        SwingUtilities.invokeLater(() -> {
            FrmLogin vista = new FrmLogin();
            LoginController controlador = new LoginController(vista);
            vista.setControlador(controlador);
            vista.setVisible(true);
        });
    }
}
