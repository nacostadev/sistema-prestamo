package sistemaprestamo.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import sistemaprestamo.modelo.Cliente;

/**
 * Formulario de gestión de clientes (capa de vista).
 * Solo se comunica con ClienteController; no ejecuta consultas SQL.
 */
public class FrmCliente extends JFrame {

    private JTextField txtId;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtDni;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnListar;
    private JButton btnLimpiar;

    private JTable tblClientes;
    private DefaultTableModel modeloTabla;

    private static final String[] COLUMNAS =
            {"ID", "Nombres", "Apellidos", "DNI", "Teléfono", "Correo", "Dirección"};

    public FrmCliente() {
        configurarVentana();
        construirFormulario();
        construirTabla();
    }

    private void configurarVentana() {
        setTitle("Gestión de Clientes - Sistema de Préstamo Financiero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setMinimumSize(new Dimension(820, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void construirFormulario() {
        JPanel pnlFormulario = new JPanel(new GridBagLayout());
        pnlFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        pnlFormulario.setPreferredSize(new Dimension(860, 220));

        txtId = new JTextField(8);
        txtId.setEditable(false);
        txtNombres = new JTextField(20);
        txtApellidos = new JTextField(20);
        txtDni = new JTextField(12);
        txtTelefono = new JTextField(12);
        txtCorreo = new JTextField(20);
        txtDireccion = new JTextField(20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(pnlFormulario, gbc, 0, "ID:", txtId);
        agregarCampo(pnlFormulario, gbc, 1, "Nombres:", txtNombres);
        agregarCampo(pnlFormulario, gbc, 2, "Apellidos:", txtApellidos);
        agregarCampo(pnlFormulario, gbc, 3, "DNI:", txtDni);
        agregarCampo(pnlFormulario, gbc, 4, "Teléfono:", txtTelefono);
        agregarCampo(pnlFormulario, gbc, 5, "Correo:", txtCorreo);
        agregarCampo(pnlFormulario, gbc, 6, "Dirección:", txtDireccion);

        btnRegistrar = crearBoton("Registrar", ClienteController.ACCION_REGISTRAR);
        btnActualizar = crearBoton("Actualizar", ClienteController.ACCION_ACTUALIZAR);
        btnEliminar = crearBoton("Eliminar", ClienteController.ACCION_ELIMINAR);
        btnListar = crearBoton("Listar", ClienteController.ACCION_LISTAR);
        btnLimpiar = crearBoton("Limpiar", ClienteController.ACCION_LIMPIAR);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnlBotones.add(btnRegistrar);
        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnEliminar);
        pnlBotones.add(btnListar);
        pnlBotones.add(btnLimpiar);

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

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
                              String etiqueta, JTextField campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.0;
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(lblEtiqueta.getFont().deriveFont(Font.PLAIN));
        panel.add(lblEtiqueta, gbc);

        gbc.gridx = 1;
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
        btnEliminar.addActionListener(controlador);
        btnListar.addActionListener(controlador);
        btnLimpiar.addActionListener(controlador);
    }

    /** Construye un objeto Cliente a partir de los campos del formulario. */
    public Cliente obtenerClienteFormulario() {
        int id = 0;
        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ignored) {
            id = 0;
        }
        return new Cliente(
                id,
                txtNombres.getText().trim(),
                txtApellidos.getText().trim(),
                txtDni.getText().trim(),
                txtTelefono.getText().trim(),
                txtCorreo.getText().trim(),
                txtDireccion.getText().trim()
        );
    }

    /** Muestra la lista de clientes en la tabla. */
    public void mostrarClientes(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        for (Cliente cliente : clientes) {
            modeloTabla.addRow(new Object[]{
                cliente.getIdCliente(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getDni(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getDireccion()
            });
        }
    }

    /** Carga en el formulario los datos de la fila seleccionada en la tabla. */
    private void cargarClienteSeleccionado() {
        int fila = tblClientes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtId.setText(String.valueOf(modeloTabla.getValueAt(fila, 0)));
        txtNombres.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
        txtApellidos.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
        txtDni.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
        txtTelefono.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)));
        txtCorreo.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));
        txtDireccion.setText(String.valueOf(modeloTabla.getValueAt(fila, 6)));
    }

    /** Limpia todos los campos del formulario y quita la selección de la tabla. */
    public void limpiarFormulario() {
        txtId.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        tblClientes.clearSelection();
        txtNombres.requestFocusInWindow();
    }

    /** Muestra un mensaje al usuario. */
    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
