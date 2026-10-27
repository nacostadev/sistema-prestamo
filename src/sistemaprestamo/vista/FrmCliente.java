package sistemaprestamo.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import sistemaprestamo.controlador.ClienteController;
import sistemaprestamo.modelo.CatalogosCliente;
import sistemaprestamo.modelo.Cliente;
import sistemaprestamo.modelo.ItemCatalogo;

/**
 * Formulario de gestión de clientes (capa de vista).
 * Solo se comunica con ClienteController; no ejecuta consultas SQL.
 */
public class FrmCliente extends JFrame {

    private JTextField txtCodigo;
    private JComboBox<ItemCatalogo> cboTipoDocumento;
    private JTextField txtNumeroDocumento;
    private JTextField txtApellidos;
    private JTextField txtNombres;
    private JTextField txtFechaNacimiento;
    private JComboBox<ItemCatalogo> cboEstadoCivil;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JComboBox<ItemCatalogo> cboCondicionLaboral;
    private JTextField txtNivelIngresos;
    private JTextField txtNombreConyuge;
    private JTextField txtReferenciasPersonales;
    private JTextField txtObservaciones;
    private JTextField txtBusqueda;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnDesactivar;
    private JButton btnListar;
    private JButton btnLimpiar;

    private JTable tblClientes;
    private DefaultTableModel modeloTabla;
    private List<Cliente> clientesMostrados = new ArrayList<>();

    private static final String[] COLUMNAS =
            {"Código", "Documento", "Apellidos", "Nombres", "Teléfono", "Correo", "Condición laboral", "Estado"};

    public FrmCliente() {
        configurarVentana();
        construirFormulario();
        construirTabla();
    }

