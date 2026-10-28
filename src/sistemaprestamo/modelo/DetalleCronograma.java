package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;


public class DetalleCronograma {

    private final int numeroCuota;
    private final LocalDate fechaVencimiento;
    private final BigDecimal saldoAnterior;
    private final BigDecimal interes;
    private final BigDecimal amortizacion;
    private final BigDecimal cuota;
    private final BigDecimal saldoPendiente;

    public DetalleCronograma(int numeroCuota, LocalDate fechaVencimiento,
                             BigDecimal saldoAnterior, BigDecimal interes,
                             BigDecimal amortizacion, BigDecimal cuota,
                             BigDecimal saldoPendiente) {
        this.numeroCuota = numeroCuota;
        this.fechaVencimiento = fechaVencimiento;
        this.saldoAnterior = saldoAnterior;
        this.interes = interes;
        this.amortizacion = amortizacion;
        this.cuota = cuota;
        this.saldoPendiente = saldoPendiente;
    }

    public int getNumeroCuota() {
        return numeroCuota;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public BigDecimal getAmortizacion() {
        return amortizacion;
    }

    public BigDecimal getCuota() {
        return cuota;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }
}
