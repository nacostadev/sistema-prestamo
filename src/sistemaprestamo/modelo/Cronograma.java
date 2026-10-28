package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cronograma de pagos de un préstamo (HU-03 RN-06 y RN-07): conserva las
 * cuotas que calcula Prestamo.generarCronograma() y verifica su consistencia.
 * Una vez generado no se edita manualmente (RN-09).
 */
public class Cronograma {

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

    /** Solo la usa Prestamo al generar el cronograma. */
    void agregarCuota(DetalleCronograma cuota) {
        detalle.add(cuota);
    }

    // REGLAS DE NEGOCIO

    /** Suma de las amortizaciones de todas las cuotas. */
    public BigDecimal calcularTotalAmortizado() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCronograma cuota : detalle) {
            total = total.add(cuota.getAmortizacion());
        }
        return total;
    }

    /** HU-03 CA-07: las amortizaciones suman el monto, el saldo final es cero y ningún importe es negativo. */
    public boolean esConsistente() {
        if (detalle.isEmpty()) {
            return false;
        }
        BigDecimal monto = prestamo.getMontoSolicitado().setScale(2, RoundingMode.HALF_UP);
        boolean cuadra = calcularTotalAmortizado().compareTo(monto) == 0
                && detalle.get(detalle.size() - 1).getSaldoPendiente().signum() == 0;
        for (DetalleCronograma cuota : detalle) {
            if (cuota.getAmortizacion().signum() < 0 || cuota.getSaldoPendiente().signum() < 0) {
                cuadra = false;
            }
        }
        return cuadra;
    }
}
