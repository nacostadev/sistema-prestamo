package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class PrestamoTest {

    // Datos base reutilizables para todos los escenarios de HU-02.
    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private TipoPrestamo tipoPersonal() {
        return tipoConMontos("500.00", "30000.00");
    }

    private TipoPrestamo tipoConMontos(String minimo, String maximo) {
        return new TipoPrestamo("TP01", "Prestamo Personal", 3, 36,
            bd("0.1500"), bd("0.4500"),
            minimo == null ? null : bd(minimo),
            maximo == null ? null : bd(maximo));
    }

    private Prestamo prestamo(String monto, int plazo, String tea) {
        Prestamo p = new Prestamo();
        p.setClienteCodigo("CL05");
        p.setTipoPrestamoCodigo("TP01");
        p.setMontoSolicitado(monto == null ? null : bd(monto));
        p.setPlazoCuotas(plazo);
        p.setTea(tea == null ? null : bd(tea));
        p.setModalidadPagoCodigo("MP01");
        p.setFechaDesembolso(LocalDate.of(2026, 4, 1));
        p.setMonedaCodigo("MO01");
        return p;
    }

    private Cliente cliente(String estado) {
        Cliente c = new Cliente();
        c.setCodigo("CL05");
        c.setEstado(estado);
        return c;
    }

    private Prestamo existente(String estado) {
        Prestamo p = new Prestamo();
        p.setEstadoNombre(estado);
        return p;
    }

    private String texto(int n) {
        char[] caracteres = new char[n];
        Arrays.fill(caracteres, 'x');
        return new String(caracteres);
    }

    private <T extends Throwable> T espera(Class<T> clase, String parteMensaje, Runnable accion) {
        try {
            accion.run();
            fail("Se esperaba excepcion " + clase.getSimpleName());
        } catch (Throwable error) {
            if (!clase.isInstance(error)) {
                throw new AssertionError("Se esperaba " + clase.getSimpleName()
                    + " pero se recibio " + error.getClass().getSimpleName(), error);
            }
            if (parteMensaje != null) {
                assertNotNull("Se esperaba mensaje en la excepcion", error.getMessage());
                assertTrue("Mensaje inesperado: " + error.getMessage(),
                    error.getMessage().contains(parteMensaje));
            }
            return clase.cast(error);
        }
        throw new AssertionError("Ruta imposible");
    }

    private void validarFaltante(String campo, java.util.function.Consumer<Prestamo> alteracion) {
        Prestamo p = prestamo("10000", 12, "0.2");
        alteracion.accept(p);
        espera(IllegalArgumentException.class, campo, p::validar);
    }

    // BLOQUE 1: HU-02 RN-06 / CA-03: Comprueba que Moroso e Incumplimiento bloqueen nuevos préstamos y otros estados no se consideren mora.

    @Test
    public void testEstaEnMora_Moroso() {
        Prestamo p = existente("Moroso");
        assertEquals(true, p.estaEnMora());
    }

    @Test
    public void testEstaEnMora_Incumplimiento() {
        Prestamo p = existente("Incumplimiento");
        assertEquals(true, p.estaEnMora());
    }

    @Test
    public void testEstaEnMora_Vigente() {
        Prestamo p = existente("Vigente");
        assertEquals(false, p.estaEnMora());
    }

    @Test
    public void testEstaEnMora_Enevaluación() {
        Prestamo p = existente("En evaluación");
        assertEquals(false, p.estaEnMora());
    }

    @Test
    public void testEstaEnMora_moroso() {
        Prestamo p = existente("moroso");
        assertEquals(false, p.estaEnMora());
    }

    @Test
    public void testEstaEnMora_nulo() {
        assertFalse(existente(null).estaEnMora());
    }

    // BLOQUE 2: HU-02 RN-02, RN-03 / CA-05: Verifica campos obligatorios, campos opcionales, valores positivos, límites de texto y normalización decimal.

    @Test
    public void testValidar_datosCorrectosYRedondeo() {
        Prestamo p = prestamo("10000.005",12,"0.20005");
        p.validar();
        assertEquals(bd("10000.01"), p.getMontoSolicitado());
        assertEquals(bd("0.2001"), p.getTea());
    }

    @Test
    public void testValidar_todosObligatoriosAusentes() {
        espera(IllegalArgumentException.class, "Complete los campos obligatorios", () -> new Prestamo().validar());
    }

    @Test
    public void testValidar_faltante_clienteNulo() {
        validarFaltante("cliente", p -> p.setClienteCodigo(null));
    }

    @Test
    public void testValidar_faltante_clienteBlanco() {
        validarFaltante("cliente", p -> p.setClienteCodigo("  "));
    }

    @Test
    public void testValidar_faltante_tipoNulo() {
        validarFaltante("tipo de préstamo", p -> p.setTipoPrestamoCodigo(null));
    }

    @Test
    public void testValidar_faltante_tipoVacio() {
        validarFaltante("tipo de préstamo", p -> p.setTipoPrestamoCodigo(""));
    }

    @Test
    public void testValidar_faltante_montoNulo() {
        validarFaltante("monto solicitado", p -> p.setMontoSolicitado(null));
    }

    @Test
    public void testValidar_faltante_plazoCero() {
        validarFaltante("plazo", p -> p.setPlazoCuotas(0));
    }

    @Test
    public void testValidar_faltante_plazoNegativo() {
        validarFaltante("plazo", p -> p.setPlazoCuotas(-1));
    }

    @Test
    public void testValidar_faltante_teaNula() {
        validarFaltante("TEA", p -> p.setTea(null));
    }

    @Test
    public void testValidar_faltante_modalidadNula() {
        validarFaltante("modalidad de pago", p -> p.setModalidadPagoCodigo(null));
    }

    @Test
    public void testValidar_faltante_modalidadBlanco() {
        validarFaltante("modalidad de pago", p -> p.setModalidadPagoCodigo(" "));
    }

    @Test
    public void testValidar_faltante_fechaNula() {
        validarFaltante("fecha de desembolso", p -> p.setFechaDesembolso(null));
    }

    @Test
    public void testValidar_faltante_monedaNula() {
        validarFaltante("moneda", p -> p.setMonedaCodigo(null));
    }

    @Test
    public void testValidar_faltante_monedaVacia() {
        validarFaltante("moneda", p -> p.setMonedaCodigo(""));
    }

    @Test
    public void testValidar_monto_cero() {
        Prestamo p = prestamo("0",12,"0.2");
        espera(IllegalArgumentException.class,"monto solicitado debe",p::validar);
    }

    @Test
    public void testValidar_monto_negativo() {
        Prestamo p = prestamo("-5",12,"0.2");
        espera(IllegalArgumentException.class,"monto solicitado debe",p::validar);
    }

    @Test
    public void testValidar_tea_cero() {
        Prestamo p = prestamo("10000",12,"0");
        espera(IllegalArgumentException.class,"TEA debe",p::validar);
    }

    @Test
    public void testValidar_tea_negativa() {
        Prestamo p = prestamo("10000",12,"-0.01");
        espera(IllegalArgumentException.class,"TEA debe",p::validar);
    }

    @Test
    public void testValidar_observaciones_255() {
        Prestamo p=prestamo("10000",12,"0.2");
        p.setObservaciones(texto(255));
        p.validar();
    }

    @Test
    public void testValidar_observaciones_256() {
        Prestamo p=prestamo("10000",12,"0.2");
        p.setObservaciones(texto(256));
        espera(IllegalArgumentException.class,"255",p::validar);
    }

    @Test
    public void testValidar_observaciones_nulo() {
        Prestamo p=prestamo("10000",12,"0.2");
        p.setObservaciones(null);
        p.validar();
    }

    @Test
    public void testValidar_destino_255() {
        Prestamo p=prestamo("10000",12,"0.2");
        p.setDescripcionDestino(texto(255));
        p.validar();
    }

    @Test
    public void testValidar_destino_256() {
        Prestamo p=prestamo("10000",12,"0.2");
        p.setDescripcionDestino(texto(256));
        espera(IllegalArgumentException.class,"255",p::validar);
    }

    @Test
    public void testValidar_destino_nulo() {
        Prestamo p=prestamo("10000",12,"0.2");
        p.setDescripcionDestino(null);
        p.validar();
    }

    @Test
    public void testValidar_camposObligatoriosConEspacios() {
        Prestamo p=prestamo("10000",12,"0.2");
        p.setClienteCodigo(" CL05 ");
        p.validar();
        assertEquals(" CL05 ",p.getClienteCodigo());
    }

    // BLOQUE 3: HU-02 RN-01, RN-06 / CA-03: Examina cliente registrado/activo y recorriendo préstamos anteriores; incluye listas y elementos nulos.

    @Test
    public void testValidarCliente_activoSinPrestamos() {
        prestamo("10000",12,"0.2").validarCliente(cliente("A"),Collections.emptyList());
    }

    @Test
    public void testValidarCliente_activoVigenteEvaluacion() {
        prestamo("10000",12,"0.2").validarCliente(cliente("A"),
        Arrays.asList(existente("Vigente"),existente("En evaluación")));
    }

    @Test
    public void testValidarCliente_noRegistrado() {
        espera(IllegalArgumentException.class,"no existe", () ->
        prestamo("10000",12,"0.2").validarCliente(null,Collections.emptyList()));
    }

    @Test
    public void testValidarCliente_inactivo() {
        espera(IllegalArgumentException.class,"inactivo", () ->
        prestamo("10000",12,"0.2").validarCliente(cliente("I"),Collections.emptyList()));
    }

    @Test
    public void testValidarCliente_morosoPrimero() {
        espera(IllegalArgumentException.class,"morosidad", () ->
        prestamo("10000",12,"0.2").validarCliente(cliente("A"),Arrays.asList(existente("Moroso"))));
    }

    @Test
    public void testValidarCliente_incumplimientoDespues() {
        espera(IllegalArgumentException.class,"incumplimiento", () ->
        prestamo("10000",12,"0.2").validarCliente(cliente("A"),Arrays.asList(existente("Vigente"),existente("Incumplimiento"))));
    }

    @Test
    public void testValidarCliente_listaNula() {
        espera(NullPointerException.class,null, () ->
        prestamo("10000",12,"0.2").validarCliente(cliente("A"),null));
    }

    @Test
    public void testValidarCliente_elementoNulo() {
        espera(NullPointerException.class,null, () ->
        prestamo("10000",12,"0.2").validarCliente(cliente("A"),Arrays.asList((Prestamo)null)));
    }

    @Test
    public void testValidarCliente_prestamoConEstadoNulo() {
        prestamo("10000",12,"0.2").validarCliente(cliente("A"),
        Arrays.asList(existente(null),existente("Vigente")));
    }

    // BLOQUE 4: HU-02 RN-04, RN-05 / CA-04, CA-06: Verifica la secuencia real: primero plazo y TEA, después monto y política crediticia.

    @Test
    public void testValidarCondiciones_correctas() {
        prestamo("10000",12,"0.2").validarCondiciones(tipoPersonal());
    }

    @Test
    public void testValidarCondiciones_tipoNulo() {
        espera(IllegalArgumentException.class,"tipo de préstamo seleccionado", () ->
        prestamo("10000",12,"0.2").validarCondiciones(null));
    }

    @Test
    public void testValidarCondiciones_plazoInvalido() {
        espera(IllegalArgumentException.class,"El plazo no cumple", () ->
        prestamo("10000",2,"0.2").validarCondiciones(tipoPersonal()));
    }

    @Test
    public void testValidarCondiciones_teaInvalida() {
        espera(IllegalArgumentException.class,"La TEA no cumple", () ->
        prestamo("10000",12,"0.1000").validarCondiciones(tipoPersonal()));
    }

    @Test
    public void testValidarCondiciones_montoInvalido() {
        espera(MontoInvalidoException.class,"monto no es válido", () ->
        prestamo("30001",12,"0.2").validarCondiciones(tipoPersonal()));
    }

    @Test
    public void testValidarCondiciones_plazoYMontoInvalidosPriorizaPlazo() {
        espera(IllegalArgumentException.class,"El plazo no cumple", () ->
        prestamo("30001",2,"0.2").validarCondiciones(tipoPersonal()));
    }

    @Test
    public void testValidarCondiciones_sinPolitica() {
        espera(IllegalArgumentException.class,"política crediticia", () ->
        prestamo("10000",12,"0.2").validarCondiciones(tipoConMontos(null,"30000")));
    }

    // BLOQUE 5: HU-02 RN-04 / CA-04: Revisa política crediticia, extremos incluidos, montos fuera de rango y validaciones ante valores ausentes.

    @Test
    public void testValidarMonto_tipoNulo() {
        espera(IllegalArgumentException.class,"tipo de préstamo seleccionado", () ->
        prestamo("10000",12,"0.2").validarMonto(null));
    }

    @Test
    public void testValidarMonto_minimoNulo() {
        espera(IllegalArgumentException.class,"política crediticia", () ->
        prestamo("10000",12,"0.2").validarMonto(tipoConMontos(null,"30000")));
    }

    @Test
    public void testValidarMonto_maximoNulo() {
        espera(IllegalArgumentException.class,"política crediticia", () ->
        prestamo("10000",12,"0.2").validarMonto(tipoConMontos("500",null)));
    }

    @Test
    public void testValidarMonto_ambosNulos() {
        espera(IllegalArgumentException.class,"política crediticia", () ->
        prestamo("10000",12,"0.2").validarMonto(tipoConMontos(null,null)));
    }

    @Test
    public void testValidarMonto_acepta_minimo() {
        prestamo("500",12,"0.2").validarMonto(tipoPersonal());
    }

    @Test
    public void testValidarMonto_acepta_maximo() {
        prestamo("30000",12,"0.2").validarMonto(tipoPersonal());
    }

    @Test
    public void testValidarMonto_acepta_interno() {
        prestamo("10000",12,"0.2").validarMonto(tipoPersonal());
    }

    @Test
    public void testValidarMonto_rechaza_inferior() {
        espera(MontoInvalidoException.class,"monto no es válido", () ->
        prestamo("499.99",12,"0.2").validarMonto(tipoPersonal()));
    }

    @Test
    public void testValidarMonto_rechaza_superior() {
        espera(MontoInvalidoException.class,"monto no es válido", () ->
        prestamo("30000.01",12,"0.2").validarMonto(tipoPersonal()));
    }

    @Test
    public void testValidarMonto_valorNulo() {
        espera(NullPointerException.class,null, () ->
        prestamo(null,12,"0.2").validarMonto(tipoPersonal()));
    }

    @Test
    public void testValidarMonto_ceroFueraDePolitica() {
        espera(MontoInvalidoException.class,"monto no es válido", () ->
        prestamo("0",12,"0.2").validarMonto(tipoPersonal()));
    }

    // BLOQUE 6: HU-02 RN-05 / CA-06: Comprueba límites mínimo/máximo de cuotas y de TEA, incluido el rechazo fuera de rango.

    @Test
    public void testValidarPlazoYTasa_tipoNulo() {
        espera(IllegalArgumentException.class,"tipo de préstamo seleccionado", () ->
        prestamo("10000",12,"0.2").validarPlazoYTasa(null));
    }

    @Test
    public void testValidarPlazoYTasa_plazo_minimo() {
        prestamo("10000",3,"0.2").validarPlazoYTasa(tipoPersonal());
    }

    @Test
    public void testValidarPlazoYTasa_plazo_maximo() {
        prestamo("10000",36,"0.2").validarPlazoYTasa(tipoPersonal());
    }

    @Test
    public void testValidarPlazoYTasa_plazo_debajoMinimo() {
        espera(IllegalArgumentException.class,"El plazo no cumple", () ->
        prestamo("10000",2,"0.2").validarPlazoYTasa(tipoPersonal()));
    }

    @Test
    public void testValidarPlazoYTasa_plazo_encimaMaximo() {
        espera(IllegalArgumentException.class,"El plazo no cumple", () ->
        prestamo("10000",37,"0.2").validarPlazoYTasa(tipoPersonal()));
    }

    @Test
    public void testValidarPlazoYTasa_tea_minima() {
        prestamo("10000",12,"0.1500").validarPlazoYTasa(tipoPersonal());
    }

    @Test
    public void testValidarPlazoYTasa_tea_maxima() {
        prestamo("10000",12,"0.4500").validarPlazoYTasa(tipoPersonal());
    }

    @Test
    public void testValidarPlazoYTasa_tea_debajoMinima() {
        espera(IllegalArgumentException.class,"La TEA no cumple", () ->
        prestamo("10000",12,"0.1499").validarPlazoYTasa(tipoPersonal()));
    }

    @Test
    public void testValidarPlazoYTasa_tea_encimaMaxima() {
        espera(IllegalArgumentException.class,"La TEA no cumple", () ->
        prestamo("10000",12,"0.4501").validarPlazoYTasa(tipoPersonal()));
    }

    @Test
    public void testValidarPlazoYTasa_teaNula() {
        espera(NullPointerException.class,null, () ->
        prestamo("10000",12,null).validarPlazoYTasa(tipoPersonal()));
    }

    // BLOQUE 7: HU-02 RN-04: Recorre las ramas del predicado de monto (mínimo, máximo, interior, fuera del rango, política incompleta).

    @Test
    public void testMontoCumple_debajo() {
        assertEquals(false,prestamo("499.99",12,"0.2").montoCumple(tipoPersonal()));
    }

    @Test
    public void testMontoCumple_igualMinimo() {
        assertEquals(true,prestamo("500",12,"0.2").montoCumple(tipoPersonal()));
    }

    @Test
    public void testMontoCumple_interno() {
        assertEquals(true,prestamo("10000",12,"0.2").montoCumple(tipoPersonal()));
    }

    @Test
    public void testMontoCumple_igualMaximo() {
        assertEquals(true,prestamo("30000",12,"0.2").montoCumple(tipoPersonal()));
    }

    @Test
    public void testMontoCumple_encima() {
        assertEquals(false,prestamo("30000.01",12,"0.2").montoCumple(tipoPersonal()));
    }

    @Test
    public void testMontoCumple_minimoNulo() {
        assertFalse(prestamo("10000",12,"0.2").montoCumple(tipoConMontos(null,"30000")));
    }

    @Test
    public void testMontoCumple_maximoNulo() {
        assertFalse(prestamo("10000",12,"0.2").montoCumple(tipoConMontos("500",null)));
    }

    @Test
    public void testMontoCumple_tipoNulo() {
        espera(NullPointerException.class,null, () ->
        prestamo("10000",12,"0.2").montoCumple(null));
    }

    @Test
    public void testMontoCumple_montoNulo() {
        espera(NullPointerException.class,null, () ->
        prestamo(null,12,"0.2").montoCumple(tipoPersonal()));
    }

    // BLOQUE 8: HU-02 RN-05: Recorre las condiciones del plazo frente a los límites definidos en el tipo de préstamo.

    @Test
    public void testPlazoCumple_debajo() {
        assertEquals(false, prestamo("10000",2,"0.2").plazoCumple(tipoPersonal()));
    }

    @Test
    public void testPlazoCumple_minimo() {
        assertEquals(true, prestamo("10000",3,"0.2").plazoCumple(tipoPersonal()));
    }

    @Test
    public void testPlazoCumple_interno() {
        assertEquals(true, prestamo("10000",12,"0.2").plazoCumple(tipoPersonal()));
    }

    @Test
    public void testPlazoCumple_maximo() {
        assertEquals(true, prestamo("10000",36,"0.2").plazoCumple(tipoPersonal()));
    }

    @Test
    public void testPlazoCumple_encima() {
        assertEquals(false, prestamo("10000",37,"0.2").plazoCumple(tipoPersonal()));
    }

    @Test
    public void testPlazoCumple_tipoNulo() {
        espera(NullPointerException.class,null, () ->
        prestamo("10000",12,"0.2").plazoCumple(null));
    }

    // BLOQUE 9: HU-02 RN-05: Recorre las condiciones de la tasa efectiva anual frente a los límites del tipo de préstamo.

    @Test
    public void testTeaCumple_debajo() {
        assertEquals(false,prestamo("10000",12,"0.1499").teaCumple(tipoPersonal()));
    }

    @Test
    public void testTeaCumple_minima() {
        assertEquals(true,prestamo("10000",12,"0.1500").teaCumple(tipoPersonal()));
    }

    @Test
    public void testTeaCumple_interna() {
        assertEquals(true,prestamo("10000",12,"0.2000").teaCumple(tipoPersonal()));
    }

    @Test
    public void testTeaCumple_maxima() {
        assertEquals(true,prestamo("10000",12,"0.4500").teaCumple(tipoPersonal()));
    }

    @Test
    public void testTeaCumple_encima() {
        assertEquals(false,prestamo("10000",12,"0.4501").teaCumple(tipoPersonal()));
    }

    @Test
    public void testTeaCumple_tipoNulo() {
        espera(NullPointerException.class,null, () ->
        prestamo("10000",12,"0.2").teaCumple(null));
    }

    @Test
    public void testTeaCumple_teaNula() {
        espera(NullPointerException.class,null, () ->
        prestamo("10000",12,null).teaCumple(tipoPersonal()));
    }

    // BLOQUE 10: HU-02 RN-02: Casos límite de redondeo. Con el modelo actual estas dos pruebas pueden FALLAR; sirven para documentar defectos, no están omitidas.

    @Test
    public void testDefectoMontoPequenoRedondeaACero() {
        Prestamo p=prestamo("0.001",12,"0.2");
        p.validar();
        assertTrue("El monto normalizado debe seguir siendo > 0",p.getMontoSolicitado().signum()>0);
    }

    @Test
    public void testDefectoTeaPequenaRedondeaACero() {
        Prestamo p=prestamo("10000",12,"0.00001");
        p.validar();
        assertTrue("La TEA normalizada debe seguir siendo > 0",p.getTea().signum()>0);
    }

}
