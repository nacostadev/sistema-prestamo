package sistemaprestamo.vista;

import sistemaprestamo.modelo.Prestamo;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Vista de Registro de Préstamo Financiero
 * @author ernes
 */
public class FrmPrestamo extends javax.swing.JFrame {

    public FrmPrestamo() {
        initComponents(); // Método generado automáticamente por NetBeans
        this.setLocationRelativeTo(null); // Centrar la ventana en pantalla
        cargarCombosEstaticos(); // Carga de opciones iniciales
        aplicarEstiloModerno(); // Configuración de colores y estilos visuales
    }

    /**
     * Carga de datos iniciales en los ComboBox
     */
    private void cargarCombosEstaticos() {
        // Cargar Modalidades de Pago
        cbModalidadPago.removeAllItems();
        cbModalidadPago.addItem("-- Seleccione Modalidad --");
        cbModalidadPago.addItem("MOD-MEN - Mensual");
        cbModalidadPago.addItem("MOD-QUI - Quincenal");

        // Cargar Monedas
        cbMoneda.removeAllItems();
        cbMoneda.addItem("-- Seleccione Moneda --");
        cbMoneda.addItem("PEN - Soles (S/)");
        cbMoneda.addItem("USD - Dólares ($)");

        // Cargar Tipos de Préstamo
        cbTipoPrestamo.removeAllItems();
        cbTipoPrestamo.addItem("-- Seleccione Tipo --");
        cbTipoPrestamo.addItem("PR-PERS - Préstamo Personal");
        cbTipoPrestamo.addItem("PR-VEH - Préstamo Vehicular");
        cbTipoPrestamo.addItem("PR-HIP - Préstamo Hipotecario");

        // Cargar Garantías
        cbGarantia.removeAllItems();
        cbGarantia.addItem("NINGUNA - Sin Garantía");
        cbGarantia.addItem("GAR-HIP - Hipotecaria");
        cbGarantia.addItem("GAR-VEH - Vehicular");
        cbGarantia.addItem("GAR-MOB - Mobiliaria");

        // Cargar Clientes
        cbCliente.removeAllItems();
        cbCliente.addItem("-- Seleccione Cliente --");
        cbCliente.addItem("CLI-001 - Juan Pérez (DNI: 12345678)");
        cbCliente.addItem("CLI-002 - María López (DNI: 87654321)");
        cbCliente.addItem("CLI-003 - Carlos Ruiz (DNI: 45678912 - Moroso)"); // RN-06 / CA-03
    }

