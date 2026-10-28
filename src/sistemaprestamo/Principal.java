package sistemaprestamo;

import javax.swing.SwingUtilities;
import sistemaprestamo.controlador.ClienteController;
import sistemaprestamo.vista.FrmClientes;

/**
 * Punto de entrada de la aplicación de escritorio.
 * Enlaza la vista con su controlador.
 */
public class Principal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrmClientes vista = new FrmClientes();
            ClienteController controlador = new ClienteController(vista);
            vista.setControlador(controlador);
            controlador.iniciar();
            vista.setVisible(true);
        });
    }
}
