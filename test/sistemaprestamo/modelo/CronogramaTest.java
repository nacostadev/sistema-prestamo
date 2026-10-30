package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias de caja blanca con la técnica de cobertura de caminos (HU-03).
 *
 * Para cada método con decisiones se identifica el flujo, se calcula la complejidad
 * ciclomática (CC = decisiones + 1, equivalente a E - N + 2) y se diseña un caso de
 * prueba por cada camino independiente. Las tablas muestran los casos (CP) y su
 * resultado esperado.
 *
 * <pre>
 * ===========================================================================
 * MÉTODO 1: Prestamo.validarPuedeGenerarCronograma()          CC = 8
 * Decisiones: if (!estaVigente)  +  if (c1 || c2 || c3 || c4 || c5 || c6)
 *             = 1 + 6 = 7 decisiones
 *   c1 monto == null      c2 monto ≤ 0      c3 plazo ≤ 0
 *   c4 TEA == null        c5 TEA ≤ 0        c6 fecha == null
 *
 * NRO   CAMINO                Estado  Monto   Plazo  TEA    Fecha   ESPERADO
 * CP01  préstamo no vigente   Moroso  10000   5      0.20   ok      IllegalArgumentException (estado)
 * CP02  c1 verdadera          Vigente null    5      0.20   ok      IllegalArgumentException (datos)
 * CP03  c2 verdadera          Vigente 0       5      0.20   ok      IllegalArgumentException (datos)
 * CP04  c3 verdadera          Vigente 10000   0      0.20   ok      IllegalArgumentException (datos)
 * CP05  c4 verdadera          Vigente 10000   5      null   ok      IllegalArgumentException (datos)
 * CP06  c5 verdadera          Vigente 10000   5      0      ok      IllegalArgumentException (datos)
 * CP07  c6 verdadera          Vigente 10000   5      0.20   null    IllegalArgumentException (datos)
 * CP08  todo válido           Vigente 10000   5      0.20   ok      no lanza excepción
 *
 * ===========================================================================
 * MÉTODO 2: Prestamo.generarCronograma()                      CC = 4   (E = 13, N = 11)
 * Decisiones: for (cada cuota)  +  if (numero == plazo)  +  if (!esConsistente)
 *
 * NRO   CAMINO                                           Monto     Plazo  TEA     ESPERADO
 * CP09  bucle 1 vez, solo rama "última cuota"            1000      1      0.20    1 cuota: 15.31 + 1000.00 = 1015.31
 * CP10  bucle varias veces: rama "cuota fija" y al       10000     5      0.20    5 cuotas: 4 de 2092.79 y última de 2092.78
 *       final rama "última cuota"
 * CP11  cronograma inconsistente (saldo negativo)        199329.80 320    0.5776  IllegalArgumentException (CA-07)
 * --    bucle 0 veces: camino infactible, porque plazo ≤ 0 se rechaza antes del bucle (CP04).
 *
 * ===========================================================================
 * MÉTODO 3: Cronograma.esConsistente()                        CC = 6
 * Decisiones: if (vacío)  +  (total == monto && saldo final == 0)  +  for (cuotas)
 *             + if (amortización &lt; 0 || saldo &lt; 0)  =  1 + 1 + 1 + 2 = 5 decisiones
 * Monto del préstamo = 1000.00
 *
 * NRO   CAMINO                                          ESPERADO
 * CP12  sin cuotas                                      false
 * CP13  total distinto del monto (saldo final en 0)     false
 * CP14  total igual al monto, saldo final distinto de 0 false
 * CP15  todo cuadra y todas las cuotas son válidas      true
 * CP16  una cuota con amortización negativa             false
 * CP17  una cuota con saldo pendiente negativo          false
 * </pre>
 */
public class CronogramaTest {

    private static final String MENSAJE_NO_VIGENTE = "Solo se puede generar el cronograma de un préstamo vigente";
    private static final String MENSAJE_DATOS = "Los datos del préstamo no son válidos";
    private static final String MENSAJE_INCONSISTENTE = "El total de amortizaciones no coincide";

    // ------------------------------------------------------------------
    // Datos de apoyo
    // ------------------------------------------------------------------

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

    /** Verifica que la validación lance IllegalArgumentException con el mensaje esperado. */
    private void assertValidacionFalla(Prestamo prestamo, String inicioMensaje) {
        try {
            prestamo.validarPuedeGenerarCronograma();
            fail("Debió lanzar IllegalArgumentException");
        } catch (IllegalArgumentException esperada) {
            assertTrue(esperada.getMessage(), esperada.getMessage().startsWith(inicioMensaje));
        }
    }