    /**
     * Aplica la paleta de colores y formato visual moderno
     */
    private void aplicarEstiloModerno() {
    // Definición de Colores según prototipo
        Color azulEncabezado = new Color(24, 69, 131);  // #184583
        Color azulBoton = new Color(13, 71, 161);       // #0D47A1
        Color fondoGeneral = new Color(240, 244, 248);    // #F0F4F8

        // 1. Fondo General de la Ventana
        if (jPanel1 != null) {
            jPanel1.setBackground(fondoGeneral);
        }
        this.getContentPane().setBackground(fondoGeneral);

        // 2. Banner Superior (panelHeader)
        if (panelHeader != null) {
            panelHeader.setBackground(azulEncabezado);
        }

        // 3. Título del Banner (Ajustado a jLabel17 que es tu etiqueta del título)
        if (jLabel17 != null) {
            jLabel17.setFont(new Font("Segoe UI", Font.BOLD, 18));
            jLabel17.setForeground(Color.WHITE); // Texto blanco para dar contraste
        }

        // Subtítulo del Banner (jLabel13)
        if (jLabel13 != null) {
            jLabel13.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            jLabel13.setForeground(Color.WHITE);
        }

        // 4. Salto de línea automático para áreas de texto multilínea
        if (txtDestinoCredito != null) {
            txtDestinoCredito.setLineWrap(true);
            txtDestinoCredito.setWrapStyleWord(true);
        }
        if (txtObservaciones != null) {
            txtObservaciones.setLineWrap(true);
            txtObservaciones.setWrapStyleWord(true);
        }

        // 5. Tooltips de ayuda en cajas de texto
        if (txtMontoSolicitado != null) txtMontoSolicitado.setToolTipText("Ingrese monto");
        if (txtPlazo != null) txtPlazo.setToolTipText("Ingrese plazo (meses)");
        if (txtTEA != null) txtTEA.setToolTipText("Ingrese TEA");
        if (txtFechaDesembolso != null) txtFechaDesembolso.setToolTipText("Seleccione una fecha");

        // 6. Estilo del Botón Registrar
        if (btnRegistrar != null) {
            btnRegistrar.setBackground(azulBoton);
            btnRegistrar.setForeground(Color.WHITE);
            btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnRegistrar.setFocusPainted(false);
            btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }

        // 7. Estilo del Botón Cancelar
        if (btnCancelar != null) {
            btnCancelar.setBackground(Color.WHITE);
            btnCancelar.setForeground(azulBoton);
            btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCancelar.setFocusPainted(false);
            btnCancelar.setBorder(javax.swing.BorderFactory.createLineBorder(azulBoton, 1));
            btnCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnRegistrar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelHeader = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cbCliente = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cbTipoPrestamo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtMontoSolicitado = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPlazo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTEA = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cbModalidadPago = new javax.swing.JComboBox<>();
        cbMoneda = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtFechaDesembolso = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDestinoCredito = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        cbGarantia = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 850, 550));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(240, 244, 248));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 650));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnRegistrar.setBackground(new java.awt.Color(13, 71, 161));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("Registrar Préstamo");
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);
        jPanel1.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 470, 150, 40));

        btnCancelar.setForeground(new java.awt.Color(13, 71, 161));
        btnCancelar.setText("Cancelar");
        btnCancelar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 71, 161)));
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        jPanel1.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 470, 100, 40));

        panelHeader.setBackground(new java.awt.Color(24, 69, 131));
        panelHeader.setPreferredSize(new java.awt.Dimension(830, 70));
        panelHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Ingrese la información del préstamo para generar el cronograma de pagos");
        panelHeader.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("REGISTRO DE PRÉSTAMO FINANCIERO");
        panelHeader.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        jPanel1.add(panelHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 770, 90));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cbCliente.setForeground(new java.awt.Color(204, 204, 204));
        cbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<< Seleccione Cliente >>", "CLI-001 - Juan Pérez (DNI: 12345678)", "CLI-002 - María López (DNI: 87654321)", "CLI-003 - Carlos Ruiz (DNI: 45678912 - Moroso)" }));
        jPanel3.add(cbCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 180, -1));

        jLabel2.setText("Cliente");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        cbTipoPrestamo.setForeground(new java.awt.Color(204, 204, 204));
        cbTipoPrestamo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<< Seleccione Tipo >>", "PR-PERS - Préstamo Personal", "PR-VEH - Préstamo Vehicular", "PR-HIP - Préstamo Hipotecario" }));
        jPanel3.add(cbTipoPrestamo, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 180, -1));

        jLabel4.setText("Monto Solicitado");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        txtMontoSolicitado.setForeground(new java.awt.Color(204, 204, 204));
        txtMontoSolicitado.setText("Ingrese monto");
        jPanel3.add(txtMontoSolicitado, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 180, -1));

        jLabel5.setText("Plazo");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        txtPlazo.setForeground(new java.awt.Color(204, 204, 204));
        txtPlazo.setText("Ingrese plazo ( meses )");
        jPanel3.add(txtPlazo, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 180, -1));

        jLabel6.setText("TEA (%)");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        txtTEA.setForeground(new java.awt.Color(204, 204, 204));
        txtTEA.setText("Ingrese TEA");
        jPanel3.add(txtTEA, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 180, -1));

        jLabel7.setText("Modalidad Pago");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));

        cbModalidadPago.setForeground(new java.awt.Color(204, 204, 204));
        cbModalidadPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<< Seleccione Modalidad >>", "MOD-MEN - Mensual", "MOD-QUI - Quincenal" }));
        jPanel3.add(cbModalidadPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, 180, -1));

        cbMoneda.setForeground(new java.awt.Color(204, 204, 204));
        cbMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<< Seleccione Moneda >>", "PEN - Soles (S/)", "USD - Dólares ($)" }));
        jPanel3.add(cbMoneda, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 220, 180, -1));

        jLabel8.setText("Moneda");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, -1));

        jPanel2.setBackground(new java.awt.Color(234, 242, 253));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtFechaDesembolso.setForeground(new java.awt.Color(204, 204, 204));
        txtFechaDesembolso.setText("Selecciones una fecha");
        txtFechaDesembolso.addActionListener(this::txtFechaDesembolsoActionPerformed);
        jPanel2.add(txtFechaDesembolso, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 180, -1));

        jLabel9.setText("Fecha Desembolso");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel15.setText("  (AAAA-MM-DD)");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jPanel3.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 310, 60));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(24, 69, 131));
        jLabel16.setText("Datos del Préstamo");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, -1, -1));

        jLabel1.setText("Tipo Préstamo");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, 330, 340));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jPanel5.setBackground(new java.awt.Color(234, 242, 253));

        jLabel12.setText("Observaciones");

        txtObservaciones.setColumns(20);
        txtObservaciones.setForeground(new java.awt.Color(204, 204, 204));
        txtObservaciones.setRows(5);
        txtObservaciones.setText("Ingrese observaciones adicionales ...");
        jScrollPane2.setViewportView(txtObservaciones);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(206, 206, 206)))
                .addGap(11, 11, 11))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(234, 242, 253));

        jLabel11.setText("Destino Crédito");

        txtDestinoCredito.setColumns(20);
        txtDestinoCredito.setForeground(new java.awt.Color(204, 204, 204));
        txtDestinoCredito.setRows(5);
        txtDestinoCredito.setText("Ingrese el destino del credito ...");
        txtDestinoCredito.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(txtDestinoCredito);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(234, 242, 253));

        cbGarantia.setForeground(new java.awt.Color(204, 204, 204));
        cbGarantia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NINGUNA - Sin Garantía", "GAR-HIP - Hipotecaria", " GAR-VEH - Vehicular", "GAR-MOB - Mobiliaria" }));

        jLabel10.setText("Garantía");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel10))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cbGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(24, 69, 131));
        jLabel14.setText("Información Adicional");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(jLabel14))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, 340, 340));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 530));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
