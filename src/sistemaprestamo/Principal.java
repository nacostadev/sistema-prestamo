package sistemaprestamo;

import javax.swing.SwingUtilities;
import sistemaprestamo.controlador.ClienteController;
import sistemaprestamo.modelo.ClienteService;
import sistemaprestamo.vista.FrmCliente;

/**
 * Punto de entrada de la aplicación de escritorio.
 * Enlaza vista, controlador y servicio (lógica de negocio).
 */
public class Principal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrmCliente vista = new FrmCliente();
            ClienteService servicio = new ClienteService();
            ClienteController controlador = new ClienteController(vista, servicio);
            vista.setControlador(controlador);
            vista.setVisible(true);
        });
    }
}