    private DetalleCronograma crearCuota(int numero, String saldoAnterior, String amortizacion,
                                         String saldoPendiente) {
        return new DetalleCronograma(numero, LocalDate.of(2026, 4, 1).plusMonths(numero),
                new BigDecimal(saldoAnterior), new BigDecimal("0.00"),
                new BigDecimal(amortizacion), new BigDecimal(amortizacion),
                new BigDecimal(saldoPendiente));
    }

    /** Cronograma de un préstamo de 1000.00 con las cuotas indicadas. */
    private Cronograma crearCronograma(DetalleCronograma... cuotas) {
        Cronograma cronograma = new Cronograma(crearPrestamo("1000", 2, "0.2000", "Vigente"));
        for (DetalleCronograma cuota : cuotas) {
            cronograma.agregarCuota(cuota);
        }
        return cronograma;
    }

    // ------------------------------------------------------------------
    // MÉTODO 1: Prestamo.validarPuedeGenerarCronograma() - CC = 8
    // ------------------------------------------------------------------

    @Test
    public void testValidarPuedeGenerarCP01PrestamoNoVigente() {
        System.out.println("validarPuedeGenerarCronograma - CP01 no vigente");
        assertValidacionFalla(crearPrestamo("10000", 5, "0.2000", "Moroso"), MENSAJE_NO_VIGENTE);
    }

    @Test
    public void testValidarPuedeGenerarCP02MontoNulo() {
        System.out.println("validarPuedeGenerarCronograma - CP02 monto nulo");
        Prestamo prestamo = crearPrestamo("10000", 5, "0.2000", "Vigente");
        prestamo.setMontoSolicitado(null);
        assertValidacionFalla(prestamo, MENSAJE_DATOS);
    }

    @Test
    public void testValidarPuedeGenerarCP03MontoCero() {
        System.out.println("validarPuedeGenerarCronograma - CP03 monto menor o igual a cero");
        assertValidacionFalla(crearPrestamo("0", 5, "0.2000", "Vigente"), MENSAJE_DATOS);
    }

    @Test
    public void testValidarPuedeGenerarCP04PlazoCero() {
        System.out.println("validarPuedeGenerarCronograma - CP04 plazo menor o igual a cero");
        assertValidacionFalla(crearPrestamo("10000", 0, "0.2000", "Vigente"), MENSAJE_DATOS);
    }

    @Test
    public void testValidarPuedeGenerarCP05TeaNula() {
        System.out.println("validarPuedeGenerarCronograma - CP05 TEA nula");
        Prestamo prestamo = crearPrestamo("10000", 5, "0.2000", "Vigente");
        prestamo.setTea(null);
        assertValidacionFalla(prestamo, MENSAJE_DATOS);
    }

    @Test
    public void testValidarPuedeGenerarCP06TeaCero() {
        System.out.println("validarPuedeGenerarCronograma - CP06 TEA menor o igual a cero");
        assertValidacionFalla(crearPrestamo("10000", 5, "0", "Vigente"), MENSAJE_DATOS);
    }

    @Test
    public void testValidarPuedeGenerarCP07FechaNula() {
        System.out.println("validarPuedeGenerarCronograma - CP07 fecha de desembolso nula");
        Prestamo prestamo = crearPrestamo("10000", 5, "0.2000", "Vigente");
        prestamo.setFechaDesembolso(null);
        assertValidacionFalla(prestamo, MENSAJE_DATOS);
    }

    @Test
    public void testValidarPuedeGenerarCP08DatosValidos() {
        System.out.println("validarPuedeGenerarCronograma - CP08 todo valido");
        Prestamo prestamo = crearPrestamo("10000", 5, "0.2000", "Vigente");
        prestamo.validarPuedeGenerarCronograma();
        assertTrue(prestamo.estaVigente());
    }

    // ------------------------------------------------------------------
    // MÉTODO 2: Prestamo.generarCronograma() - CC = 4
    // ------------------------------------------------------------------

