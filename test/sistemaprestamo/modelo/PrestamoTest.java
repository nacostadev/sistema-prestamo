package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

/** Pruebas de las reglas de Prestamo (HU-02). */
public class PrestamoTest {

    /** Condiciones de un tipo de préstamo: plazo 3-36, TEA 15-45 %, monto 500-30,000. */
    private TipoPrestamo crearTipoPersonal() {
        return new TipoPrestamo("TP01", "Préstamo Personal", 3, 36,
                new BigDecimal("0.1500"), new BigDecimal("0.4500"),
                new BigDecimal("500.00"), new BigDecimal("30000.00"));
    }

    private Prestamo crearPrestamo(String monto, int plazo, String tea) {
        Prestamo prestamo = new Prestamo();
        prestamo.setClienteCodigo("CL05");
        prestamo.setTipoPrestamoCodigo("TP01");
        prestamo.setMontoSolicitado(new BigDecimal(monto));
        prestamo.setPlazoCuotas(plazo);
        prestamo.setTea(new BigDecimal(tea));
        prestamo.setModalidadPagoCodigo("MP01");
        prestamo.setFechaDesembolso(LocalDate.of(2026, 4, 1));
        prestamo.setMonedaCodigo("MO01");
        return prestamo;
    }

    @Test
    public void testValidarPrestamoCompleto() {
        System.out.println("validar - prestamo completo");
        Prestamo prestamo = crearPrestamo("10000", 12, "0.2");
        prestamo.validar();
        assertEquals(new BigDecimal("10000.00"), prestamo.getMontoSolicitado());
        assertEquals(new BigDecimal("0.2000"), prestamo.getTea());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarSinCamposObligatorios() {
        System.out.println("validar - sin campos obligatorios");
        new Prestamo().validar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarMontoNegativo() {
        System.out.println("validar - monto negativo");
        crearPrestamo("-5", 12, "0.2").validar();
    }

    @Test
    public void testValidarCondicionesCorrectas() {
        System.out.println("validarCondiciones - correctas");
        Prestamo prestamo = crearPrestamo("10000", 12, "0.2");
        prestamo.validar();
        prestamo.validarCondiciones(crearTipoPersonal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarCondicionesMontoFueraDePolitica() {
        System.out.println("validarCondiciones - monto fuera de politica");
        Prestamo prestamo = crearPrestamo("50000", 12, "0.2");
        prestamo.validar();
        prestamo.validarCondiciones(crearTipoPersonal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarCondicionesPlazoFueraDelTipo() {
        System.out.println("validarCondiciones - plazo fuera del tipo");
        Prestamo prestamo = crearPrestamo("10000", 60, "0.2");
        prestamo.validar();
        prestamo.validarCondiciones(crearTipoPersonal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarCondicionesTeaFueraDelTipo() {
        System.out.println("validarCondiciones - tea fuera del tipo");
        Prestamo prestamo = crearPrestamo("10000", 12, "0.9");
        prestamo.validar();
        prestamo.validarCondiciones(crearTipoPersonal());
    }

    @Test
    public void testEstaEnMora() {
        System.out.println("estaEnMora");
        Prestamo prestamo = new Prestamo();
        prestamo.setEstadoNombre("Vigente");
        assertFalse(prestamo.estaEnMora());
        prestamo.setEstadoNombre("Moroso");
        assertTrue(prestamo.estaEnMora());
        prestamo.setEstadoNombre("Incumplimiento");
        assertTrue(prestamo.estaEnMora());
    }

    // ------------------------------------------------------------------
    // HU-02 RN-01 y RN-06: cliente activo y sin morosidad (CA-03)
    // ------------------------------------------------------------------

    private Cliente crearCliente(String estado) {
        Cliente cliente = new Cliente();
        cliente.setCodigo("CL05");
        cliente.setEstado(estado);
        return cliente;
    }

    private Prestamo crearPrestamoConEstado(String estado) {
        Prestamo prestamo = new Prestamo();
        prestamo.setEstadoNombre(estado);
        return prestamo;
    }

    @Test
    public void testValidarClienteActivoSinPrestamos() {
        System.out.println("validarCliente - activo sin prestamos");
        crearPrestamo("10000", 12, "0.2").validarCliente(crearCliente("A"), java.util.Collections.<Prestamo>emptyList());
    }

    @Test
    public void testValidarClienteConPrestamoVigenteOEnEvaluacionPuedeRegistrar() {
        System.out.println("validarCliente - vigente y en evaluacion no bloquean");
        crearPrestamo("10000", 12, "0.2").validarCliente(crearCliente("A"),
                java.util.List.of(crearPrestamoConEstado("Vigente"), crearPrestamoConEstado("En evaluación")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarClienteNoRegistrado() {
        System.out.println("validarCliente - no registrado");
        crearPrestamo("10000", 12, "0.2").validarCliente(null, java.util.Collections.<Prestamo>emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarClienteInactivo() {
        System.out.println("validarCliente - inactivo");
        crearPrestamo("10000", 12, "0.2").validarCliente(crearCliente("I"), java.util.Collections.<Prestamo>emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarClienteConPrestamoMoroso() {
        System.out.println("validarCliente - moroso");
        crearPrestamo("10000", 12, "0.2").validarCliente(crearCliente("A"),
                java.util.List.of(crearPrestamoConEstado("Moroso")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarClienteConPrestamoEnIncumplimiento() {
        System.out.println("validarCliente - incumplimiento");
        crearPrestamo("10000", 12, "0.2").validarCliente(crearCliente("A"),
                java.util.List.of(crearPrestamoConEstado("Vigente"), crearPrestamoConEstado("Incumplimiento")));
    }

    // ------------------------------------------------------------------
    // HU-02 CA-04 (monto = error) y CA-06 (plazo y tasa = advertencia)
    // ------------------------------------------------------------------

    @Test(expected = MontoInvalidoException.class)
    public void testValidarMontoFueraDeLosLimites() {
        System.out.println("validarMonto - fuera de limites");
        crearPrestamo("100", 12, "0.2").validarMonto(crearTipoPersonal());
    }

    @Test
    public void testValidarMontoEnLosLimites() {
        System.out.println("validarMonto - en los limites");
        crearPrestamo("500", 12, "0.2").validarMonto(crearTipoPersonal());
        crearPrestamo("30000", 12, "0.2").validarMonto(crearTipoPersonal());
    }

    @Test
    public void testValidarPlazoYTasaNoLanzaMontoInvalido() {
        System.out.println("validarPlazoYTasa - plazo fuera del tipo");
        try {
            crearPrestamo("10000", 2, "0.2").validarPlazoYTasa(crearTipoPersonal());
            fail("Debió lanzar IllegalArgumentException");
        } catch (MontoInvalidoException excepcion) {
            fail("El plazo no debe tratarse como monto inválido");
        } catch (IllegalArgumentException esperada) {
            assertTrue(esperada.getMessage().startsWith("El plazo no cumple"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarPlazoYTasaConTeaFuera() {
        System.out.println("validarPlazoYTasa - tea fuera del tipo");
        crearPrestamo("10000", 12, "0.1000").validarPlazoYTasa(crearTipoPersonal());
    }

    @Test
    public void testValidarCondicionesAplicaMontoPlazoYTasa() {
        System.out.println("validarCondiciones - todo correcto");
        crearPrestamo("10000", 12, "0.2").validarCondiciones(crearTipoPersonal());
    }
}