// 1. Validar CA-05: Campos obligatorios
        if (cbCliente.getSelectedIndex() <= 0 ||
            cbTipoPrestamo.getSelectedIndex() <= 0 ||
            cbModalidadPago.getSelectedIndex() <= 0 ||
            cbMoneda.getSelectedIndex() <= 0 ||
            txtMontoSolicitado.getText().trim().isEmpty() ||
            txtPlazo.getText().trim().isEmpty() ||
            txtTEA.getText().trim().isEmpty() ||
            txtFechaDesembolso.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                "Por favor, complete todos los campos obligatorios (*).",
                "Campos Incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. Extraer datos seleccionados
            String clienteSeleccionado = cbCliente.getSelectedItem().toString();
            
            // Validar RN-06 / CA-03: Cliente con morosidad
            if (clienteSeleccionado.contains("Moroso")) {
                JOptionPane.showMessageDialog(this,
                    "El cliente seleccionado presenta préstamos en estado de morosidad o incumplimiento.\nNo se puede registrar el préstamo.",
                    "Restricción de Crédito",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal montoSolicitado = new BigDecimal(txtMontoSolicitado.getText().trim());
            int plazoCuotas = Integer.parseInt(txtPlazo.getText().trim());
            BigDecimal tea = new BigDecimal(txtTEA.getText().trim());
            LocalDate fechaDesembolso = LocalDate.parse(txtFechaDesembolso.getText().trim());

            // 3. Validar CA-04: Monto dentro de límites permitidos
            if (montoSolicitado.compareTo(new BigDecimal("1000")) < 0 || 
                montoSolicitado.compareTo(new BigDecimal("100000")) > 0) {
                
                JOptionPane.showMessageDialog(this,
                    "El monto solicitado excede o está por debajo de los límites permitidos por la política crediticia (S/ 1,000 - S/ 100,000).",
                    "Monto Inválido",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Validar CA-06: Plazo permitido
            if (plazoCuotas <= 0 || plazoCuotas > 120) {
                JOptionPane.showMessageDialog(this,
                    "El plazo ingresado no cumple con las condiciones del tipo de préstamo seleccionado.",
                    "Plazo Inválido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 5. Instanciar el modelo Prestamos
            Prestamo nuevoPrestamo = new Prestamo();
            nuevoPrestamo.setClienteCodigo(clienteSeleccionado.split(" - ")[0]);
            nuevoPrestamo.setTipoPrestamoCodigo(cbTipoPrestamo.getSelectedItem().toString().split(" - ")[0]);
            nuevoPrestamo.setMontoSolicitado(montoSolicitado);
            nuevoPrestamo.setPlazoCuotas(plazoCuotas);
            nuevoPrestamo.setTea(tea);
            nuevoPrestamo.setModalidadPagoCodigo(cbModalidadPago.getSelectedItem().toString().split(" - ")[0]);
            nuevoPrestamo.setMonedaCodigo(cbMoneda.getSelectedItem().toString().split(" - ")[0]);
            nuevoPrestamo.setFechaDesembolso(fechaDesembolso);
            nuevoPrestamo.setGarantiaCodigo(cbGarantia.getSelectedItem().toString().split(" - ")[0]);
            nuevoPrestamo.setDescripcionDestino(txtDestinoCredito.getText().trim());
            nuevoPrestamo.setObservaciones(txtObservaciones.getText().trim());

            // TODO: Enviar al Service / DAO para persistir en Base de Datos

            // 6. CA-02: Confirmación de registro exitoso
            JOptionPane.showMessageDialog(this,
                "El préstamo financiero fue registrado correctamente.",
                "Registro Exitoso",
                JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Verifique que los campos Monto, Plazo y TEA contengan datos numéricos válidos.",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "La fecha de desembolso debe tener el formato AAAA-MM-DD (Ejemplo: 2026-05-01).",
                "Formato de Fecha Incorrecto",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // CA-07: Error general del sistema
            JOptionPane.showMessageDialog(this,
                "Ocurrió un error en el sistema. No fue posible registrar el préstamo financiero.",
                "Error de Transacción",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
limpiarFormulario();
    }                                           

    private void limpiarFormulario() {
        cbCliente.setSelectedIndex(0);
        cbTipoPrestamo.setSelectedIndex(0);
        cbModalidadPago.setSelectedIndex(0);
        cbMoneda.setSelectedIndex(0);
        cbGarantia.setSelectedIndex(0);

        txtMontoSolicitado.setText("");
        txtPlazo.setText("");
        txtTEA.setText("");
        txtFechaDesembolso.setText("");
        txtDestinoCredito.setText("");
        txtObservaciones.setText("");
    }

    /**
     * Método principal para ejecutar la ventana con apariencia del sistema
     */
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(FrmPrestamo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new FrmPrestamo().setVisible(true);
        });
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtFechaDesembolsoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaDesembolsoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaDesembolsoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cbCliente;
    private javax.swing.JComboBox<String> cbGarantia;
    private javax.swing.JComboBox<String> cbModalidadPago;
    private javax.swing.JComboBox<String> cbMoneda;
    private javax.swing.JComboBox<String> cbTipoPrestamo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTextArea txtDestinoCredito;
    private javax.swing.JTextField txtFechaDesembolso;
    private javax.swing.JTextField txtMontoSolicitado;
    private javax.swing.JTextArea txtObservaciones;
    private javax.swing.JTextField txtPlazo;
    private javax.swing.JTextField txtTEA;
    // End of variables declaration//GEN-END:variables
}