    @Test
    public void testGenerarCP09UnaSolaCuota() {
        System.out.println("generarCronograma - CP09 bucle una vez, ultima cuota");
        Cronograma cronograma = crearPrestamo("1000", 1, "0.2000", "Vigente").generarCronograma();
        assertEquals(1, cronograma.getDetalle().size());
        DetalleCronograma cuota = cronograma.getDetalle().get(0);
        assertEquals(1, cuota.getNumeroCuota());
        assertEquals(LocalDate.of(2026, 5, 1), cuota.getFechaVencimiento());
        assertEquals(new BigDecimal("1000.00"), cuota.getSaldoAnterior());
        assertEquals(new BigDecimal("15.31"), cuota.getInteres());
        assertEquals(new BigDecimal("1000.00"), cuota.getAmortizacion());
        assertEquals(new BigDecimal("1015.31"), cuota.getCuota());
        assertEquals(new BigDecimal("0.00"), cuota.getSaldoPendiente());
        assertTrue(cronograma.esConsistente());
    }

    @Test
    public void testGenerarCP10VariasCuotasConAjusteFinal() {
        System.out.println("generarCronograma - CP10 cuota fija y ultima cuota");
        Cronograma cronograma = crearPrestamo("10000", 5, "0.2000", "Vigente").generarCronograma();
        assertEquals(5, cronograma.getDetalle().size());
        for (int i = 0; i < 4; i++) {
            assertEquals(new BigDecimal("2092.79"), cronograma.getDetalle().get(i).getCuota());
        }
        DetalleCronograma ultima = cronograma.getDetalle().get(4);
        assertEquals(new BigDecimal("2092.78"), ultima.getCuota());
        assertEquals(ultima.getSaldoAnterior(), ultima.getAmortizacion());
        assertEquals(new BigDecimal("0.00"), ultima.getSaldoPendiente());
        assertTrue(cronograma.esConsistente());
    }

    @Test
    public void testGenerarCP11CronogramaInconsistente() {
        System.out.println("generarCronograma - CP11 cronograma inconsistente");
        Prestamo prestamo = crearPrestamo("199329.80", 320, "0.5776", "Vigente");
        try {
            prestamo.generarCronograma();
            fail("Debió lanzar IllegalArgumentException");
        } catch (IllegalArgumentException esperada) {
            assertTrue(esperada.getMessage(), esperada.getMessage().startsWith(MENSAJE_INCONSISTENTE));
        }
    }

    // ------------------------------------------------------------------
    // MÉTODO 3: Cronograma.esConsistente() - CC = 6
    // ------------------------------------------------------------------

    @Test
    public void testEsConsistenteCP12SinCuotas() {
        System.out.println("esConsistente - CP12 sin cuotas");
        assertFalse(crearCronograma().esConsistente());
    }

    @Test
    public void testEsConsistenteCP13TotalDistintoDelMonto() {
        System.out.println("esConsistente - CP13 total amortizado distinto del monto");
        // Total amortizado 800.00 (monto 1000.00) y saldo final 0.00: solo falla el total.
        Cronograma cronograma = crearCronograma(
                crearCuota(1, "1000.00", "400.00", "600.00"),
                crearCuota(2, "600.00", "400.00", "0.00"));
        assertFalse(cronograma.esConsistente());
    }

    @Test
    public void testEsConsistenteCP14SaldoFinalDistintoDeCero() {
        System.out.println("esConsistente - CP14 saldo final distinto de cero");
        Cronograma cronograma = crearCronograma(
                crearCuota(1, "1000.00", "1000.00", "5.00"));
        assertFalse(cronograma.esConsistente());
    }

    @Test
    public void testEsConsistenteCP15TodoCuadra() {
        System.out.println("esConsistente - CP15 todo cuadra");
        Cronograma cronograma = crearCronograma(
                crearCuota(1, "1000.00", "600.00", "400.00"),
                crearCuota(2, "400.00", "400.00", "0.00"));
        assertTrue(cronograma.esConsistente());
    }

    @Test
    public void testEsConsistenteCP16AmortizacionNegativa() {
        System.out.println("esConsistente - CP16 amortizacion negativa");
        Cronograma cronograma = crearCronograma(
                crearCuota(1, "1000.00", "-50.00", "1050.00"),
                crearCuota(2, "1050.00", "1050.00", "0.00"));
        assertFalse(cronograma.esConsistente());
    }

    @Test
    public void testEsConsistenteCP17SaldoPendienteNegativo() {
        System.out.println("esConsistente - CP17 saldo pendiente negativo");
        Cronograma cronograma = crearCronograma(
                crearCuota(1, "1000.00", "500.00", "-1.00"),
                crearCuota(2, "500.00", "500.00", "0.00"));
        assertFalse(cronograma.esConsistente());
    }
}
