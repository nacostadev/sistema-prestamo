package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.persistencia.CronogramaRepository;
import sistemaprestamo.persistencia.PrestamoRepository;

/**
 * Lógica de negocio del cronograma de pagos (HU-03).
 * Aquí se realizan TODOS los cálculos financieros (TEM, cuota fija por el
 * método francés, intereses, amortizaciones y saldos), se verifica su
 * consistencia y se aplican las reglas de negocio. Los procedimientos
 * almacenados solo guardan y consultan el resultado ya calculado.
 * No contiene consultas SQL ni código Swing.
 */
public class CronogramaService {

    private static final MathContext PRECISION = new MathContext(30, RoundingMode.HALF_UP);
    private static final int ESCALA_MONEDA = 2;

    private final PrestamoRepository prestamoRepositorio;
    private final CronogramaRepository cronogramaRepositorio;

    public CronogramaService() {
        this.prestamoRepositorio = new PrestamoRepository();
        this.cronogramaRepositorio = new CronogramaRepository();
    }

    /**
     * Genera y guarda el cronograma de un préstamo registrado (HU-03 CA-02).
     * Rechaza préstamos no vigentes (RN-01, CA-05) o con datos inválidos
     * (RN-08, CA-04) y no guarda nada si los cálculos no cuadran (CA-07).
     */
    public Cronograma generarCronograma(String codigoPrestamo) throws SQLException {
        if (codigoPrestamo == null || codigoPrestamo.trim().isEmpty()) {
            throw new IllegalArgumentException("Seleccione un préstamo para generar el cronograma.");
        }

        Prestamo prestamo = prestamoRepositorio.buscarPorCodigo(codigoPrestamo.trim());
        if (prestamo == null) {
            throw new ReglaNegocioException("El préstamo no está registrado.");
        }

        if (!prestamo.estaVigente()) {
            throw new ReglaNegocioException("Solo se puede generar el cronograma de un préstamo vigente.");
        }

        // HU-03 RN-09: un préstamo solo tiene un cronograma. Lo garantiza la
        // restricción UNIQUE de la tabla; TraductorErrores muestra el mensaje.

        List<DetalleCronograma> detalle;
        try {
            detalle = calcular(prestamo.getMontoSolicitado(), prestamo.getPlazoCuotas(),
                    prestamo.getTea(), prestamo.getFechaDesembolso());
        } catch (ArithmeticException excepcion) {
            throw new ReglaNegocioException(
                    "Ocurrió un error en el cálculo del cronograma. No se guardó.", excepcion);
        }

        String codigoCronograma = cronogramaRepositorio.guardar(prestamo.getCodigo(), detalle);
        return new Cronograma(codigoCronograma, prestamo.getCodigo(), detalle);
    }

    /**
     * Calcula el cronograma por el método francés (HU-03 RN-03, RN-04, RN-05).
     * No accede a la base de datos.
     *
     * <pre>
     * TEM     = (1 + TEA)^(1/12) - 1
     * Cuota   = P * i / (1 - (1 + i)^-n)
     * Interés = saldo anterior * i
     * Amortización = cuota - interés
     * Saldo   = saldo anterior - amortización
     * </pre>
     *
     * Los importes se redondean a 2 decimales en cada cuota; la última cuota
     * absorbe la diferencia por redondeo para dejar el saldo en cero. La fecha
     * de la cuota k es la fecha de desembolso más k meses.
     *
     * @param monto monto del préstamo (P)
     * @param plazo número de cuotas (n)
     * @param tea tasa efectiva anual como fracción decimal (0.20 = 20 %)
     * @param fechaDesembolso fecha de desembolso
     * @throws ReglaNegocioException si los datos son inválidos (RN-08) o los
     *         cálculos no cuadran (CA-07)
     */
    public List<DetalleCronograma> calcular(BigDecimal monto, int plazo, BigDecimal tea,
                                            LocalDate fechaDesembolso) {
        if (monto == null || monto.signum() <= 0
                || plazo <= 0
                || tea == null || tea.signum() <= 0
                || fechaDesembolso == null) {
            throw new ReglaNegocioException(
                    "Los datos del préstamo no son válidos (monto, plazo o TEA). No se generó el cronograma.");
        }

        BigDecimal tem = calcularTem(tea);
        BigDecimal cuotaFija = calcularCuotaFija(monto, tem, plazo);

        List<DetalleCronograma> detalle = new ArrayList<>();
        BigDecimal saldo = monto.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
        BigDecimal totalAmortizado = BigDecimal.ZERO;

        for (int numero = 1; numero <= plazo; numero++) {
            BigDecimal interes = saldo.multiply(tem, PRECISION).setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
            BigDecimal amortizacion;
            BigDecimal cuota;
            if (numero == plazo) {
                amortizacion = saldo;
                cuota = interes.add(amortizacion);
            } else {
                amortizacion = cuotaFija.subtract(interes);
                cuota = cuotaFija;
            }
            BigDecimal saldoPendiente = saldo.subtract(amortizacion);

            detalle.add(new DetalleCronograma(numero, fechaDesembolso.plusMonths(numero),
                    saldo, interes, amortizacion, cuota, saldoPendiente));

            totalAmortizado = totalAmortizado.add(amortizacion);
            saldo = saldoPendiente;
        }

        verificarConsistencia(detalle, monto, totalAmortizado);
        return detalle;
    }

    /** TEM = (1 + TEA)^(1/12) - 1. */
    BigDecimal calcularTem(BigDecimal tea) {
        double tem = Math.pow(1.0 + tea.doubleValue(), 1.0 / 12.0) - 1.0;
        return new BigDecimal(tem, PRECISION);
    }

    /** Cuota = P * i / (1 - (1 + i)^-n), redondeada a 2 decimales. */
    BigDecimal calcularCuotaFija(BigDecimal monto, BigDecimal tem, int plazo) {
        BigDecimal factor = BigDecimal.ONE.add(tem).pow(plazo, PRECISION);
        BigDecimal cuota = monto.multiply(tem, PRECISION)
                .multiply(factor, PRECISION)
                .divide(factor.subtract(BigDecimal.ONE), PRECISION);
        return cuota.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /** HU-03 CA-07: el total amortizado debe ser el monto y ningún importe puede ser negativo. */
    private void verificarConsistencia(List<DetalleCronograma> detalle, BigDecimal monto,
                                       BigDecimal totalAmortizado) {
        BigDecimal montoEsperado = monto.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
        boolean consistente = totalAmortizado.compareTo(montoEsperado) == 0
                && detalle.get(detalle.size() - 1).getSaldoPendiente().signum() == 0;
        for (DetalleCronograma cuota : detalle) {
            if (cuota.getAmortizacion().signum() < 0 || cuota.getSaldoPendiente().signum() < 0) {
                consistente = false;
            }
        }
        if (!consistente) {
            throw new ReglaNegocioException(
                    "El total de amortizaciones no coincide con el monto del préstamo. No se guardó el cronograma.");
        }
    }
}
