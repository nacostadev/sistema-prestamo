package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prestamo {

    public static final String ESTADO_VIGENTE = "Vigente";
    public static final String ESTADO_MOROSO = "Moroso";
    public static final String ESTADO_INCUMPLIMIENTO = "Incumplimiento";

    private static final MathContext PRECISION = new MathContext(30, RoundingMode.HALF_UP);
    private static final int ESCALA_MONEDA = 2;
    /** RN-08 (propuesta): días que se permite programar un desembolso nuevo. */
    public static final int MAX_DIAS_PROGRAMACION_DESEMBOLSO = 30;
    public static final String MENSAJE_PLAZO_ENTERO =
            "El plazo debe ser un número entero positivo de cuotas (sin decimales). No se generó el cronograma.";

    private String codigo;
    private String clienteCodigo;
    private String clienteNombre;
    private String tipoPrestamoCodigo;
    private String tipoPrestamoNombre;
    private BigDecimal montoSolicitado;
    private BigDecimal plazo;
    private BigDecimal tea;
    private String modalidadPagoCodigo;
    private String modalidadPagoNombre;
    private LocalDate fechaDesembolso;
    private String monedaCodigo;
    private String monedaSimbolo;
    private String garantiaCodigo;
    private String observaciones;
    private String descripcionDestino;
    private String estadoCodigo;
    private String estadoNombre;

    public Prestamo() {
    }

    public boolean estaVigente() {
        return ESTADO_VIGENTE.equals(estadoNombre);
    }

    /** Moroso o en incumplimiento (HU-02 RN-06). */
    public boolean estaEnMora() {
        return ESTADO_MOROSO.equals(estadoNombre) || ESTADO_INCUMPLIMIENTO.equals(estadoNombre);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getClienteCodigo() {
        return clienteCodigo;
    }

    public void setClienteCodigo(String clienteCodigo) {
        this.clienteCodigo = clienteCodigo;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getTipoPrestamoCodigo() {
        return tipoPrestamoCodigo;
    }

    public void setTipoPrestamoCodigo(String tipoPrestamoCodigo) {
        this.tipoPrestamoCodigo = tipoPrestamoCodigo;
    }

    public String getTipoPrestamoNombre() {
        return tipoPrestamoNombre;
    }

    public void setTipoPrestamoNombre(String tipoPrestamoNombre) {
        this.tipoPrestamoNombre = tipoPrestamoNombre;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(BigDecimal montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    /** Plazo como número entero de cuotas; devuelve 0 si el plazo es nulo o no es un entero válido. */
    public int getPlazoCuotas() {
        return tienePlazoEntero() ? plazo.intValueExact() : 0;
    }

    public void setPlazoCuotas(int plazoCuotas) {
        this.plazo = BigDecimal.valueOf(plazoCuotas);
    }

    /** Plazo tal como se leyó (puede ser nulo o decimal); permite validarlo sin truncarlo. */
    public BigDecimal getPlazo() {
        return plazo;
    }

    public void setPlazo(BigDecimal plazo) {
        this.plazo = plazo;
    }

    /** true si el plazo existe y es un número entero (sin parte decimal). */
    public boolean tienePlazoEntero() {
        if (plazo == null) {
            return false;
        }
        try {
            plazo.intValueExact();
            return true;
        } catch (ArithmeticException excepcion) {
            return false;
        }
    }

    public BigDecimal getTea() {
        return tea;
    }

    public void setTea(BigDecimal tea) {
        this.tea = tea;
    }

    public String getModalidadPagoCodigo() {
        return modalidadPagoCodigo;
    }

    public void setModalidadPagoCodigo(String modalidadPagoCodigo) {
        this.modalidadPagoCodigo = modalidadPagoCodigo;
    }

    public String getModalidadPagoNombre() {
        return modalidadPagoNombre;
    }

    public void setModalidadPagoNombre(String modalidadPagoNombre) {
        this.modalidadPagoNombre = modalidadPagoNombre;
    }

    public LocalDate getFechaDesembolso() {
        return fechaDesembolso;
    }

    public void setFechaDesembolso(LocalDate fechaDesembolso) {
        this.fechaDesembolso = fechaDesembolso;
    }

    public String getMonedaCodigo() {
        return monedaCodigo;
    }

    public void setMonedaCodigo(String monedaCodigo) {
        this.monedaCodigo = monedaCodigo;
    }

    public String getMonedaSimbolo() {
        return monedaSimbolo;
    }

    public void setMonedaSimbolo(String monedaSimbolo) {
        this.monedaSimbolo = monedaSimbolo;
    }

    public String getGarantiaCodigo() {
        return garantiaCodigo;
    }

    public void setGarantiaCodigo(String garantiaCodigo) {
        this.garantiaCodigo = garantiaCodigo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDescripcionDestino() {
        return descripcionDestino;
    }

    public void setDescripcionDestino(String descripcionDestino) {
        this.descripcionDestino = descripcionDestino;
    }

    public String getEstadoCodigo() {
        return estadoCodigo;
    }

    public void setEstadoCodigo(String estadoCodigo) {
        this.estadoCodigo = estadoCodigo;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    @Override
    public String toString() {
        return codigo + " - " + clienteNombre + " (" + estadoNombre + ")";
    }


    public void validar() {
        List<String> faltantes = new ArrayList<>();
        agregarSiVacio(faltantes, clienteCodigo, "cliente");
        agregarSiVacio(faltantes, tipoPrestamoCodigo, "tipo de préstamo");
        if (montoSolicitado == null) {
            faltantes.add("monto solicitado");
        }
        if (plazo == null || plazo.signum() <= 0) {
            faltantes.add("plazo (número de cuotas)");
        }
        if (tea == null) {
            faltantes.add("TEA");
        }
        agregarSiVacio(faltantes, modalidadPagoCodigo, "modalidad de pago");
        if (fechaDesembolso == null) {
            faltantes.add("fecha de desembolso");
        }
        agregarSiVacio(faltantes, monedaCodigo, "moneda");
        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Complete los campos obligatorios: " + String.join(", ", faltantes) + ".");
        }

        // RN-08: evitar registrar desembolsos con fechas pasadas o demasiado futuras.
        validarFechaDesembolso();

        if (!tienePlazoEntero()) {
            throw new IllegalArgumentException(MENSAJE_PLAZO_ENTERO);
        }
        if (montoSolicitado.signum() <= 0) {
            throw new IllegalArgumentException("El monto solicitado debe ser mayor que cero.");
        }
        if (tea.signum() <= 0) {
            throw new IllegalArgumentException("La TEA debe ser mayor que cero.");
        }
        validarLongitud(observaciones, "observaciones");
        validarLongitud(descripcionDestino, "descripción del destino del crédito");

        montoSolicitado = montoSolicitado.setScale(2, RoundingMode.HALF_UP);
        tea = tea.setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * RN-08 (propuesta): fecha del día o hasta 30 días después.
     * Se calcula a partir de la fecha de ejecución; no se fija al año 2026.
     */
    public void validarFechaDesembolso() {
        validarFechaDesembolso(LocalDate.now());
    }

    /** Permite comprobar fechas límite usando una fecha actual fija en las pruebas. */
    void validarFechaDesembolso(LocalDate fechaActual) {
        if (fechaDesembolso == null) {
            throw new IllegalArgumentException(
                    "Complete los campos obligatorios: fecha de desembolso.");
        }
        if (fechaDesembolso.isBefore(fechaActual)) {
            throw new IllegalArgumentException(
                    "La fecha de desembolso no puede ser anterior a la fecha actual.");
        }
        if (fechaDesembolso.isAfter(fechaActual.plusDays(MAX_DIAS_PROGRAMACION_DESEMBOLSO))) {
            throw new IllegalArgumentException(
                    "La fecha de desembolso no puede superar los "
                    + MAX_DIAS_PROGRAMACION_DESEMBOLSO
                    + " días posteriores a la fecha actual.");
        }
    }

    /**
     * Situación crediticia del cliente para el registro de préstamos.
     * Retorna null si puede solicitar un préstamo o el motivo de la restricción.
     * Se priorizan las advertencias de incumplimiento y morosidad sobre la de Vigente.
     * Un préstamo finalizado no bloquea una nueva solicitud.
     */
    public static String obtenerRestriccionCliente(Cliente cliente, List<Prestamo> prestamosDelCliente) {
        if (cliente == null) {
            return "El cliente no existe en el sistema.";
        }
        String nombre = cliente.getApellidos() + ", " + cliente.getNombres();
        if (!cliente.estaActivo()) {
            return "El cliente " + nombre + " se encuentra inactivo en el sistema. "
                    + "No se permite registrar un nuevo préstamo.";
        }
        boolean tieneMorosidad = false;
        boolean tieneIncumplimiento = false;
        boolean tieneVigente = false;
        for (Prestamo existente : prestamosDelCliente) {
            String estado = existente.getEstadoNombre();
            if (ESTADO_INCUMPLIMIENTO.equals(estado)) {
                tieneIncumplimiento = true;
            } else if (ESTADO_MOROSO.equals(estado)) {
                tieneMorosidad = true;
            } else if (ESTADO_VIGENTE.equals(estado)) {
                tieneVigente = true;
            }
        }
        if (tieneIncumplimiento) {
            return "El cliente " + nombre + " presenta un préstamo en estado de incumplimiento. "
                    + "No se permite registrar un nuevo préstamo.";
        }
        if (tieneMorosidad) {
            return "El cliente " + nombre + " presenta un préstamo en estado de morosidad. "
                    + "No se permite registrar un nuevo préstamo.";
        }
        if (tieneVigente) {
            return "El cliente " + nombre + " ya tiene un préstamo vigente. "
                    + "Debe cancelarlo antes de solicitar uno nuevo.";
        }
        return null;
    }

    /** Verifica las restricciones de selección y de registro con la misma regla del modelo. */
    public void validarCliente(Cliente cliente, List<Prestamo> prestamosDelCliente) {
        String restriccion = obtenerRestriccionCliente(cliente, prestamosDelCliente);
        if (restriccion != null) {
            throw new IllegalArgumentException(restriccion);
        }
    }

    /** Monto, plazo y TEA contra las condiciones del tipo de préstamo (HU-02 RN-04 y RN-05). */
    public void validarCondiciones(TipoPrestamo tipo) {
        validarPlazoYTasa(tipo);
        validarMonto(tipo);
    }

    /** HU-02 RN-04 / CA-04: lanza MontoInvalidoException si el monto está fuera de los límites. */
    public void validarMonto(TipoPrestamo tipo) {
        exigirTipo(tipo);
        if (tipo.getMontoMinimo() == null || tipo.getMontoMaximo() == null) {
            throw new IllegalArgumentException(
                    "No existe una política crediticia definida para el tipo de préstamo.");
        }
        if (!montoCumple(tipo)) {
            throw new MontoInvalidoException("El monto no es válido: debe estar entre "
                    + tipo.getMontoMinimo() + " y " + tipo.getMontoMaximo() + ".");
        }
    }

    /** HU-02 RN-05 / CA-06: el plazo y la TEA deben cumplir las condiciones del tipo de préstamo. */
    public void validarPlazoYTasa(TipoPrestamo tipo) {
        exigirTipo(tipo);
        if (!plazoCumple(tipo)) {
            throw new IllegalArgumentException("El plazo no cumple con las condiciones del tipo de préstamo ("
                    + tipo.getPlazoMinimo() + " a " + tipo.getPlazoMaximo() + " cuotas).");
        }
        if (!teaCumple(tipo)) {
            throw new IllegalArgumentException("La TEA no cumple con las condiciones del tipo de préstamo ("
                    + comoPorcentaje(tipo.getTasaMinima()) + " a " + comoPorcentaje(tipo.getTasaMaxima()) + ").");
        }
    }

    public boolean montoCumple(TipoPrestamo tipo) {
        return tipo.getMontoMinimo() != null && tipo.getMontoMaximo() != null
                && montoSolicitado.compareTo(tipo.getMontoMinimo()) >= 0
                && montoSolicitado.compareTo(tipo.getMontoMaximo()) <= 0;
    }

    public boolean plazoCumple(TipoPrestamo tipo) {
        return tienePlazoEntero() && getPlazoCuotas() >= tipo.getPlazoMinimo()
                && getPlazoCuotas() <= tipo.getPlazoMaximo();
    }

    public boolean teaCumple(TipoPrestamo tipo) {
        return tea.compareTo(tipo.getTasaMinima()) >= 0 && tea.compareTo(tipo.getTasaMaxima()) <= 0;
    }

    private void exigirTipo(TipoPrestamo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de préstamo seleccionado no está registrado.");
        }
    }

    public void validarPuedeGenerarCronograma() {
        if (!estaVigente()) {
            throw new IllegalArgumentException("Solo se puede generar el cronograma de un préstamo vigente.");
        }
        List<String> faltantes = new ArrayList<>();
        if (montoSolicitado == null) {
            faltantes.add("monto");
        }
        if (plazo == null) {
            faltantes.add("plazo");
        }
        if (tea == null) {
            faltantes.add("TEA");
        }
        if (fechaDesembolso == null) {
            faltantes.add("fecha de desembolso");
        }
        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios del préstamo: "
                    + String.join(", ", faltantes) + ". No se generó el cronograma.");
        }
        if (!tienePlazoEntero()) {
            throw new IllegalArgumentException(MENSAJE_PLAZO_ENTERO);
        }
        if (montoSolicitado.signum() <= 0 || plazo.signum() <= 0 || tea.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Los datos del préstamo no son válidos (monto, plazo o TEA). No se generó el cronograma.");
        }
    }

    /** TEM = (1 + TEA)^(1/12) - 1. Lanza ArithmeticException si no se puede calcular. */
    public BigDecimal calcularTem() {
        if (tea == null) {
            throw new ArithmeticException("No se pudo calcular la TEM: la TEA no está definida.");
        }
        double tem = Math.pow(1.0 + tea.doubleValue(), 1.0 / 12.0) - 1.0;
        if (Double.isNaN(tem) || Double.isInfinite(tem) || tem <= 0) {
            throw new ArithmeticException("No se pudo calcular la TEM con la TEA indicada.");
        }
        return BigDecimal.valueOf(tem);
    }

    /** Cuota fija mensual (método francés). Lanza ArithmeticException si no se puede calcular. */
    public BigDecimal calcularCuotaFija() {
        if (montoSolicitado == null || !tienePlazoEntero() || getPlazoCuotas() <= 0) {
            throw new ArithmeticException("No se pudo calcular la cuota: monto o plazo no válidos.");
        }
        BigDecimal tem = calcularTem();
        BigDecimal factor = BigDecimal.ONE.add(tem).pow(getPlazoCuotas(), PRECISION);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);
        if (denominador.signum() <= 0) {
            throw new ArithmeticException("No se pudo calcular la cuota mensual.");
        }
        BigDecimal cuota = montoSolicitado.multiply(tem, PRECISION)
                .multiply(factor, PRECISION)
                .divide(denominador, PRECISION);
        cuota = cuota.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
        if (cuota.signum() <= 0) {
            throw new ArithmeticException("La cuota mensual calculada no es válida.");
        }
        return cuota;
    }

    public BigDecimal calcularInteres(BigDecimal saldoAnterior) {
        return saldoAnterior.multiply(calcularTem(), PRECISION).setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }


    public Cronograma generarCronograma() {
        validarPuedeGenerarCronograma();

        Cronograma cronograma = new Cronograma(this);
        BigDecimal cuotaFija = calcularCuotaFija();
        BigDecimal saldo = montoSolicitado.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);

        int plazoCuotas = getPlazoCuotas();
        for (int numero = 1; numero <= plazoCuotas; numero++) {
            BigDecimal interes = calcularInteres(saldo);
            BigDecimal amortizacion;
            BigDecimal cuota;
            if (numero == plazoCuotas) {
                amortizacion = saldo;
                cuota = interes.add(amortizacion);
            } else {
                amortizacion = cuotaFija.subtract(interes);
                cuota = cuotaFija;
            }
            BigDecimal saldoPendiente = saldo.subtract(amortizacion);

            cronograma.agregarCuota(new DetalleCronograma(numero, fechaDesembolso.plusMonths(numero),
                    saldo, interes, amortizacion, cuota, saldoPendiente));
            saldo = saldoPendiente;
        }

        if (!cronograma.esConsistente()) {
            throw new CronogramaInconsistenteException("El total de amortizaciones no coincide con el monto del "
                    + "préstamo o el saldo pendiente final no es cero. No se guardó el cronograma.");
        }
        return cronograma;
    }

    private String comoPorcentaje(BigDecimal fraccion) {
        return fraccion.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + " %";
    }

    private void agregarSiVacio(List<String> faltantes, String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            faltantes.add(campo);
        }
    }

    private void validarLongitud(String valor, String campo) {
        if (valor != null && valor.length() > 255) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' no puede superar los 255 caracteres.");
        }
    }
}