    private void configurarVentana() {
        setTitle("Gestión de Clientes - Sistema de Préstamo Financiero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void construirFormulario() {
        JPanel pnlFormulario = new JPanel(new GridBagLayout());
        pnlFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Cliente (* obligatorio)"));

        txtCodigo = new JTextField(8);
        txtCodigo.setEditable(false);
        cboTipoDocumento = new JComboBox<>();
        txtNumeroDocumento = new JTextField(14);
        txtApellidos = new JTextField(20);
        txtNombres = new JTextField(20);
        txtFechaNacimiento = new JTextField(10);
        cboEstadoCivil = new JComboBox<>();
        txtDireccion = new JTextField(20);
        txtTelefono = new JTextField(10);
        txtCorreo = new JTextField(20);
        cboCondicionLaboral = new JComboBox<>();
        txtNivelIngresos = new JTextField(10);
        txtNombreConyuge = new JTextField(20);
        txtReferenciasPersonales = new JTextField(20);
        txtObservaciones = new JTextField(20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dos columnas de campos: (etiqueta, campo) a la izquierda y a la derecha.
        agregarFila(pnlFormulario, gbc, 0, "Código:", txtCodigo, "Tipo de documento *:", cboTipoDocumento);
        agregarFila(pnlFormulario, gbc, 1, "N.º de documento *:", txtNumeroDocumento, "Estado civil *:", cboEstadoCivil);
        agregarFila(pnlFormulario, gbc, 2, "Apellidos *:", txtApellidos, "Nombres *:", txtNombres);
        agregarFila(pnlFormulario, gbc, 3, "Fecha nac. (AAAA-MM-DD) *:", txtFechaNacimiento,
                "Dirección *:", txtDireccion);
        agregarFila(pnlFormulario, gbc, 4, "Teléfono (9 dígitos) *:", txtTelefono, "Correo *:", txtCorreo);
        agregarFila(pnlFormulario, gbc, 5, "Condición laboral *:", cboCondicionLaboral,
                "Nivel de ingresos *:", txtNivelIngresos);
        agregarFila(pnlFormulario, gbc, 6, "Cónyuge:", txtNombreConyuge,
                "Referencias personales:", txtReferenciasPersonales);
        agregarFila(pnlFormulario, gbc, 7, "Observaciones:", txtObservaciones, null, null);

        btnRegistrar = crearBoton("Registrar", ClienteController.ACCION_REGISTRAR);
        btnActualizar = crearBoton("Actualizar", ClienteController.ACCION_ACTUALIZAR);
        btnDesactivar = crearBoton("Desactivar", ClienteController.ACCION_DESACTIVAR);
        btnListar = crearBoton("Buscar / Listar", ClienteController.ACCION_LISTAR);
        btnLimpiar = crearBoton("Limpiar", ClienteController.ACCION_LIMPIAR);
        txtBusqueda = new JTextField(18);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnlBotones.add(btnRegistrar);
        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnDesactivar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(new JLabel("   Buscar (documento, apellidos o nombres):"));
        pnlBotones.add(txtBusqueda);
        pnlBotones.add(btnListar);

        JPanel pnlSuperior = new JPanel(new BorderLayout());
        pnlSuperior.add(pnlFormulario, BorderLayout.CENTER);
        pnlSuperior.add(pnlBotones, BorderLayout.SOUTH);
        add(pnlSuperior, BorderLayout.NORTH);
    }

    private void construirTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblClientes = new JTable(modeloTabla);
        tblClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblClientes.getTableHeader().setReorderingAllowed(false);

        // Al hacer clic en una fila se cargan sus datos en el formulario (solo vista).
        tblClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                cargarClienteSeleccionado();
            }
        });

        JScrollPane pnlScroll = new JScrollPane(tblClientes);
        pnlScroll.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        add(pnlScroll, BorderLayout.CENTER);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila,
                             String etiquetaIzq, java.awt.Component campoIzq,
                             String etiquetaDer, java.awt.Component campoDer) {
        agregarCampo(panel, gbc, 0, fila, etiquetaIzq, campoIzq);
        if (etiquetaDer != null) {
            agregarCampo(panel, gbc, 2, fila, etiquetaDer, campoDer);
        }
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int columna, int fila,
                              String etiqueta, java.awt.Component campo) {
        gbc.gridx = columna;
        gbc.gridy = fila;
        gbc.weightx = 0.0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = columna + 1;
        gbc.weightx = 1.0;
        panel.add(campo, gbc);
    }

    private JButton crearBoton(String texto, String accion) {
        JButton boton = new JButton(texto);
        boton.setActionCommand(accion);
        boton.setFocusPainted(false);
        return boton;
    }

    /** Registra el controlador como oyente de todos los botones. */
    public void setControlador(ClienteController controlador) {
        btnRegistrar.addActionListener(controlador);
        btnActualizar.addActionListener(controlador);
        btnDesactivar.addActionListener(controlador);
        btnListar.addActionListener(controlador);
        btnLimpiar.addActionListener(controlador);
        txtBusqueda.setActionCommand(ClienteController.ACCION_LISTAR);
        txtBusqueda.addActionListener(controlador);
    }

    /** Llena los combos con los catálogos leídos de la base de datos. */
    public void cargarCatalogos(CatalogosCliente catalogos) {
        llenarCombo(cboTipoDocumento, catalogos.getTiposDocumento());
        llenarCombo(cboEstadoCivil, catalogos.getEstadosCiviles());
        llenarCombo(cboCondicionLaboral, catalogos.getCondicionesLaborales());
    }

    private void llenarCombo(JComboBox<ItemCatalogo> combo, List<ItemCatalogo> items) {
        combo.removeAllItems();
        for (ItemCatalogo item : items) {
            combo.addItem(item);
        }
    }

    /**
     * Construye un Cliente con los campos del formulario. Los textos vacíos
     * llegan como cadenas vacías; el servicio valida los obligatorios.
     * Lanza IllegalArgumentException si la fecha o los ingresos no tienen formato válido.
     */
    public Cliente obtenerClienteFormulario() {
        Cliente cliente = new Cliente();
        cliente.setCodigo(obtenerCodigoFormulario());
        cliente.setTipoDocumentoCodigo(codigoSeleccionado(cboTipoDocumento));
        cliente.setNumeroDocumento(txtNumeroDocumento.getText());
        cliente.setApellidos(txtApellidos.getText());
        cliente.setNombres(txtNombres.getText());
        cliente.setFechaNacimiento(leerFecha(txtFechaNacimiento.getText()));
        cliente.setEstadoCivilCodigo(codigoSeleccionado(cboEstadoCivil));
        cliente.setDireccion(txtDireccion.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setCorreo(txtCorreo.getText());
        cliente.setCondicionLaboralCodigo(codigoSeleccionado(cboCondicionLaboral));
        cliente.setNivelIngresos(leerMonto(txtNivelIngresos.getText()));
        cliente.setNombreConyuge(txtNombreConyuge.getText());
        cliente.setReferenciasPersonales(txtReferenciasPersonales.getText());
        cliente.setObservaciones(txtObservaciones.getText());
        return cliente;
    }

    public String obtenerCodigoFormulario() {
        return txtCodigo.getText().trim();
    }

    public String obtenerNombreCompletoFormulario() {
        return (txtNombres.getText().trim() + " " + txtApellidos.getText().trim()).trim();
    }

    public String obtenerBusqueda() {
        return txtBusqueda.getText().trim();
    }

    private String codigoSeleccionado(JComboBox<ItemCatalogo> combo) {
        ItemCatalogo item = (ItemCatalogo) combo.getSelectedItem();
        return item == null ? null : item.getCodigo();
    }

    private LocalDate leerFecha(String texto) {
        if (texto.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException excepcion) {
            throw new IllegalArgumentException("La fecha de nacimiento debe tener el formato AAAA-MM-DD.");
        }
    }

    private BigDecimal leerMonto(String texto) {
        if (texto.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim());
        } catch (NumberFormatException excepcion) {
            throw new IllegalArgumentException("El nivel de ingresos debe ser un número válido.");
        }
    }

    /** Muestra la lista de clientes en la tabla. */
    public void mostrarClientes(List<Cliente> clientes) {
        clientesMostrados = new ArrayList<>(clientes);
        modeloTabla.setRowCount(0);
        for (Cliente cliente : clientesMostrados) {
            modeloTabla.addRow(new Object[]{
                cliente.getCodigo(),
                cliente.getTipoDocumentoNombre() + " " + cliente.getNumeroDocumento(),
                cliente.getApellidos(),
                cliente.getNombres(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getCondicionLaboralNombre(),
                cliente.estaActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    /** Carga en el formulario los datos completos del cliente seleccionado en la tabla. */
    private void cargarClienteSeleccionado() {
        int fila = tblClientes.getSelectedRow();
        if (fila < 0 || fila >= clientesMostrados.size()) {
            return;
        }
        Cliente cliente = clientesMostrados.get(fila);
        txtCodigo.setText(cliente.getCodigo());
        seleccionar(cboTipoDocumento, cliente.getTipoDocumentoCodigo());
        txtNumeroDocumento.setText(cliente.getNumeroDocumento());
        txtApellidos.setText(cliente.getApellidos());
        txtNombres.setText(cliente.getNombres());
        txtFechaNacimiento.setText(cliente.getFechaNacimiento().toString());
        seleccionar(cboEstadoCivil, cliente.getEstadoCivilCodigo());
        txtDireccion.setText(cliente.getDireccion());
        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getCorreo());
        seleccionar(cboCondicionLaboral, cliente.getCondicionLaboralCodigo());
        txtNivelIngresos.setText(cliente.getNivelIngresos().toPlainString());
        txtNombreConyuge.setText(textoONulo(cliente.getNombreConyuge()));
        txtReferenciasPersonales.setText(textoONulo(cliente.getReferenciasPersonales()));
        txtObservaciones.setText(textoONulo(cliente.getObservaciones()));
    }

    private void seleccionar(JComboBox<ItemCatalogo> combo, String codigo) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getCodigo().equals(codigo)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String textoONulo(String valor) {
        return valor == null ? "" : valor;
    }

    /** Limpia todos los campos del formulario y quita la selección de la tabla. */
    public void limpiarFormulario() {
        txtCodigo.setText("");
        txtNumeroDocumento.setText("");
        txtApellidos.setText("");
        txtNombres.setText("");
        txtFechaNacimiento.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtNivelIngresos.setText("");
        txtNombreConyuge.setText("");
        txtReferenciasPersonales.setText("");
        txtObservaciones.setText("");
        if (cboTipoDocumento.getItemCount() > 0) {
            cboTipoDocumento.setSelectedIndex(0);
        }
        if (cboEstadoCivil.getItemCount() > 0) {
            cboEstadoCivil.setSelectedIndex(0);
        }
        if (cboCondicionLaboral.getItemCount() > 0) {
            cboCondicionLaboral.setSelectedIndex(0);
        }
        tblClientes.clearSelection();
        txtNumeroDocumento.requestFocusInWindow();
    }

    /** Muestra un mensaje al usuario. */
    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
