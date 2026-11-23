package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Cronograma {

    /** Diferencia máxima aceptada por redondeo a 2 decimales. */
    private static final BigDecimal TOLERANCIA_REDONDEO = new BigDecimal("0.01");

    private String codigo;
    private final Prestamo prestamo;
    private final List<DetalleCronograma> detalle = new ArrayList<>();

    public Cronograma(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public List<DetalleCronograma> getDetalle() {
        return Collections.unmodifiableList(detalle);
    }

    public static Cronograma reconstruir(String codigo, Prestamo prestamo, List<DetalleCronograma> cuotas) {
        Cronograma cronograma = new Cronograma(prestamo);
        cronograma.codigo = codigo;
        cronograma.detalle.addAll(cuotas);
        return cronograma;
    }

    void agregarCuota(DetalleCronograma cuota) {
        detalle.add(cuota);
    }

    // REGLAS DE NEGOCIO

    public BigDecimal calcularTotalAmortizado() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCronograma cuota : detalle) {
            if (cuota.getAmortizacion() != null) {
                total = total.add(cuota.getAmortizacion());
            }
        }
        return total;
    }

    public BigDecimal calcularTotalIntereses() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCronograma cuota : detalle) {
            if (cuota.getInteres() != null) {
                total = total.add(cuota.getInteres());
            }
        }
        return total;
    }

    public BigDecimal calcularTotalPagado() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCronograma cuota : detalle) {
            if (cuota.getCuota() != null) {
                total = total.add(cuota.getCuota());
            }
        }
        return total;
    }

    /**
     * Valida la consistencia matemática: la suma de amortizaciones coincide con el monto del préstamo,
     * el saldo pendiente final es cero (ambos con tolerancia de redondeo) y cada cuota cuadra.
     */
    public boolean esConsistente() {
        if (detalle.isEmpty() || prestamo == null || prestamo.getMontoSolicitado() == null) {
            return false;
        }
        BigDecimal monto = prestamo.getMontoSolicitado().setScale(2, RoundingMode.HALF_UP);
        for (DetalleCronograma cuota : detalle) {
            if (!cuotaCuadra(cuota)) {
                return false;
            }
        }
        BigDecimal saldoFinal = detalle.get(detalle.size() - 1).getSaldoPendiente();
        return calcularTotalAmortizado().subtract(monto).abs().compareTo(TOLERANCIA_REDONDEO) <= 0
                && saldoFinal.abs().compareTo(TOLERANCIA_REDONDEO) <= 0;
    }

    private boolean cuotaCuadra(DetalleCronograma cuota) {
        if (cuota.getSaldoAnterior() == null || cuota.getInteres() == null
                || cuota.getAmortizacion() == null || cuota.getCuota() == null
                || cuota.getSaldoPendiente() == null) {
            return false;
        }
        if (cuota.getAmortizacion().signum() < 0 || cuota.getSaldoPendiente().signum() < 0) {
            return false;
        }
        BigDecimal saldoEsperado = cuota.getSaldoAnterior().subtract(cuota.getAmortizacion());
        return saldoEsperado.subtract(cuota.getSaldoPendiente()).abs().compareTo(TOLERANCIA_REDONDEO) <= 0;
    }
}
