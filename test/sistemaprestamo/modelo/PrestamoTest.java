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
}
