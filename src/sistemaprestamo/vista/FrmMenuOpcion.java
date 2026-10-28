package sistemaprestamo.vista;
 
import sistemaprestamo.controlador.MenuController;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
 
public class FrmMenuOpcion extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmMenuOpcion.class.getName());
 
    private static final Color BARRA        = new Color(16, 52, 102);
    private static final Color BARRA_HOVER  = new Color(40, 90, 160);
    private static final Color BARRA_CERRAR = new Color(200, 40, 40);
    private static final Color FONDO_ARRIBA = new Color(10, 42, 94);
    private static final Color FONDO_ABAJO  = new Color(6, 28, 66);
    private static final Color ITEM_ACTIVO  = new Color(30, 107, 255);
    private static final Color ITEM_HOVER   = new Color(255, 255, 255, 28);
    private static final Color TEXTO        = Color.WHITE;
    private static final Color TEXTO_SUAVE  = new Color(176, 196, 226);
    private static final Color LINEA        = new Color(255, 255, 255, 45);
    private static final Font  FUENTE_ICONO = new Font("Segoe UI Symbol", Font.PLAIN, 19);
    private static final Font  FUENTE_ITEM  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final int   ANCHO_MENU   = 270;
 
    private final CardLayout cards = new CardLayout();
    private final JPanel contenido = new JPanel(cards);
    private final Map<String, ItemMenu> items = new LinkedHashMap<>();
    private ItemMenu activo;
    private MenuController controller;
    private JButton btnMaximizar;
    private int dragX, dragY;
 
    public FrmMenuOpcion() {
        setUndecorated(true);
        initComponents();
        construirDiseno();
        controller = new MenuController(this);
        controller.seleccionar(MenuController.PRESTAMO);
    }
 
    private void construirDiseno() {
        setTitle("Sistema de Préstamos");
        setMinimumSize(new Dimension(1280, 720));
 
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(BARRA, 1));
 
        contenido.setBackground(new Color(240, 244, 248));
 
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.add(crearBarraLateral(), BorderLayout.WEST);
        cuerpo.add(contenido, BorderLayout.CENTER);
 
        getContentPane().add(crearBarraTitulo(), BorderLayout.NORTH);
        getContentPane().add(cuerpo, BorderLayout.CENTER);
 
        setSize(1280, 720);
        setLocationRelativeTo(null);
    }
 
    private JPanel crearBarraTitulo() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(BARRA);
        barra.setPreferredSize(new Dimension(100, 34));
 
        JLabel titulo = new JLabel("   Sistema de Préstamos");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
 
        JPanel zonaBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        zonaBotones.setOpaque(false);
 
        JButton btnMin = crearBotonVentana("\u2013", BARRA_HOVER);
        btnMaximizar = crearBotonVentana("\u25A1", BARRA_HOVER);
        JButton btnCerrar = crearBotonVentana("\u2715", BARRA_CERRAR);
 
        btnMin.addActionListener(e -> controller.minimizar());
        btnMaximizar.addActionListener(e -> controller.alternarMaximizar());
        btnCerrar.addActionListener(e -> controller.cerrar());
 
        zonaBotones.add(btnMin);
        zonaBotones.add(btnMaximizar);
        zonaBotones.add(btnCerrar);
 
        barra.add(titulo, BorderLayout.WEST);
        barra.add(zonaBotones, BorderLayout.EAST);
 
        MouseAdapter mover = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                dragX = e.getXOnScreen() - getX();
                dragY = e.getYOnScreen() - getY();
            }
            @Override public void mouseDragged(MouseEvent e) {
                if (!controller.estaMaximizada()) {
                    setLocation(e.getXOnScreen() - dragX, e.getYOnScreen() - dragY);
                }
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) controller.alternarMaximizar();
            }
        };
        barra.addMouseListener(mover);
        barra.addMouseMotionListener(mover);
        titulo.addMouseListener(mover);
        titulo.addMouseMotionListener(mover);
 
        return barra;
    }
 
    private JButton crearBotonVentana(String texto, Color colorHover) {
        JButton b = new JButton(texto);
        b.setUI(new BasicButtonUI());
        b.setOpaque(true);
        b.setBackground(BARRA);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(46, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(colorHover); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(BARRA); }
        });
        return b;
    }
 
    private JPanel crearBarraLateral() {
        JPanel barra = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, FONDO_ARRIBA, 0, getHeight(), FONDO_ABAJO));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        barra.setOpaque(false);
        barra.setPreferredSize(new Dimension(ANCHO_MENU, 700));
 
        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(crearLogo());
        superior.add(crearLinea());
        superior.add(crearPerfil("Lain Cardenas", "Analista de Crédito", "RN-07"));
        superior.add(crearLinea());
        barra.add(superior, BorderLayout.NORTH);
 
        JPanel panelItems = new JPanel();
        panelItems.setOpaque(false);
        panelItems.setLayout(new BoxLayout(panelItems, BoxLayout.Y_AXIS));
        panelItems.setBorder(new EmptyBorder(12, 0, 0, 0));
        agregarOpcion(panelItems, MenuController.INICIO,   "\u2302",       "Inicio");
        agregarOpcion(panelItems, MenuController.PRESTAMO, "\uD83D\uDCC4", "Registrar Préstamo");
        agregarOpcion(panelItems, MenuController.CONSULTA, "\uD83D\uDD0D", "Consultar Préstamos");
        agregarOpcion(panelItems, MenuController.CLIENTES, "\uD83D\uDC65", "Clientes");
        agregarOpcion(panelItems, MenuController.REPORTES, "\uD83D\uDCCA", "Reportes");
        agregarOpcion(panelItems, MenuController.CONFIG,   "\u2699",       "Configuración");
        barra.add(panelItems, BorderLayout.CENTER);
 
        JPanel inferior = new JPanel();
        inferior.setOpaque(false);
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));
        inferior.setBorder(new EmptyBorder(0, 0, 14, 0));
        inferior.add(crearLinea());
        inferior.add(Box.createVerticalStrut(10));
        inferior.add(new ItemMenu(null, "\u21AA", "Cerrar Sesión", true));
        barra.add(inferior, BorderLayout.SOUTH);
 
        return barra;
    }
 
    private void agregarOpcion(JPanel panel, String clave, String icono, String texto) {
        ItemMenu item = new ItemMenu(clave, icono, texto, false);
        items.put(clave, item);
        panel.add(item);
        panel.add(Box.createVerticalStrut(4));
    }
 
    private JPanel crearLogo() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 20, 18, 10));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
 
        JLabel icono = new JLabel("\uD83C\uDFDB");
        icono.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 36));
        icono.setForeground(TEXTO);
 
        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        JLabel t1 = new JLabel("Sistema de Préstamos");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t1.setForeground(TEXTO);
        JLabel t2 = new JLabel("Financieros");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t2.setForeground(TEXTO_SUAVE);
        textos.add(t1);
        textos.add(t2);
 
        p.add(icono, BorderLayout.WEST);
        p.add(textos, BorderLayout.CENTER);
        return p;
    }
 
    private JPanel crearPerfil(String nombre, String cargo, String codigo) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 10));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
 
        JPanel textos = new JPanel(new GridLayout(3, 1));
        textos.setOpaque(false);
        JLabel l1 = new JLabel(nombre);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(TEXTO);
        JLabel l2 = new JLabel(cargo);
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l2.setForeground(TEXTO_SUAVE);
        JLabel l3 = new JLabel(codigo);
        l3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l3.setForeground(TEXTO_SUAVE);
        textos.add(l1);
        textos.add(l2);
        textos.add(l3);
 
        p.add(crearAvatar(), BorderLayout.WEST);
        p.add(textos, BorderLayout.CENTER);
        return p;
    }
 
    private JComponent crearAvatar() {
        return new JComponent() {
            { setPreferredSize(new Dimension(54, 54)); }
 
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                g2.setColor(ITEM_ACTIVO);
                g2.fillOval(1, 1, d, d);
                g2.setColor(new Color(180, 210, 255));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, d, d);
                g2.setColor(Color.WHITE);
                g2.fillOval(d / 2 - 8, 11, 18, 18);
                g2.setClip(new Ellipse2D.Float(1, 1, d, d));
                g2.fillOval(d / 2 - 15, 31, 32, 30);
                g2.dispose();
            }
        };
    }
 
    private JComponent crearLinea() {
        return new JComponent() {
            {
                setPreferredSize(new Dimension(ANCHO_MENU, 1));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            }
 
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(LINEA);
                g.fillRect(18, 0, getWidth() - 36, 1);
            }
        };
    }
 
    private class ItemMenu extends JComponent {
        private final String clave;
        private final String icono;
        private final String texto;
        private final boolean esSalir;
        private boolean activoFlag;
        private boolean hover;
 
        ItemMenu(String clave, String icono, String texto, boolean esSalir) {
            this.clave = clave;
            this.icono = icono;
            this.texto = texto;
            this.esSalir = esSalir;
 
            setPreferredSize(new Dimension(ANCHO_MENU, 46));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    if (ItemMenu.this.esSalir) controller.cerrarSesion();
                    else controller.seleccionar(ItemMenu.this.clave);
                }
            });
        }
 
        void setActivo(boolean v) {
            activoFlag = v;
            repaint();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
            int w = getWidth(), h = getHeight();
 
            if (activoFlag) {
                g2.setColor(ITEM_ACTIVO);
                g2.fillRoundRect(10, 0, w - 20, h, 12, 12);
            } else if (hover) {
                g2.setColor(ITEM_HOVER);
                g2.fillRoundRect(10, 0, w - 20, h, 12, 12);
            }
 
            g2.setColor(TEXTO);
            g2.setFont(FUENTE_ICONO);
            FontMetrics fmI = g2.getFontMetrics();
            g2.drawString(icono, 26, (h + fmI.getAscent() - fmI.getDescent()) / 2);
 
            g2.setFont(FUENTE_ITEM);
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(texto, 68, (h + fmT.getAscent() - fmT.getDescent()) / 2);
 
            g2.dispose();
        }
    }
 
    public void agregarPanel(String clave, JPanel panel) {
        contenido.add(panel, clave);
    }
 
    public void mostrarPanel(String clave) {
        cards.show(contenido, clave);
    }
 
    public void marcarActivo(String clave) {
        ItemMenu nuevo = items.get(clave);
        if (nuevo == null) return;
        if (activo != null) activo.setActivo(false);
        activo = nuevo;
        activo.setActivo(true);
    }
 
    public void actualizarBotonMaximizar(boolean maximizada) {
        btnMaximizar.setText(maximizada ? "\u2750" : "\u25A1");
    }
 
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
 
    public boolean confirmar(String titulo, String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 196, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmMenuOpcion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
