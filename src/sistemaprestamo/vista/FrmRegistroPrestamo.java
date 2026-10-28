package sistemaprestamo.vista;

import sistemaprestamo.modelo.Prestamo;
import java.awt.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.View;

/**
 * Vista de Registro de Préstamo Financiero.
 * Interfaz construida 100% por código (sin initComponents ni AbsoluteLayout).
 * Incluye un calendario propio para las fechas (no requiere librerías externas).
 */
public class FrmRegistroPrestamo extends JFrame {

    // ====== Paleta ======
    private static final Color AZUL_TITULO = new Color(30, 74, 138);
    private static final Color AZUL_BOTON = new Color(37, 99, 235);
    private static final Color FONDO = new Color(238, 242, 247);
    private static final Color BORDE = new Color(204, 214, 230);
    private static final Color TEXTO = new Color(31, 45, 61);
    private static final Color GRIS = new Color(110, 122, 140);
    private static final Color VERDE = new Color(22, 128, 61);
    private static final Color ROJO = new Color(200, 35, 50);
    private static final Color RN_BG = new Color(230, 236, 243);
    private static final Color RN_FG = new Color(74, 91, 112);
    private static final Color RN_COD = new Color(30, 58, 95);

    private static final Font F_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_PEQ = new Font("Segoe UI", Font.PLAIN, 11);

    private static final Locale ES = Locale.forLanguageTag("es-PE");
    private static final DateTimeFormatter FMT_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final int OBS_MAX = 500;

    // ====== Datos de ejemplo (reemplazar por Service / DAO) ======
    private static class Cliente {
        final String id, tipoDoc, doc, nombres, apellidos;
        final double ingresos;
        final boolean activo, moroso;

        Cliente(String id, String tipoDoc, String doc, String nombres, String apellidos,
                double ingresos, boolean activo, boolean moroso) {
            this.id = id; this.tipoDoc = tipoDoc; this.doc = doc; this.nombres = nombres;
            this.apellidos = apellidos; this.ingresos = ingresos; this.activo = activo; this.moroso = moroso;
        }

        String nombre() { return apellidos + ", " + nombres; }
    }

    private static final List<Cliente> CLIENTES = List.of(
            new Cliente("CL01", "DNI", "45871236", "Juan Carlos", "Ramírez Torres", 2500, true, false),
            new Cliente("CL02", "DNI", "72943815", "María Fernanda", "Gómez López", 1800, true, false),
            new Cliente("CL03", "DNI", "40125698", "Luis Alberto", "Salazar Vega", 3200, true, true),
            new Cliente("CL04", "Carné de Extranjería", "001458963", "Rosa", "Quispe Rojas", 2100, false, false),
            new Cliente("CL05", "DNI", "61478523", "Carla", "Torres Medina", 2800, true, false));

    /** Política de crédito por tipo de préstamo */
    private static class Politica {
        final String nombre;
        final int min, max, pMin, pMax, tMin, tMax;

        Politica(String nombre, int min, int max, int pMin, int pMax, int tMin, int tMax) {
            this.nombre = nombre; this.min = min; this.max = max;
            this.pMin = pMin; this.pMax = pMax; this.tMin = tMin; this.tMax = tMax;
        }
    }

    private static final Map<String, Politica> POLITICAS = new LinkedHashMap<>();
    static {
        POLITICAS.put("TP01", new Politica("Préstamo Personal", 500, 30000, 1, 36, 15, 45));
        POLITICAS.put("TP02", new Politica("Préstamo Vehicular", 5000, 80000, 1, 60, 10, 30));
    }

    private static int secuencia = 4; // último código usado: PR0004

    // ====== Componentes ======
    private CampoTexto txtDni, txtMonto, txtTEA, txtDestino;
    private JSpinner spPlazo;
    private SpinnerNumberModel mdlPlazo;
    private SelectorFecha selFecha;
    private JComboBox<String> cbTipo, cbMoneda, cbModalidad, cbGarantia;
    private JTextArea txtObs;
    private JLabel lblContador;
    private JButton btnValidar, btnRegistrar, btnCancelar;
    private Hint alerta;
    private JPanel panelCliInfo;
    private JScrollPane scrollPrincipal;

    private final Map<JComponent, JLabel> errores = new HashMap<>();
    private final Map<JComponent, javax.swing.border.Border> bordesOriginales = new HashMap<>();
    private final List<JComponent> marcados = new ArrayList<>();

