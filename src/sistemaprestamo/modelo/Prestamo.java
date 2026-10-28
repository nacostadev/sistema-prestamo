package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Préstamo (HU-02 y HU-03): representa un registro de Prestamo.Prestamos
 * y contiene sus reglas de negocio y los cálculos del cronograma de pagos
 * (sección REGLAS DE NEGOCIO).
 * La TEA se maneja como fracción decimal (0.2000 = 20 %), igual que en la base
 * de datos. Los atributos terminados en "Nombre" o "Simbolo" se llenan al
 * consultar y no se envían a la base de datos.
 */
public class Prestamo {

    public static final String ESTADO_VIGENTE = "Vigente";
    public static final String ESTADO_MOROSO = "Moroso";
    public static final String ESTADO_INCUMPLIMIENTO = "Incumplimiento";

    private static final MathContext PRECISION = new MathContext(30, RoundingMode.HALF_UP);
    private static final int ESCALA_MONEDA = 2;

    private String codigo;
    private String clienteCodigo;
    private String clienteNombre;
    private String tipoPrestamoCodigo;
    private String tipoPrestamoNombre;
    private BigDecimal montoSolicitado;
    private int plazoCuotas;
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

    public int getPlazoCuotas() {
        return plazoCuotas;
    }

    public void setPlazoCuotas(int plazoCuotas) {
        this.plazoCuotas = plazoCuotas;
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

    // REGLAS DE NEGOCIO

    /**
     * HU-02 RN-02, RN-03 y CA-05: verifica los campos obligatorios y que los
     * importes sean positivos. Ajusta la escala del monto (2) y de la TEA (4).
     */
    public void validar() {
        List<String> faltantes = new ArrayList<>();
        agregarSiVacio(faltantes, clienteCodigo, "cliente");
        agregarSiVacio(faltantes, tipoPrestamoCodigo, "tipo de préstamo");
        if (montoSolicitado == null) {
            faltantes.add("monto solicitado");
        }
        if (plazoCuotas <= 0) {
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
     * HU-02 RN-04 y RN-05 (CA-04, CA-06): el monto debe estar dentro de la
     * política crediticia y el plazo y la TEA dentro de las condiciones del
     * tipo de préstamo.
     */
    public void validarCondiciones(TipoPrestamo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de préstamo seleccionado no está registrado.");
        }
        if (plazoCuotas < tipo.getPlazoMinimo() || plazoCuotas > tipo.getPlazoMaximo()) {
            throw new IllegalArgumentException("El plazo no cumple con las condiciones del tipo de préstamo ("
                    + tipo.getPlazoMinimo() + " a " + tipo.getPlazoMaximo() + " cuotas).");
        }
        if (tea.compareTo(tipo.getTasaMinima()) < 0 || tea.compareTo(tipo.getTasaMaxima()) > 0) {
            throw new IllegalArgumentException("La TEA no cumple con las condiciones del tipo de préstamo ("
                    + comoPorcentaje(tipo.getTasaMinima()) + " a " + comoPorcentaje(tipo.getTasaMaxima()) + ").");
        }
        if (tipo.getMontoMinimo() == null || tipo.getMontoMaximo() == null) {
            throw new IllegalArgumentException(
                    "No existe una política crediticia definida para el tipo de préstamo.");
        }
        if (montoSolicitado.compareTo(tipo.getMontoMinimo()) < 0
                || montoSolicitado.compareTo(tipo.getMontoMaximo()) > 0) {
            throw new IllegalArgumentException("El monto no es válido: debe estar entre "
                    + tipo.getMontoMinimo() + " y " + tipo.getMontoMaximo() + ".");
        }
    }

    /**
     * HU-03 RN-01, RN-02 y RN-08 (CA-04, CA-05): para generar el cronograma el
     * préstamo debe estar vigente y tener monto, plazo, TEA y fecha de
     * desembolso válidos.
     */
    public void validarPuedeGenerarCronograma() {
        if (!estaVigente()) {
            throw new IllegalArgumentException("Solo se puede generar el cronograma de un préstamo vigente.");
        }
        if (montoSolicitado == null || montoSolicitado.signum() <= 0
                || plazoCuotas <= 0
                || tea == null || tea.signum() <= 0
                || fechaDesembolso == null) {
            throw new IllegalArgumentException(
                    "Los datos del préstamo no son válidos (monto, plazo o TEA). No se generó el cronograma.");
        }
    }

    /** HU-03 RN-03: TEM = (1 + TEA)^(1/12) - 1. */
    public BigDecimal calcularTem() {
        double tem = Math.pow(1.0 + tea.doubleValue(), 1.0 / 12.0) - 1.0;
        return BigDecimal.valueOf(tem);
    }

    /** HU-03 RN-04 (método francés): Cuota = P * i / (1 - (1 + i)^-n), a 2 decimales. */
    public BigDecimal calcularCuotaFija() {
        BigDecimal tem = calcularTem();
        BigDecimal factor = BigDecimal.ONE.add(tem).pow(plazoCuotas, PRECISION);
        BigDecimal cuota = montoSolicitado.multiply(tem, PRECISION)
                .multiply(factor, PRECISION)
                .divide(factor.subtract(BigDecimal.ONE), PRECISION);
        return cuota.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /** HU-03 RN-05: Interés = saldo anterior * i, a 2 decimales. */
    public BigDecimal calcularInteres(BigDecimal saldoAnterior) {
        return saldoAnterior.multiply(calcularTem(), PRECISION).setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /**
     * HU-03 RN-05: genera el cronograma del préstamo. Para cada cuota calcula
     * interés, amortización (cuota - interés) y saldo pendiente (saldo anterior
     * - amortización). La última cuota absorbe la diferencia por redondeo para
     * dejar el saldo en cero. La fecha de la cuota k es el desembolso más k meses.
     * Si el total amortizado no coincide con el monto lanza IllegalArgumentException
     * (CA-07).
     */
    public Cronograma generarCronograma() {
        validarPuedeGenerarCronograma();

        Cronograma cronograma = new Cronograma(this);
        BigDecimal cuotaFija = calcularCuotaFija();
        BigDecimal saldo = montoSolicitado.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);

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
            throw new IllegalArgumentException("El total de amortizaciones no coincide con el monto del "
                    + "préstamo. No se guardó el cronograma.");
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
