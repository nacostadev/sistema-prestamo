package sistemaprestamo.controlador;

import sistemaprestamo.vista.FrmClientes;
import sistemaprestamo.vista.FrmMenuOpcion;
//import sistemaprestamo.vista.FrmPrestamo;
import sistemaprestamo.vista.FrmRegistroPrestamo;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.swing.JFrame;
import javax.swing.JPanel;
import sistemaprestamo.modelo.Sesion;
import sistemaprestamo.modelo.Usuario;

public class MenuController {

    //public static final String INICIO   = "INICIO";
    public static final String PRESTAMO = "PRESTAMO";
    //public static final String CONSULTA = "CONSULTA";
    public static final String CLIENTES = "CLIENTES";
    public static final String REPORTES = "REPORTES";
    public static final String CONFIG   = "CONFIG";

    private final FrmMenuOpcion vista;
    private final Map<String, Supplier<JFrame>> formularios = new LinkedHashMap<>();
    private final Set<String> abiertos = new HashSet<>();

    public MenuController(FrmMenuOpcion vista) {
        this.vista = vista;
        formularios.put(PRESTAMO, FrmRegistroPrestamo::new);
        formularios.put(CLIENTES, FrmClientes::new);
    }

    public void seleccionar(String clave) {
        if (PRESTAMO.equals(clave)) {
            try {
                Usuario.validarSesionAutorizada(Sesion.getUsuario());
            } catch (IllegalArgumentException excepcion) {
                vista.mostrarMensaje(excepcion.getMessage());
                return;
            }
        }
        Supplier<JFrame> fabrica = formularios.get(clave);
        if (fabrica == null) {
            vista.mostrarMensaje("Módulo en construcción");
            return;
        }

        try {
            if (!abiertos.contains(clave)) {
                JFrame frm = fabrica.get();
                JPanel envoltorio = new JPanel(new BorderLayout());
                envoltorio.add(frm.getContentPane(), BorderLayout.CENTER);
                vista.agregarPanel(clave, envoltorio);
                abiertos.add(clave);
            }
            vista.mostrarPanel(clave);
            vista.marcarActivo(clave);
        } catch (Exception e) {
            vista.mostrarMensaje("No se pudo abrir el módulo: " + e.getMessage());
        }
    }

    public void cerrarSesion() {
        if (vista.confirmar("Cerrar Sesión", "¿Desea cerrar sesión?")) {
            System.exit(0);
        }
    }

    public void minimizar() {
        vista.setExtendedState(vista.getExtendedState() | JFrame.ICONIFIED);
    }

    public boolean estaMaximizada() {
        return (vista.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
    }

    public void alternarMaximizar() {
        if (estaMaximizada()) {
            vista.setExtendedState(JFrame.NORMAL);
            vista.actualizarBotonMaximizar(false);
        } else {
            GraphicsConfiguration gc = vista.getGraphicsConfiguration();
            Rectangle b = gc.getBounds();
            Insets in = Toolkit.getDefaultToolkit().getScreenInsets(gc);
            vista.setMaximizedBounds(new Rectangle(b.x + in.left, b.y + in.top,
                    b.width - in.left - in.right, b.height - in.top - in.bottom));
            vista.setExtendedState(JFrame.MAXIMIZED_BOTH);
            vista.actualizarBotonMaximizar(true);
        }
    }

    public void cerrar() {
        System.exit(0);
    }
}
