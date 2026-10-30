package sistemaprestamo.vista;
import java.util.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import sistemaprestamo.modelo.*;
import sistemaprestamo.controlador.CronogramaController;

/** Diseño editable en FrmCronogramaPagos.form. Los cálculos se mantienen en el modelo. */
public class FrmCronogramaPagos extends javax.swing.JFrame {
    private CronogramaController controlador;
    private java.util.List<Prestamo> prestamos = new ArrayList<>();
    private boolean cargando;
    private static final DateTimeFormatter FECHA=DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public FrmCronogramaPagos() { this(true); }
    public FrmCronogramaPagos(boolean cargarDatos) {
        initComponents();
        tblCronograma.getTableHeader().setReorderingAllowed(false);
        scrollDetalle.setColumnHeaderView(tblCronograma.getTableHeader());
        tblCronograma.setRowHeight(24);
        JScrollPane scroll=new JScrollPane(jPanel1);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(scroll);
        controlador=new CronogramaController(this);
        if(cargarDatos) controlador.iniciar();
    }
    public void seleccionarPrestamo(String codigo) {
        for(int i=0;i<prestamos.size();i++) if(prestamos.get(i).getCodigo().equals(codigo)) { cboPrestamo.setSelectedIndex(i+1); return; }
        mostrarMensaje("El préstamo no se encuentra registrado.","Validación",JOptionPane.WARNING_MESSAGE);
    }
    public void mostrarPrestamos(java.util.List<Prestamo> lista) {
        cargando=true; prestamos=new ArrayList<>(lista); cboPrestamo.removeAllItems(); cboPrestamo.addItem("<<Seleccionar>>");
        for(Prestamo p:prestamos) cboPrestamo.addItem(p.toString());
        cargando=false; mostrarPrestamo(null); limpiarCronograma();
    }
    public String obtenerCodigoPrestamo() { int i=cboPrestamo.getSelectedIndex()-1; return i<0 ? "" : prestamos.get(i).getCodigo(); }
    public void mostrarPrestamo(Prestamo p) {
        txtMonto.setText(p==null ? "" : dinero(p.getMonedaSimbolo(),p.getMontoSolicitado()));
        txtPlazo.setText(p==null ? "" : String.valueOf(p.getPlazoCuotas()));
        txtTea.setText(p==null ? "" : p.getTea().movePointRight(2).stripTrailingZeros().toPlainString());
        txtFecha.setText(p==null ? "" : p.getFechaDesembolso().format(FECHA));
        lblEstado.setText(p==null ? "Seleccione un préstamo para ver sus datos." : "Cliente: "+p.getClienteNombre()+" | Estado del préstamo: "+p.getEstadoNombre());
    }
    public void mostrarCronograma(Cronograma c) {
        DefaultTableModel m=(DefaultTableModel)tblCronograma.getModel(); m.setRowCount(0);
        Prestamo p=c.getPrestamo();
        for(DetalleCronograma d:c.getDetalle()) m.addRow(new Object[]{d.getNumeroCuota(),d.getFechaVencimiento().format(FECHA),dinero(p.getMonedaSimbolo(),d.getSaldoAnterior()),dinero(p.getMonedaSimbolo(),d.getInteres()),dinero(p.getMonedaSimbolo(),d.getAmortizacion()),dinero(p.getMonedaSimbolo(),d.getCuota()),dinero(p.getMonedaSimbolo(),d.getSaldoPendiente())});
        lblResumen.setText("Cronograma "+c.getCodigo()+" | Total amortizado: "+dinero(p.getMonedaSimbolo(),c.calcularTotalAmortizado())+" | Intereses: "+dinero(p.getMonedaSimbolo(),c.calcularTotalIntereses()));
        lblTem.setText("TEM: "+p.calcularTem().movePointRight(2).setScale(6,java.math.RoundingMode.HALF_UP)+" % | Cuota fija: "+dinero(p.getMonedaSimbolo(),p.calcularCuotaFija()));
    }
    public void limpiarCronograma() { ((DefaultTableModel)tblCronograma.getModel()).setRowCount(0); lblResumen.setText("El cronograma guardado se mostrará aquí."); lblTem.setText("TEM y cuota fija se calcularán al generar."); }
    public void mostrarMensaje(String mensaje,String titulo,int tipo) { JOptionPane.showMessageDialog(this,mensaje,titulo,tipo); }
    public boolean confirmar(String mensaje,String titulo) { return JOptionPane.showConfirmDialog(this,mensaje,titulo,JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION; }
    private String dinero(String simbolo,java.math.BigDecimal valor) { return (simbolo==null ? "" : simbolo+" ")+String.format(Locale.US,"%,.2f",valor); }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        panelPrestamo = new javax.swing.JPanel();
        lblPrestamo = new javax.swing.JLabel();
        cboPrestamo = new javax.swing.JComboBox();
        btnActualizar = new javax.swing.JButton();
        lblMonto = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        lblPlazo = new javax.swing.JLabel();
        txtPlazo = new javax.swing.JTextField();
        lblTea = new javax.swing.JLabel();
        txtTea = new javax.swing.JTextField();
        lblFecha = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        btnGenerar = new javax.swing.JButton();
        lblTem = new javax.swing.JLabel();
        panelCronograma = new javax.swing.JPanel();
        lblDetalle = new javax.swing.JLabel();
        scrollDetalle = new javax.swing.JScrollPane();
        tblCronograma = new javax.swing.JTable();
        lblResumen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(240, 244, 248));
        jPanel1.setPreferredSize(new java.awt.Dimension(1100, 850));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelHeader.setBackground(new java.awt.Color(24, 69, 131));
        panelHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setText("CRONOGRAMA DE PAGOS");
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        panelHeader.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 12, 1000, 28));

        lblSubtitulo.setText("Seleccione un préstamo registrado para generar o consultar sus cuotas");
        lblSubtitulo.setForeground(new java.awt.Color(255, 255, 255));
        panelHeader.add(lblSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 43, 1000, 20));

        jPanel1.add(panelHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1100, 75));

        panelPrestamo.setBackground(new java.awt.Color(255, 255, 255));
        panelPrestamo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPrestamo.setText("Préstamo registrado *");
        lblPrestamo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        panelPrestamo.add(lblPrestamo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 12, 750, 20));

        cboPrestamo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPrestamoActionPerformed(evt);
            }
        });
        panelPrestamo.add(cboPrestamo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 36, 800, 30));

        btnActualizar.setText("Actualizar préstamos");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        panelPrestamo.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 36, 210, 30));

        lblMonto.setText("Monto del préstamo");
        lblMonto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        panelPrestamo.add(lblMonto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 82, 245, 18));

        txtMonto.setEditable(false);
        panelPrestamo.add(txtMonto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 245, 28));

        lblPlazo.setText("Plazo (número de cuotas)");
        lblPlazo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        panelPrestamo.add(lblPlazo, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 82, 245, 18));

        txtPlazo.setEditable(false);
        panelPrestamo.add(txtPlazo, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 105, 245, 28));

        lblTea.setText("TEA (%)");
        lblTea.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        panelPrestamo.add(lblTea, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 82, 245, 18));

        txtTea.setEditable(false);
        panelPrestamo.add(txtTea, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 105, 245, 28));

        lblFecha.setText("Fecha de desembolso");
        lblFecha.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        panelPrestamo.add(lblFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 82, 245, 18));

        txtFecha.setEditable(false);
        panelPrestamo.add(txtFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 105, 245, 28));

        lblEstado.setText("Seleccione un préstamo para ver sus datos.");
        panelPrestamo.add(lblEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 145, 1030, 20));

        btnGenerar.setText("Generar cronograma de pagos");
        btnGenerar.setBackground(new java.awt.Color(30, 74, 138));
        btnGenerar.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });
        panelPrestamo.add(btnGenerar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 290, 32));

        lblTem.setText("TEM y cuota fija se calcularán al generar.");
        panelPrestamo.add(lblTem, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 185, 700, 24));

        jPanel1.add(panelPrestamo, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 90, 1070, 230));

        panelCronograma.setBackground(new java.awt.Color(255, 255, 255));
        panelCronograma.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDetalle.setText("Detalle del cronograma");
        lblDetalle.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        panelCronograma.add(lblDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 12, 1000, 24));

        tblCronograma.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "N° de cuota", "Fecha de vencimiento", "Saldo anterior", "Interés", "Amortización", "Cuota", "Saldo pendiente"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollDetalle.setViewportView(tblCronograma);

        panelCronograma.add(scrollDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 48, 1030, 370));

        lblResumen.setText("El cronograma guardado se mostrará aquí.");
        lblResumen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        panelCronograma.add(lblResumen, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 435, 1030, 40));

        jPanel1.add(panelCronograma, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 335, 1070, 495));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1100, 850));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void cboPrestamoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPrestamoActionPerformed
        if(!cargando && controlador!=null) controlador.seleccionar();
    }//GEN-LAST:event_cboPrestamoActionPerformed
    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        controlador.iniciar();
    }//GEN-LAST:event_btnActualizarActionPerformed
    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        controlador.generar();
    }//GEN-LAST:event_btnGenerarActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JComboBox cboPrestamo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblDetalle;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblMonto;
    private javax.swing.JLabel lblPlazo;
    private javax.swing.JLabel lblPrestamo;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTea;
    private javax.swing.JLabel lblTem;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelCronograma;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelPrestamo;
    private javax.swing.JScrollPane scrollDetalle;
    private javax.swing.JTable tblCronograma;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JTextField txtPlazo;
    private javax.swing.JTextField txtTea;
    // End of variables declaration//GEN-END:variables
}
