package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas del cronograma de pagos (HU-03): los cálculos los hace Prestamo y
 * el resultado se guarda en Cronograma.
 * Ejemplo base: P = 10,000, 5 cuotas, TEA 20 %, desembolso 01/04/2026.
 */
public class CronogramaTest {

    private Prestamo crearPrestamo(String monto, int plazo, String tea, String estado) {
        Prestamo prestamo = new Prestamo();
        prestamo.setCodigo("PR0001");
        prestamo.setMontoSolicitado(new BigDecimal(monto));
        prestamo.setPlazoCuotas(plazo);
        prestamo.setTea(new BigDecimal(tea));
        prestamo.setFechaDesembolso(LocalDate.of(2026, 4, 1));
        prestamo.setEstadoNombre(estado);
        return prestamo;
    }

    private Cronograma crearCronogramaEjemplo() {
        return crearPrestamo("10000", 5, "0.2000", "Vigente").generarCronograma();
    }

    @Test
    public void testCalcularTem() {
        System.out.println("calcularTem");
        double result = crearPrestamo("10000", 5, "0.2000", "Vigente").calcularTem().doubleValue();
        assertEquals(0.01530947, result, 0.00000001);
    }

    @Test
    public void testCalcularCuotaFija() {
        System.out.println("calcularCuotaFija");
        BigDecimal result = crearPrestamo("10000", 5, "0.2000", "Vigente").calcularCuotaFija();
        assertEquals(new BigDecimal("2092.79"), result);
    }

    @Test
    public void testCalcularInteres() {
        System.out.println("calcularInteres");
        BigDecimal result = crearPrestamo("10000", 5, "0.2000", "Vigente")
                .calcularInteres(new BigDecimal("10000.00"));
        assertEquals(new BigDecimal("153.09"), result);
    }

    @Test
    public void testGenerarCantidadDeCuotas() {
        System.out.println("generar - cantidad de cuotas");
        Cronograma cronograma = crearCronogramaEjemplo();
        assertEquals(5, cronograma.getDetalle().size());
    }

    @Test
    public void testGenerarPrimeraCuota() {
        System.out.println("generar - primera cuota");
        Cronograma cronograma = crearCronogramaEjemplo();
        DetalleCronograma cuota = cronograma.getDetalle().get(0);
        assertEquals(1, cuota.getNumeroCuota());
        assertEquals(LocalDate.of(2026, 5, 1), cuota.getFechaVencimiento());
        assertEquals(new BigDecimal("10000.00"), cuota.getSaldoAnterior());
        assertEquals(new BigDecimal("153.09"), cuota.getInteres());
        assertEquals(new BigDecimal("1939.70"), cuota.getAmortizacion());
        assertEquals(new BigDecimal("2092.79"), cuota.getCuota());
        assertEquals(new BigDecimal("8060.30"), cuota.getSaldoPendiente());
    }

    @Test
    public void testGenerarUltimaCuotaDejaSaldoEnCero() {
        System.out.println("generar - ultima cuota");
        Cronograma cronograma = crearCronogramaEjemplo();
        DetalleCronograma ultima = cronograma.getDetalle().get(4);
        assertEquals(LocalDate.of(2026, 9, 1), ultima.getFechaVencimiento());
        assertEquals(new BigDecimal("0.00"), ultima.getSaldoPendiente());
    }

    @Test
    public void testCalcularTotalAmortizado() {
        System.out.println("calcularTotalAmortizado");
        Cronograma cronograma = crearCronogramaEjemplo();
        assertEquals(new BigDecimal("10000.00"), cronograma.calcularTotalAmortizado());
    }

    @Test
    public void testEsConsistente() {
        System.out.println("esConsistente");
        assertFalse(new Cronograma(crearPrestamo("10000", 5, "0.2000", "Vigente")).esConsistente());
        assertTrue(crearCronogramaEjemplo().esConsistente());
    }

    @Test
    public void testGenerarPlazoLargo() {
        System.out.println("generar - 240 cuotas");
        Cronograma cronograma = crearPrestamo("200000", 240, "0.0800", "Vigente").generarCronograma();
        assertEquals(240, cronograma.getDetalle().size());
        assertTrue(cronograma.esConsistente());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerarPrestamoNoVigente() {
        System.out.println("generar - prestamo no vigente");
        crearPrestamo("10000", 5, "0.2000", "Moroso").generarCronograma();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerarMontoInvalido() {
        System.out.println("generar - monto invalido");
        crearPrestamo("0", 5, "0.2000", "Vigente").generarCronograma();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerarTeaInvalida() {
        System.out.println("generar - tea invalida");
        crearPrestamo("10000", 5, "0", "Vigente").generarCronograma();
    }
}