    public FrmRegistroPrestamo() {
        setTitle("Registrar Préstamo Financiero");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        construirUI();
        cargarCombos();
        configurarEventos();
        resetFormulario();
        setMinimumSize(new Dimension(820, 520));
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    // =====================================================================
    //  CONSTRUCCIÓN DE LA INTERFAZ
    // =====================================================================
    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout(0, 8));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(10, 16, 12, 16));
        raiz.add(crearHeader(), BorderLayout.NORTH);

        alerta = new Hint(RN_BG, RN_FG);
        alerta.setVisible(false);

        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 10, 0);
        g.gridy = 0; cuerpo.add(alerta, g);
        g.gridy = 1; cuerpo.add(crearSeccionCliente(), g);
        g.gridy = 2; cuerpo.add(crearSeccionCondiciones(), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 0, 0); cuerpo.add(crearSeccionAdicional(), g);

        JPanel norte = new PanelScroll(new BorderLayout());
        norte.setOpaque(true);
        norte.setBackground(FONDO);
        norte.setBorder(new EmptyBorder(0, 0, 0, 10));
        norte.add(cuerpo, BorderLayout.NORTH);

        scrollPrincipal = new JScrollPane(norte);
        scrollPrincipal.setBorder(BorderFactory.createEmptyBorder());
        scrollPrincipal.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPrincipal.setBackground(FONDO);
        scrollPrincipal.getViewport().setOpaque(true);
        scrollPrincipal.getViewport().setBackground(FONDO);
        scrollPrincipal.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPrincipal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPrincipal.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);
        scrollPrincipal.getVerticalScrollBar().setUI(new ScrollFino());
        scrollPrincipal.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPrincipal.getVerticalScrollBar().setBackground(FONDO);
        scrollPrincipal.getVerticalScrollBar().setBorder(null);
        raiz.add(scrollPrincipal, BorderLayout.CENTER);

        setContentPane(raiz);
    }

    private JPanel crearHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        JLabel t = new JLabel("Registrar Préstamo Financiero");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(AZUL_TITULO);
        JLabel s = new JLabel("Complete la información del préstamo y del cliente para registrar una nueva operación.");
        s.setFont(F_PEQ);
        s.setForeground(GRIS);
        textos.add(t);
        textos.add(s);
        p.add(textos, BorderLayout.WEST);

        LocalDateTime ahora = LocalDateTime.now();
        String fecha = ahora.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ES));
        String hora = ahora.format(DateTimeFormatter.ofPattern("HH:mm"));
        JLabel lf = new JLabel("<html><div style='text-align:right'>" + fecha + "<br>" + hora + "</div></html>");
        lf.setFont(F_PEQ);
        lf.setForeground(AZUL_TITULO);
        p.add(lf, BorderLayout.EAST);
        return p;
    }

    /** Sección 1: Validación del cliente */
    private JPanel crearSeccionCliente() {
        txtDni = new CampoTexto("Ingrese DNI o Carné de Extranjería");
        btnValidar = crearBoton("Validar Cliente", true);
        btnValidar.setPreferredSize(new Dimension(140, 30));

        JPanel celdaDni = celda("DNI / CE del Cliente", true, txtDni);

        JPanel fila = new JPanel(new GridBagLayout());
        fila.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.anchor = GridBagConstraints.SOUTHWEST; g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1; g.insets = new Insets(0, 0, 0, 12);
        fila.add(celdaDni, g);
        g.gridx = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE; g.insets = new Insets(0, 0, 0, 0);
        fila.add(btnValidar, g);

        panelCliInfo = new JPanel(new BorderLayout());
        panelCliInfo.setOpaque(false);
        panelCliInfo.setVisible(false);

        return tarjeta(1, "Validación del Cliente", columna(8, fila, panelCliInfo));
    }

    /** Sección 2: Condiciones financieras */
    private JPanel crearSeccionCondiciones() {
        cbTipo = combo();
        cbMoneda = combo();
        cbModalidad = combo();
        txtMonto = new CampoTexto("Ingrese monto");
        spPlazo = crearSpinnerPlazo();
        txtTEA = new CampoTexto("Ingrese TEA anual");
        selFecha = new SelectorFecha();

        JPanel fila1 = fila(2,
                celda("Tipo de Préstamo", true, cbTipo),
                celda("Moneda", true, cbMoneda));
        JPanel fila2 = fila(3,
                celda("Monto Solicitado", true, txtMonto),
                celda("Plazo (en cuotas)", true, spPlazo),
                celda("TEA (%)", true, txtTEA));
        JPanel fila3 = fila(2,
                celda("Modalidad de Pago", true, cbModalidad),
                celda("Fecha de Desembolso", true, selFecha));

        return tarjeta(2, "Condiciones Financieras Obligatorias", columna(8, fila1, fila2, fila3));
    }

    /** Sección 3: Información adicional y garantías */
    private JPanel crearSeccionAdicional() {
        cbGarantia = combo();
        txtDestino = new CampoTexto("Ej. Capital de trabajo");

        txtObs = new JTextArea(2, 20);
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setFont(F_NORMAL);
        ((AbstractDocument) txtObs.getDocument()).setDocumentFilter(new LimiteFilter(OBS_MAX));
        JScrollPane sp = new JScrollPane(txtObs);
        sp.setBorder(new LineBorder(BORDE, 1, true));
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUI(new ScrollFino());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        lblContador = new JLabel("0/" + OBS_MAX, SwingConstants.RIGHT);
        lblContador.setFont(F_PEQ);
        lblContador.setForeground(GRIS);
        JPanel contador = new JPanel(new BorderLayout());
        contador.setOpaque(false);
        contador.add(lblContador, BorderLayout.EAST);
        JPanel celdaObs = new JPanel();
        celdaObs.setOpaque(false);
        celdaObs.setLayout(new BorderLayout(0, 3));
        celdaObs.add(etiqueta("Observaciones del Préstamo", false), BorderLayout.NORTH);
        celdaObs.add(sp, BorderLayout.CENTER);
        celdaObs.add(contador, BorderLayout.SOUTH);

        JPanel fila1 = fila(2,
                celda("Garantía / Colateral", false, cbGarantia),
                celda("Destino del Crédito", false, txtDestino));

        btnRegistrar = crearBoton("Registrar Préstamo", true);
        btnRegistrar.setPreferredSize(new Dimension(170, 32));
        btnCancelar = crearBoton("Cancelar", false);
        btnCancelar.setPreferredSize(new Dimension(100, 32));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnRegistrar);
        JLabel nota = new JLabel("<html><font color='#C82332'>*</font> Campos obligatorios</html>");
        nota.setFont(F_PEQ);
        nota.setForeground(GRIS);
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, BORDE), new EmptyBorder(8, 0, 0, 0)));
        pie.add(nota, BorderLayout.WEST);
        pie.add(botones, BorderLayout.EAST);

        return tarjeta(3, "Información Adicional y Garantías", columna(8, fila1, celdaObs, pie));
    }

    // ====== Helpers de UI ======
    private JPanel tarjeta(int numero, String titulo, JComponent contenido) {
        JPanel t = new Tarjeta(new BorderLayout(0, 8));
        t.setBorder(new EmptyBorder(9, 15, 11, 15));

        JPanel cab = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cab.setOpaque(false);
        cab.add(new Circulo(String.valueOf(numero)));
        JLabel lt = new JLabel(titulo);
        lt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lt.setForeground(AZUL_TITULO);
        cab.add(lt);

        contenido.setOpaque(false);
        t.add(cab, BorderLayout.NORTH);
        t.add(contenido, BorderLayout.CENTER);
        return t;
    }

    /** Apila componentes verticalmente (anclados arriba, ancho completo) */
    private JPanel columna(int gap, JComponent... items) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.NORTHWEST;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) continue;
            g.gridy = i;
            g.insets = new Insets(i == 0 ? 0 : gap, 0, 0, 0);
            p.add(items[i], g);
        }
        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 0; f.gridy = items.length; f.weighty = 1;
        JPanel relleno = new JPanel();
        relleno.setOpaque(false);
        relleno.setPreferredSize(new Dimension(0, 0));
        p.add(relleno, f);
        return p;
    }

    /** Fila de N columnas iguales */
    private JPanel fila(int columnas, JComponent... celdas) {
        JPanel p = new JPanel(new GridLayout(1, columnas, 12, 0));
        p.setOpaque(false);
        for (JComponent c : celdas) p.add(c);
        return p;
    }

    /** Etiqueta + campo + mensaje de error + texto gris de ayuda */
    private JPanel celda(String titulo, boolean obligatorio, JComponent campo) {
        JLabel err = new JLabel();
        err.setFont(F_PEQ);
        err.setForeground(ROJO);
        err.setVisible(false);
        errores.put(campo, err);
        return columna(3, etiqueta(titulo, obligatorio), campo, err);
    }

    private JLabel etiqueta(String titulo, boolean obligatorio) {
        String marca = obligatorio ? " <font color='#C82332'>*</font>"
                : " <font color='#7A8699'>(opcional)</font>";
        JLabel l = new JLabel("<html>" + titulo + marca + "</html>");
        l.setFont(F_BOLD);
        l.setForeground(AZUL_TITULO);
        return l;
    }

    /** Spinner de cuotas: números enteros desde 1 hasta el máximo del tipo de préstamo */
    private JSpinner crearSpinnerPlazo() {
        mdlPlazo = new SpinnerNumberModel(1, 1, 60, 1);
        JSpinner sp = new JSpinner(mdlPlazo);
        sp.setFont(F_NORMAL);
        sp.setPreferredSize(new Dimension(100, 30));
        sp.setBorder(new LineBorder(BORDE, 1, true));
        JSpinner.NumberEditor ed = new JSpinner.NumberEditor(sp, "#");
        sp.setEditor(ed);
        JTextField tf = ed.getTextField();
        tf.setFont(F_NORMAL);
        tf.setHorizontalAlignment(SwingConstants.LEFT);
        tf.setBorder(new EmptyBorder(2, 8, 2, 4));
        return sp;
    }

    /** El máximo de cuotas depende del tipo de préstamo (personal: 36, vehicular: 60) */
    private void actualizarPlazo() {
        Politica t = POLITICAS.get(codigo(cbTipo));
        int max = t != null ? t.pMax : 60;
        mdlPlazo.setMaximum(max);
        if (((Number) mdlPlazo.getValue()).intValue() > max) mdlPlazo.setValue(max);
    }

    private JComboBox<String> combo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(F_NORMAL);
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(100, 30));
        return cb;
    }

    private JButton crearBoton(String texto, boolean primario) {
        JButton b = new JButton(texto);
        b.setFont(F_BOLD);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (primario) {
            b.setBorderPainted(false);
            b.setBackground(AZUL_BOTON);
            b.setForeground(Color.WHITE);
        } else {
            b.setBorder(new LineBorder(AZUL_BOTON, 1, true));
            b.setBackground(Color.WHITE);
            b.setForeground(AZUL_BOTON);
        }
        return b;
    }

    // =====================================================================
    //  DATOS Y EVENTOS
    // =====================================================================
    private void cargarCombos() {
        cbTipo.addItem("Seleccionar tipo de préstamo");
        for (Map.Entry<String, Politica> e : POLITICAS.entrySet()) {
            cbTipo.addItem(e.getKey() + " · " + e.getValue().nombre);
        }
        cbMoneda.addItem("Seleccionar moneda");
        cbMoneda.addItem("MO01 · Sol peruano (PEN)");
        cbMoneda.addItem("MO02 · Dólar (USD)");

        cbModalidad.addItem("Seleccionar modalidad");
        cbModalidad.addItem("MP01 · Mensual (Método francés)");

        cbGarantia.addItem("NINGUNA - Sin Garantía");
        cbGarantia.addItem("Aval");
        cbGarantia.addItem("Hipotecaria");
        cbGarantia.addItem("Prendaria");
    }

    private void configurarEventos() {
        btnValidar.addActionListener(e -> {
            limpiarErrores();
            if (txtDni.getText().trim().isEmpty()) {
                bad(txtDni, "Ingrese el documento del cliente");
                return;
            }
            validarCliente();
        });
        txtDni.addActionListener(e -> btnValidar.doClick());
        txtDni.getDocument().addDocumentListener(simple(() -> ocultarInfoCliente()));

        btnRegistrar.addActionListener(e -> registrarPrestamo());
        btnCancelar.addActionListener(e -> {
            limpiarErrores();
            ocultarAlerta();
            resetFormulario();
        });

        cbTipo.addActionListener(e -> actualizarPlazo());
        cbMoneda.addActionListener(e -> {
            String m = String.valueOf(cbMoneda.getSelectedItem());
            txtMonto.setSufijo(m.startsWith("MO02") ? "$" : "S/");
        });
        txtObs.getDocument().addDocumentListener(
                simple(() -> lblContador.setText(txtObs.getText().length() + "/" + OBS_MAX)));
    }

    private DocumentListener simple(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { r.run(); }
            public void removeUpdate(DocumentEvent e) { r.run(); }
            public void changedUpdate(DocumentEvent e) { r.run(); }
        };
    }

    // ====== Cliente ======
    private void ocultarInfoCliente() {
        panelCliInfo.removeAll();
        panelCliInfo.setVisible(false);
        panelCliInfo.revalidate();
    }

    /** RN-01 / RN-06: cliente existente, activo y sin morosidad. Muestra su ficha. */
    private Cliente validarCliente() {
        String doc = txtDni.getText().trim();
        ocultarInfoCliente();
        if (doc.isEmpty()) return null;

        Cliente c = null;
        for (Cliente x : CLIENTES) { // TODO: reemplazar por consulta a la BD
            if (x.doc.equals(doc)) { c = x; break; }
        }
        panelCliInfo.removeAll();
        if (c == null) {
            Hint w = new Hint(new Color(255, 244, 214), new Color(122, 91, 0));
            w.setHtml("No existe un cliente registrado con el documento " + esc(doc) + ".");
            panelCliInfo.add(w, BorderLayout.CENTER);
        } else {
            panelCliInfo.add(fichaCliente(c), BorderLayout.CENTER);
        }
        panelCliInfo.setVisible(true);
        panelCliInfo.revalidate();
        panelCliInfo.repaint();
        return c;
    }

    private JPanel fichaCliente(Cliente c) {
        JPanel k = new JPanel(new GridBagLayout());
        k.setBackground(new Color(244, 247, 251));
        k.setBorder(new CompoundBorder(new LineBorder(BORDE, 1, true), new EmptyBorder(8, 12, 8, 12)));

        Pill estado = c.activo ? new Pill("Activo", new Color(209, 231, 221), new Color(15, 81, 50))
                : new Pill("Inactivo", new Color(226, 227, 229), new Color(65, 70, 75));
        Pill credito = c.moroso ? new Pill("Con morosidad", new Color(248, 215, 218), new Color(132, 32, 41))
                : new Pill("Sin morosidad", new Color(209, 231, 221), new Color(15, 81, 50));

        JComponent[] datos = {
            dato("Cliente", valor(c.nombre())),
            dato("Documento", valor(c.tipoDoc + " " + c.doc)),
            dato("Ingresos", valor(fmt(c.ingresos))),
            dato("Estado", envolver(estado)),
            dato("Situación crediticia", envolver(credito))
        };
        double[] pesos = {2.2, 1.6, 1.0, 0.8, 1.4};
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < datos.length; i++) {
            g.gridx = i; g.weightx = pesos[i]; g.insets = new Insets(0, i == 0 ? 0 : 12, 0, 0);
            k.add(datos[i], g);
        }
        return k;
    }

    private JPanel dato(String caption, JComponent valor) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(caption);
        l.setFont(F_PEQ);
        l.setForeground(GRIS);
        p.add(l, BorderLayout.NORTH);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    private JLabel valor(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_BOLD);
        l.setForeground(TEXTO);
        return l;
    }

    private JPanel envolver(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    // ====== Registro ======
    private void registrarPrestamo() {
        limpiarErrores();
        ocultarAlerta();

        // CA-05: campos obligatorios
        List<String> faltan = new ArrayList<>();
        exigir(txtDni, !txtDni.getText().trim().isEmpty(), "DNI / CE del cliente", faltan);
        exigir(cbTipo, cbTipo.getSelectedIndex() > 0, "Tipo de préstamo", faltan);
        exigir(cbMoneda, cbMoneda.getSelectedIndex() > 0, "Moneda", faltan);
        exigir(cbModalidad, cbModalidad.getSelectedIndex() > 0, "Modalidad de pago", faltan);
        exigir(txtMonto, !txtMonto.getText().trim().isEmpty(), "Monto solicitado", faltan);
        exigir(txtTEA, !txtTEA.getText().trim().isEmpty(), "TEA", faltan);
        exigir(selFecha, !selFecha.getText().trim().isEmpty(), "Fecha de desembolso", faltan);
        if (!faltan.isEmpty()) {
            mostrarAlerta("warning", "<b>Faltan campos obligatorios.</b> Complete: " + String.join(", ", faltan) + ".");
            return;
        }

        // RN-01 / CA-03: cliente registrado, activo y sin morosidad
        Cliente c = validarCliente();
        if (c == null) {
            bad(txtDni, "Cliente no registrado");
            mostrarAlerta("warning", "<b>No se puede registrar el préstamo.</b> El cliente no existe en el sistema.");
            return;
        }
        if (!c.activo) {
            bad(txtDni, "Cliente inactivo");
            mostrarAlerta("warning", "<b>No se puede registrar el préstamo.</b> " + esc(c.nombre()) + " se encuentra inactivo en el sistema.");
            return;
        }
        if (c.moroso) {
            bad(txtDni, "Cliente con morosidad");
            mostrarAlerta("warning", "<b>No se puede registrar el préstamo.</b> " + esc(c.nombre()) + " tiene préstamos en estado de morosidad o incumplimiento.");
            return;
        }

        // Formatos numéricos y de fecha
        BigDecimal monto = leerDecimal(txtMonto);
        try { spPlazo.commitEdit(); } catch (java.text.ParseException ex) { /* se conserva el último valor válido */ }
        Integer plazo = ((Number) spPlazo.getValue()).intValue();
        BigDecimal tea = leerDecimal(txtTEA);
        if (monto == null || tea == null) {
            mostrarAlerta("warning", "<b>Datos numéricos no válidos.</b> Verifique que Monto, Plazo y TEA contengan números válidos.");
            return;
        }
        LocalDate fecha = selFecha.getFecha();
        if (fecha == null) {
            bad(selFecha, "Use el formato dd/mm/aaaa");
            mostrarAlerta("warning", "<b>Fecha no válida.</b> La fecha de desembolso debe tener el formato dd/mm/aaaa (Ejemplo: 01/10/2026).");
            return;
        }
        if (fecha.isBefore(LocalDate.now())) {
            bad(selFecha, "No puede ser anterior a hoy");
            mostrarAlerta("warning", "<b>Fecha no válida.</b> La fecha de desembolso no puede ser anterior a hoy.");
            return;
        }

        // CA-04: límites de monto según tipo
        Politica t = POLITICAS.get(codigo(cbTipo));
        if (monto.compareTo(BigDecimal.valueOf(t.min)) < 0 || monto.compareTo(BigDecimal.valueOf(t.max)) > 0) {
            bad(txtMonto, "Monto fuera de rango");
            mostrarAlerta("danger", "<b>Monto no válido.</b> El monto solicitado debe estar entre " + fmt(t.min) + " y " + fmt(t.max) + " para " + t.nombre + ".");
            return;
        }
        // CA-06: plazo y tasa según tipo
        List<String> errs = new ArrayList<>();
        if (plazo < t.pMin || plazo > t.pMax) {
            bad(spPlazo, "Entre " + t.pMin + " y " + t.pMax);
            errs.add("el plazo debe estar entre " + t.pMin + " y " + t.pMax + " cuotas");
        }
        if (tea.compareTo(BigDecimal.valueOf(t.tMin)) < 0 || tea.compareTo(BigDecimal.valueOf(t.tMax)) > 0) {
            bad(txtTEA, "Entre " + t.tMin + "% y " + t.tMax + "%");
            errs.add("la TEA debe estar entre " + t.tMin + "% y " + t.tMax + "%");
        }
        if (!errs.isEmpty()) {
            mostrarAlerta("warning", "<b>Condiciones no válidas para " + t.nombre + ":</b> " + String.join("; ", errs) + ".");
            return;
        }

        try {
            Prestamo p = new Prestamo();
            p.setClienteCodigo(c.id);
            p.setTipoPrestamoCodigo(codigo(cbTipo));
            p.setMontoSolicitado(monto);
            p.setPlazoCuotas(plazo);
            p.setTea(tea);
            p.setModalidadPagoCodigo(codigo(cbModalidad));
            p.setMonedaCodigo(codigo(cbMoneda));
            p.setFechaDesembolso(fecha);
            p.setGarantiaCodigo(codigo(cbGarantia));
            p.setDescripcionDestino(txtDestino.getText().trim());
            p.setObservaciones(txtObs.getText().trim());

            // TODO: enviar "p" al Service / DAO para persistir en la BD

            String id = String.format("PR%04d", ++secuencia);
            resetFormulario();
            mostrarAlerta("success", "<b>Préstamo registrado correctamente.</b> Código <b>" + id + "</b> · "
                    + esc(c.nombre()) + " · " + fmt(monto.doubleValue()) + " en " + plazo + " cuotas al " + tea + "% TEA.");
        } catch (Exception ex) {
            // CA-07: error durante la transacción
            mostrarAlerta("danger", "<b>No fue posible registrar el préstamo financiero.</b> Ocurrió un error durante la transacción. Inténtelo nuevamente.");
        }
    }

    private void exigir(JComponent c, boolean ok, String nombre, List<String> faltan) {
        if (!ok) {
            bad(c, "Campo requerido");
            faltan.add(nombre);
        }
    }

    private BigDecimal leerDecimal(CampoTexto c) {
        try {
            return new BigDecimal(c.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            bad(c, "Número no válido");
            return null;
        }
    }

    /** Código = texto antes de " · " o " - " (p. ej. "TP01 · Préstamo Personal" -> "TP01") */
    private String codigo(JComboBox<String> cb) {
        Object sel = cb.getSelectedItem();
        if (sel == null) return "";
        return sel.toString().split("\\s[·-]\\s")[0].trim();
    }

    private void resetFormulario() {
        txtDni.setText("");
        ocultarInfoCliente();
        cbTipo.setSelectedIndex(0);
        cbMoneda.setSelectedIndex(0);
        cbModalidad.setSelectedIndex(0);
        cbGarantia.setSelectedIndex(0);
        txtMonto.setText("");
        mdlPlazo.setValue(1);
        txtTEA.setText("");
        txtDestino.setText("");
        txtObs.setText("");
        selFecha.setFecha(LocalDate.now());
        txtMonto.setSufijo("S/");
        actualizarPlazo();
    }

    // ====== Alertas y errores de campo ======
    private void mostrarAlerta(String tipo, String html) {
        Color bg, fg;
        switch (tipo) {
            case "success": bg = new Color(209, 231, 221); fg = new Color(15, 81, 50); break;
            case "danger": bg = new Color(248, 215, 218); fg = new Color(132, 32, 41); break;
            default: bg = new Color(255, 243, 205); fg = new Color(102, 77, 3); break;
        }
        alerta.setBackground(bg);
        alerta.setForeground(fg);
        alerta.setHtml(html);
        alerta.setVisible(true);
        alerta.revalidate();
        SwingUtilities.invokeLater(() -> scrollPrincipal.getViewport().setViewPosition(new Point(0, 0)));
    }

    private void ocultarAlerta() {
        alerta.setVisible(false);
    }

    private void bad(JComponent c, String texto) {
        JLabel err = errores.get(c);
        if (err != null) {
            err.setText(texto);
            err.setVisible(true);
            err.revalidate();
        }
        if (c instanceof CampoTexto) {
            ((CampoTexto) c).setInvalido(true);
        } else {
            bordesOriginales.putIfAbsent(c, c.getBorder());
            c.setBorder(new LineBorder(ROJO, 1, true));
        }
        if (!marcados.contains(c)) marcados.add(c);
    }

    private void limpiarErrores() {
        for (JComponent c : marcados) {
            if (c instanceof CampoTexto) {
                ((CampoTexto) c).setInvalido(false);
            } else if (bordesOriginales.containsKey(c)) {
                c.setBorder(bordesOriginales.get(c));
            }
            JLabel err = errores.get(c);
            if (err != null) {
                err.setVisible(false);
                err.revalidate();
            }
        }
        marcados.clear();
    }

    private static String fmt(double n) {
        return String.format(Locale.US, "S/ %,.2f", n);
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // =====================================================================
    //  COMPONENTES PROPIOS
    // =====================================================================

    /** JTextField con placeholder y sufijo opcional (S/, $) */
    private static class CampoTexto extends JTextField {
        private final String hint;
        private String sufijo = "";
        private boolean invalido = false;

        CampoTexto(String hint) {
            this.hint = hint;
            setFont(F_NORMAL);
            setPreferredSize(new Dimension(100, 30));
            aplicarBorde();
        }

        private void aplicarBorde() {
            int der = sufijo.isEmpty() ? 8 : 30;
            setBorder(new CompoundBorder(new LineBorder(invalido ? ROJO : BORDE, 1, true), new EmptyBorder(2, 8, 2, der)));
        }

        void setSufijo(String s) { sufijo = s == null ? "" : s; aplicarBorde(); repaint(); }

        void setInvalido(boolean b) { invalido = b; aplicarBorde(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets in = getInsets();
            if (getText().isEmpty()) {
                g2.setColor(new Color(160, 168, 180));
                g2.setFont(getFont());
                int y = (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
                g2.drawString(hint, in.left, y);
            }
            if (!sufijo.isEmpty()) {
                g2.setColor(GRIS);
                g2.setFont(F_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(sufijo, getWidth() - 10 - fm.stringWidth(sufijo), y);
            }
            g2.dispose();
        }
    }

    /** Caja de texto gris (RN) o de color (alertas). Se ajusta al ancho disponible. */
    private static class Hint extends JLabel {
        Hint(Color bg, Color fg) {
            setOpaque(true);
            setBackground(bg);
            setForeground(fg);
            setFont(F_PEQ);
            setVerticalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(5, 9, 5, 9));
        }

        static Hint rn(String codigo, String texto) {
            Hint h = new Hint(RN_BG, RN_FG);
            h.setRN(codigo, texto);
            return h;
        }

        void setRN(String codigo, String texto) {
            String cod = "<b><font color='#1E3A5F'>" + codigo + "</font></b>&nbsp;&nbsp;";
            setHtml(cod + texto);
        }

        void setHtml(String contenido) {
            setText("<html>" + contenido + "</html>");
            setVisible(!contenido.isEmpty());
            revalidate();
        }

        @Override
        public Dimension getPreferredSize() {
            Insets in = getInsets();
            int w = getWidth() > 0 ? getWidth() : 240;
            int h = 16;
            View v = (View) getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.setSize(Math.max(1, w - in.left - in.right), 0);
                h = (int) Math.ceil(v.getPreferredSpan(View.Y_AXIS));
            }
            return new Dimension(10, h + in.top + in.bottom);
        }

        @Override
        public Dimension getMinimumSize() { return new Dimension(0, getPreferredSize().height); }

        @Override
        public void setBounds(int x, int y, int w, int h) {
            int antes = getWidth();
            super.setBounds(x, y, w, h);
            if (antes != w && w > 0) EventQueue.invokeLater(this::revalidate);
        }
    }

    /** Tarjeta blanca con borde redondeado fino, pintada a mano para que las esquinas queden limpias */
    private static class Tarjeta extends JPanel {
        Tarjeta(LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(BORDE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
        }
    }

    /** Etiqueta redondeada tipo "badge" */
    private static class Pill extends JLabel {
        private final Color fondo;

        Pill(String texto, Color fondo, Color fg) {
            super(texto, SwingConstants.CENTER);
            this.fondo = fondo;
            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(2, 10, 2, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Círculo azul con el número de sección */
    private static class Circulo extends JLabel {
        Circulo(String n) {
            super(n, SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(24, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AZUL_BOTON);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Limita la cantidad de caracteres de un JTextArea */
    private static class LimiteFilter extends DocumentFilter {
        private final int max;

        LimiteFilter(int max) { this.max = max; }

        @Override
        public void insertString(FilterBypass fb, int off, String s, AttributeSet a) throws BadLocationException {
            if (s != null && fb.getDocument().getLength() + s.length() <= max) {
                super.insertString(fb, off, s, a);
            }
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String s, AttributeSet a) throws BadLocationException {
            int nuevo = fb.getDocument().getLength() - len + (s == null ? 0 : s.length());
            if (nuevo <= max) {
                super.replace(fb, off, len, s, a);
            }
        }
    }

    /** Icono de calendario dibujado (sin imágenes externas) */
    private static class IconoCalendario implements Icon {
        public int getIconWidth() { return 16; }

        public int getIconHeight() { return 16; }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AZUL_BOTON);
            g2.drawRoundRect(x + 1, y + 3, 13, 11, 3, 3);
            g2.fillRect(x + 2, y + 4, 12, 3);
            g2.drawLine(x + 5, y + 1, x + 5, y + 4);
            g2.drawLine(x + 10, y + 1, x + 10, y + 4);
            g2.fillRect(x + 4, y + 9, 2, 2);
            g2.fillRect(x + 7, y + 9, 2, 2);
            g2.fillRect(x + 10, y + 9, 2, 2);
            g2.dispose();
        }
    }

    /** Flecha triangular para navegar entre meses */
    private static class Flecha implements Icon {
        private final boolean izquierda;

        Flecha(boolean izquierda) { this.izquierda = izquierda; }

        public int getIconWidth() { return 10; }

        public int getIconHeight() { return 12; }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AZUL_BOTON);
            int[] xs = izquierda ? new int[]{x + 8, x + 2, x + 8} : new int[]{x + 2, x + 8, x + 2};
            g2.fillPolygon(xs, new int[]{y + 1, y + 6, y + 11}, 3);
            g2.dispose();
        }
    }

    /** Campo de fecha con calendario desplegable (formato dd/mm/aaaa) */
    private static class SelectorFecha extends JPanel {
        private final CampoTexto txt = new CampoTexto("dd/mm/aaaa");
        private final JButton btn = new JButton(new IconoCalendario());
        private final JPopupMenu popup = new JPopupMenu();
        private final JLabel lblMes = new JLabel("", SwingConstants.CENTER);
        private final JButton[] dias = new JButton[42];
        private YearMonth mes = YearMonth.now();
        private LocalDate minimo = LocalDate.now(); // no se permiten fechas anteriores a hoy

        SelectorFecha() {
            super(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(new LineBorder(BORDE, 1, true));

            txt.setBorder(new EmptyBorder(2, 8, 2, 4));
            txt.setPreferredSize(new Dimension(100, 28));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(30, 28));
            btn.setToolTipText("Abrir calendario");
            btn.addActionListener(e -> abrir());
            add(txt, BorderLayout.CENTER);
            add(btn, BorderLayout.EAST);

            popup.setBorder(new LineBorder(BORDE));
            popup.add(crearCalendario());
        }

        void setMinimo(LocalDate m) { minimo = m; }

        String getText() { return txt.getText(); }

        LocalDate getFecha() {
            try {
                return LocalDate.parse(txt.getText().trim(), FMT_FECHA);
            } catch (Exception ex) {
                return null;
            }
        }

        void setFecha(LocalDate d) { txt.setText(d == null ? "" : d.format(FMT_FECHA)); }

        private void abrir() {
            LocalDate f = getFecha();
            mes = YearMonth.from(f != null ? f : LocalDate.now());
            actualizar();
            popup.show(this, 0, getHeight() + 2);
        }

        private JPanel crearCalendario() {
            JPanel p = new JPanel(new BorderLayout(0, 6));
            p.setBackground(Color.WHITE);
            p.setBorder(new EmptyBorder(8, 8, 8, 8));

            JButton ant = navegacion("‹");
            JButton sig = navegacion("›");
            ant.addActionListener(e -> { mes = mes.minusMonths(1); actualizar(); });
            sig.addActionListener(e -> { mes = mes.plusMonths(1); actualizar(); });
            lblMes.setFont(F_BOLD);
            lblMes.setForeground(AZUL_TITULO);
            JPanel cab = new JPanel(new BorderLayout());
            cab.setOpaque(false);
            cab.add(ant, BorderLayout.WEST);
            cab.add(lblMes, BorderLayout.CENTER);
            cab.add(sig, BorderLayout.EAST);

            JPanel grilla = new JPanel(new GridLayout(7, 7, 2, 2));
            grilla.setOpaque(false);
            String[] sem = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do"};
            for (String s : sem) {
                JLabel l = new JLabel(s, SwingConstants.CENTER);
                l.setFont(F_PEQ);
                l.setForeground(GRIS);
                grilla.add(l);
            }
            for (int i = 0; i < 42; i++) {
                JButton b = new JButton();
                b.setFont(F_PEQ);
                b.setMargin(new Insets(0, 0, 0, 0));
                b.setBorder(new EmptyBorder(0, 0, 0, 0));
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
                b.setOpaque(true);
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
                b.setPreferredSize(new Dimension(30, 26));
                b.addActionListener(e -> {
                    LocalDate d = (LocalDate) b.getClientProperty("fecha");
                    setFecha(d);
                    popup.setVisible(false);
                });
                dias[i] = b;
                grilla.add(b);
            }

            JButton hoy = new JButton("Hoy");
            hoy.setFont(F_BOLD);
            hoy.setForeground(AZUL_BOTON);
            hoy.setFocusPainted(false);
            hoy.setBorderPainted(false);
            hoy.setContentAreaFilled(false);
            hoy.setCursor(new Cursor(Cursor.HAND_CURSOR));
            hoy.addActionListener(e -> {
                setFecha(LocalDate.now());
                popup.setVisible(false);
            });
            JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            pie.setOpaque(false);
            pie.add(hoy);

            p.add(cab, BorderLayout.NORTH);
            p.add(grilla, BorderLayout.CENTER);
            p.add(pie, BorderLayout.SOUTH);
            return p;
        }

        private JButton navegacion(String t) {
            JButton b = new JButton(new Flecha("‹".equals(t)));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(32, 24));
            return b;
        }

        private void actualizar() {
            String nombreMes = mes.getMonth().getDisplayName(TextStyle.FULL, ES);
            lblMes.setText(Character.toUpperCase(nombreMes.charAt(0)) + nombreMes.substring(1) + " " + mes.getYear());

            LocalDate primero = mes.atDay(1);
            int desfase = primero.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
            LocalDate hoy = LocalDate.now();
            LocalDate sel = getFecha();
            for (int i = 0; i < 42; i++) {
                LocalDate d = primero.plusDays(i - desfase);
                JButton b = dias[i];
                boolean delMes = YearMonth.from(d).equals(mes);
                boolean habil = minimo == null || !d.isBefore(minimo);
                boolean esSel = d.equals(sel);
                b.putClientProperty("fecha", d);
                b.setText(String.valueOf(d.getDayOfMonth()));
                b.setEnabled(habil);
                if (esSel) {
                    b.setBackground(AZUL_BOTON);
                    b.setForeground(Color.WHITE);
                } else {
                    b.setBackground(d.equals(hoy) ? new Color(219, 232, 252) : Color.WHITE);
                    b.setForeground(!habil ? new Color(200, 205, 215) : delMes ? TEXTO : new Color(160, 170, 185));
                }
            }
            popup.revalidate();
            popup.repaint();
        }
    }

    /** Panel que siempre ocupa el ancho del visor (nunca hay scroll horizontal) */
    private static class PanelScroll extends JPanel implements Scrollable {
        PanelScroll(LayoutManager lm) { super(lm); }

        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }

        public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }

        public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 80; }

        public boolean getScrollableTracksViewportWidth() { return true; }

        public boolean getScrollableTracksViewportHeight() { return false; }
    }

    /** Barra de scroll delgada y redondeada */
    private static class ScrollFino extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            super.configureScrollBarColors();
            thumbColor = new Color(160, 182, 214);
            trackColor = FONDO;
        }

        @Override
        protected JButton createDecreaseButton(int o) { return botonCero(); }

        @Override
        protected JButton createIncreaseButton(int o) { return botonCero(); }

        private JButton botonCero() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(FONDO);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() || isDragging ? new Color(120, 148, 190) : thumbColor);
            g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 8, 8);
            g2.dispose();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmRegistroPrestamo().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
