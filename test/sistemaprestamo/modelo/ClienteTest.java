package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;


public class ClienteTest {

    private static final String OK = "OK";

    public ClienteTest() {
    }


    private Cliente crearClienteValido() {
        Cliente cliente = new Cliente();
        cliente.setCodigo("CL05");
        cliente.setTipoDocumentoCodigo("TD01");
        cliente.setNumeroDocumento("45871236");
        cliente.setApellidos("Ramírez Torres");
        cliente.setNombres("Juan Carlos");
        cliente.setFechaNacimiento(LocalDate.of(1985, 6, 15));
        cliente.setEstadoCivilCodigo("EC02");
        cliente.setDireccion("Av. Perú 123, Trujillo");
        cliente.setTelefono("987654321");
        cliente.setCorreo("jramirez@example.com");
        cliente.setCondicionLaboral("Dependiente");
        cliente.setNivelIngresos(new BigDecimal("2500.00"));
        cliente.setEstado(Cliente.ESTADO_ACTIVO);
        return cliente;
    }

    private String repetir(char caracter, int veces) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            texto.append(caracter);
        }
        return texto.toString();
    }

    private String ejecutar(Runnable accion) {
        try {
            accion.run();
            return OK;
        } catch (IllegalArgumentException excepcion) {
            return excepcion.getMessage();
        }
    }

    private String resumir(Cliente cliente) {
        return cliente.getNumeroDocumento() + "|" + cliente.getNombres() + "|"
                + cliente.getApellidos() + "|" + cliente.getDireccion() + "|"
                + cliente.getTelefono() + "|" + cliente.getCorreo() + "|"
                + cliente.getCondicionLaboral() + "|"
                + cliente.getNombreConyuge() + "|" + cliente.getReferenciasPersonales() + "|"
                + cliente.getObservaciones();
    }


    /**
     * Test of estaActivo method, of class Cliente.
     */
    @Test
    public void testEstaActivo_caso1_estadoActivo() {
        System.out.println("estaActivo - caso 1");
        Cliente cliente = new Cliente();
        cliente.setEstado(Cliente.ESTADO_ACTIVO);
        boolean expResult = true;
        boolean result = cliente.estaActivo();
        assertEquals(expResult, result);
    }

    /**
     * Test of estaActivo method, of class Cliente.
     */
    @Test
    public void testEstaActivo_caso2_estadoInactivo() {
        System.out.println("estaActivo - caso 2");
        Cliente cliente = new Cliente();
        cliente.setEstado(Cliente.ESTADO_INACTIVO);
        boolean expResult = false;
        boolean result = cliente.estaActivo();
        assertEquals(expResult, result);
    }

    /**
     * Test of estaActivo method, of class Cliente.
     */
    @Test
    public void testEstaActivo_caso3_estadoNulo() {
        System.out.println("estaActivo - caso 3");
        Cliente cliente = new Cliente();
        boolean expResult = false;
        boolean result = cliente.estaActivo();
        assertEquals(expResult, result);
    }

    /**
     * Test of validarRegistrado method, of class Cliente.
     */
    @Test
    public void testValidarRegistrado_caso1_clienteInexistente() {
        System.out.println("validarRegistrado - caso 1");
        String expResult = "El cliente no está registrado en el sistema.";
        String result = ejecutar(() -> Cliente.validarRegistrado(null));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarRegistrado method, of class Cliente.
     */
    @Test
    public void testValidarRegistrado_caso2_clienteRegistrado() {
        System.out.println("validarRegistrado - caso 2");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> Cliente.validarRegistrado(cliente));
        assertEquals(expResult, result);
    }

    private static final String MENSAJE_NO_DESACTIVAR =
            "No se puede desactivar al cliente porque tiene préstamos activos o solicitudes de crédito en evaluación.";

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso1_sinPrestamos() {
        System.out.println("validarPuedeDesactivarse - caso 1");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(Collections.<String>emptyList()));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso2_prestamoVigente() {
        System.out.println("validarPuedeDesactivarse - caso 2");
        Cliente cliente = crearClienteValido();
        String expResult = MENSAJE_NO_DESACTIVAR;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(List.of("EP01")));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso3_prestamoMoroso() {
        System.out.println("validarPuedeDesactivarse - caso 3");
        Cliente cliente = crearClienteValido();
        String expResult = MENSAJE_NO_DESACTIVAR;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(List.of("EP02")));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso4_prestamoIncumplimiento() {
        System.out.println("validarPuedeDesactivarse - caso 4");
        Cliente cliente = crearClienteValido();
        String expResult = MENSAJE_NO_DESACTIVAR;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(List.of("EP03")));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso5_solicitudEnEvaluacion() {
        System.out.println("validarPuedeDesactivarse - caso 5");
        Cliente cliente = crearClienteValido();
        String expResult = MENSAJE_NO_DESACTIVAR;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(List.of("EP04")));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso6_variosPrestamos() {
        System.out.println("validarPuedeDesactivarse - caso 6");
        Cliente cliente = crearClienteValido();
        String expResult = MENSAJE_NO_DESACTIVAR;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(List.of("EP01", "EP02", "EP04")));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso7_estadoQueNoImpide() {
        System.out.println("validarPuedeDesactivarse - caso 7");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(List.of("EP99")));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumentoUnico method, of class Cliente.
     */
    @Test
    public void testValidarDocumentoUnico_caso1_listaVacia() {
        System.out.println("validarDocumentoUnico - caso 1");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarDocumentoUnico(Collections.<Cliente>emptyList()));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumentoUnico method, of class Cliente.
     */
    @Test
    public void testValidarDocumentoUnico_caso2_mismoCliente() {
        System.out.println("validarDocumentoUnico - caso 2");
        Cliente cliente = crearClienteValido();
        Cliente existente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarDocumentoUnico(Collections.singletonList(existente)));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumentoUnico method, of class Cliente.
     */
    @Test
    public void testValidarDocumentoUnico_caso3_otroClienteConElMismoDocumento() {
        System.out.println("validarDocumentoUnico - caso 3");
        Cliente cliente = crearClienteValido();
        Cliente existente = crearClienteValido();
        existente.setCodigo("CL02");
        String expResult = "Ya existe un cliente registrado con ese número de documento.";
        String result = ejecutar(() -> cliente.validarDocumentoUnico(Collections.singletonList(existente)));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumentoUnico method, of class Cliente.
     */
    @Test
    public void testValidarDocumentoUnico_caso4_clienteNuevoSinCodigo() {
        System.out.println("validarDocumentoUnico - caso 4");
        Cliente cliente = crearClienteValido();
        cliente.setCodigo(null);
        Cliente existente = crearClienteValido();
        String expResult = "Ya existe un cliente registrado con ese número de documento.";
        String result = ejecutar(() -> cliente.validarDocumentoUnico(Collections.singletonList(existente)));
        assertEquals(expResult, result);
    }


    /**
     * Test of validarMayorDeEdad method, of class Cliente.
     */
    @Test
    public void testValidarMayorDeEdad_caso1_tiene17Anios() {
        System.out.println("validarMayorDeEdad - caso 1");
        Cliente cliente = crearClienteValido();
        cliente.setFechaNacimiento(LocalDate.now().minusYears(18).plusDays(1));
        String expResult = "El cliente debe ser mayor de edad (18 años o más).";
        String result = ejecutar(cliente::validarMayorDeEdad);
        assertEquals(expResult, result);
    }

    /**
     * Test of validarMayorDeEdad method, of class Cliente.
     */
    @Test
    public void testValidarMayorDeEdad_caso2_cumple18Hoy() {
        System.out.println("validarMayorDeEdad - caso 2");
        Cliente cliente = crearClienteValido();
        cliente.setFechaNacimiento(LocalDate.now().minusYears(18));
        String expResult = OK;
        String result = ejecutar(cliente::validarMayorDeEdad);
        assertEquals(expResult, result);
    }

    /**
     * Test of validarMayorDeEdad method, of class Cliente.
     */
    @Test
    public void testValidarMayorDeEdad_caso3_adulto() {
        System.out.println("validarMayorDeEdad - caso 3");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(cliente::validarMayorDeEdad);
        assertEquals(expResult, result);
    }


    /**
     * Test of validarLongitud method, of class Cliente.
     */
    @Test
    public void testValidarLongitud_caso1_valorNulo() {
        System.out.println("validarLongitud - caso 1");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarLongitud(null, "observaciones", 5));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarLongitud method, of class Cliente.
     */
    @Test
    public void testValidarLongitud_caso2_valorDentroDelMaximo() {
        System.out.println("validarLongitud - caso 2");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarLongitud("abc", "observaciones", 5));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarLongitud method, of class Cliente.
     */
    @Test
    public void testValidarLongitud_caso3_valorSuperaElMaximo() {
        System.out.println("validarLongitud - caso 3");
        Cliente cliente = crearClienteValido();
        String expResult = "El campo 'observaciones' no puede superar los 5 caracteres.";
        String result = ejecutar(() -> cliente.validarLongitud("abcdef", "observaciones", 5));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarLongitud method, of class Cliente.
     */
    @Test
    public void testValidarLongitud_caso4_valorIgualAlMaximo() {
        System.out.println("validarLongitud - caso 4");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarLongitud("abcde", "observaciones", 5));
        assertEquals(expResult, result);
    }


    /**
     * Test of validarDocumento method, of class Cliente.
     */
    @Test
    public void testValidarDocumento_caso1_dniValido() {
        System.out.println("validarDocumento - caso 1");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(cliente::validarDocumento);
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumento method, of class Cliente.
     */
    @Test
    public void testValidarDocumento_caso2_dniConPocosDigitos() {
        System.out.println("validarDocumento - caso 2");
        Cliente cliente = crearClienteValido();
        cliente.setNumeroDocumento("1234");
        String expResult = "El DNI debe contener exactamente 8 dígitos.";
        String result = ejecutar(cliente::validarDocumento);
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumento method, of class Cliente.
     */
    @Test
    public void testValidarDocumento_caso3_pasaporteValido() {
        System.out.println("validarDocumento - caso 3");
        Cliente cliente = crearClienteValido();
        cliente.setTipoDocumentoCodigo("TD02");
        cliente.setNumeroDocumento("AB1234567");
        String expResult = OK;
        String result = ejecutar(cliente::validarDocumento);
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumento method, of class Cliente.
     */
    @Test
    public void testValidarDocumento_caso4_dniConLetras() {
        System.out.println("validarDocumento - caso 4");
        Cliente cliente = crearClienteValido();
        cliente.setNumeroDocumento("4587123A");
        String expResult = "El DNI debe contener exactamente 8 dígitos.";
        String result = ejecutar(cliente::validarDocumento);
        assertEquals(expResult, result);
    }

    /**
     * Test of validarDocumento method, of class Cliente.
     */
    @Test
    public void testValidarDocumento_caso5_carnetExtranjeriaMuyLargo() {
        System.out.println("validarDocumento - caso 5");
        Cliente cliente = crearClienteValido();
        cliente.setTipoDocumentoCodigo("TD03");
        cliente.setNumeroDocumento(repetir('7', 13));
        String expResult = "El campo 'número de documento' no puede superar los 12 caracteres.";
        String result = ejecutar(cliente::validarDocumento);
        assertEquals(expResult, result);
    }


    /**
     * Test of obtenerCamposObligatoriosFaltantes method, of class Cliente.
     */
    @Test
    public void testObtenerCamposObligatoriosFaltantes_caso1_clienteCompleto() {
        System.out.println("obtenerCamposObligatoriosFaltantes - caso 1");
        Cliente cliente = crearClienteValido();
        String expResult = "";
        String result = cliente.obtenerCamposObligatoriosFaltantes();
        assertEquals(expResult, result);
    }

    /**
     * Test of obtenerCamposObligatoriosFaltantes method, of class Cliente.
     */
    @Test
    public void testObtenerCamposObligatoriosFaltantes_caso2_sinFechaNacimiento() {
        System.out.println("obtenerCamposObligatoriosFaltantes - caso 2");
        Cliente cliente = crearClienteValido();
        cliente.setFechaNacimiento(null);
        String expResult = "fecha de nacimiento";
        String result = cliente.obtenerCamposObligatoriosFaltantes();
        assertEquals(expResult, result);
    }

    /**
     * Test of obtenerCamposObligatoriosFaltantes method, of class Cliente.
     */
    @Test
    public void testObtenerCamposObligatoriosFaltantes_caso3_sinNivelIngresos() {
        System.out.println("obtenerCamposObligatoriosFaltantes - caso 3");
        Cliente cliente = crearClienteValido();
        cliente.setNivelIngresos(null);
        String expResult = "nivel de ingresos";
        String result = cliente.obtenerCamposObligatoriosFaltantes();
        assertEquals(expResult, result);
    }

    /**
     * Test of obtenerCamposObligatoriosFaltantes method, of class Cliente.
     */
    @Test
    public void testObtenerCamposObligatoriosFaltantes_caso4_variosCamposFaltantes() {
        System.out.println("obtenerCamposObligatoriosFaltantes - caso 4");
        Cliente cliente = crearClienteValido();
        cliente.setTipoDocumentoCodigo(null);
        cliente.setNumeroDocumento("   ");
        cliente.setFechaNacimiento(null);
        cliente.setNivelIngresos(null);
        String expResult = "tipo de documento, número de documento, fecha de nacimiento, nivel de ingresos";
        String result = cliente.obtenerCamposObligatoriosFaltantes();
        assertEquals(expResult, result);
    }


    /** Cliente con espacios sobrantes en los datos de texto, sin campos opcionales. */
    private Cliente crearClienteConEspacios() {
        Cliente cliente = crearClienteValido();
        cliente.setNumeroDocumento("  45871236 ");
        cliente.setNombres("  Juan Carlos ");
        cliente.setApellidos(" Ramírez Torres  ");
        cliente.setDireccion("  Av. Perú 123 ");
        cliente.setTelefono(" 987654321 ");
        cliente.setCorreo(" jramirez@example.com  ");
        cliente.setCondicionLaboral("  Dependiente ");
        return cliente;
    }

    /**
     * Test of quitarEspacios method, of class Cliente.
     */
    @Test
    public void testQuitarEspacios_caso1_sinCamposOpcionales() {
        System.out.println("quitarEspacios - caso 1");
        Cliente cliente = crearClienteConEspacios();
        cliente.quitarEspacios();
        String expResult = "45871236|Juan Carlos|Ramírez Torres|Av. Perú 123|987654321|jramirez@example.com|Dependiente|null|null|null";
        String result = resumir(cliente);
        assertEquals(expResult, result);
    }

    /**
     * Test of quitarEspacios method, of class Cliente.
     */
    @Test
    public void testQuitarEspacios_caso2_soloConyuge() {
        System.out.println("quitarEspacios - caso 2");
        Cliente cliente = crearClienteConEspacios();
        cliente.setNombreConyuge("  María Pérez  ");
        cliente.quitarEspacios();
        String expResult = "45871236|Juan Carlos|Ramírez Torres|Av. Perú 123|987654321|jramirez@example.com|Dependiente|María Pérez|null|null";
        String result = resumir(cliente);
        assertEquals(expResult, result);
    }

    /**
     * Test of quitarEspacios method, of class Cliente.
     */
    @Test
    public void testQuitarEspacios_caso3_soloReferenciasPersonales() {
        System.out.println("quitarEspacios - caso 3");
        Cliente cliente = crearClienteConEspacios();
        cliente.setReferenciasPersonales("  Vecino  ");
        cliente.quitarEspacios();
        String expResult = "45871236|Juan Carlos|Ramírez Torres|Av. Perú 123|987654321|jramirez@example.com|Dependiente|null|Vecino|null";
        String result = resumir(cliente);
        assertEquals(expResult, result);
    }

    /**
     * Test of quitarEspacios method, of class Cliente.
     */
    @Test
    public void testQuitarEspacios_caso4_soloObservaciones() {
        System.out.println("quitarEspacios - caso 4");
        Cliente cliente = crearClienteConEspacios();
        cliente.setObservaciones("  Buen pagador  ");
        cliente.quitarEspacios();
        String expResult = "45871236|Juan Carlos|Ramírez Torres|Av. Perú 123|987654321|jramirez@example.com|Dependiente|null|null|Buen pagador";
        String result = resumir(cliente);
        assertEquals(expResult, result);
    }


    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso1_clienteValido() {
        System.out.println("validar - caso 1");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso2_sinCamposObligatorios() {
        System.out.println("validar - caso 2");
        Cliente cliente = new Cliente();
        String expResult = "Complete los campos obligatorios: tipo de documento, número de documento, nombres, apellidos, fecha de nacimiento, estado civil, dirección, teléfono, correo electrónico, condición laboral, nivel de ingresos.";
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso3_fechaNacimientoFutura() {
        System.out.println("validar - caso 3");
        Cliente cliente = crearClienteValido();
        cliente.setFechaNacimiento(LocalDate.now().plusDays(1));
        String expResult = "La fecha de nacimiento no puede ser futura.";
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso4_telefonoInvalido() {
        System.out.println("validar - caso 4");
        Cliente cliente = crearClienteValido();
        cliente.setTelefono("98765");
        String expResult = "El teléfono debe contener exactamente 9 dígitos.";
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso5_correoMuyLargo() {
        System.out.println("validar - caso 5");
        Cliente cliente = crearClienteValido();
        cliente.setCorreo(repetir('a', 95) + "@x.com"); // 101 caracteres
        String expResult = "El correo electrónico no tiene un formato válido.";
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso6_correoSinFormato() {
        System.out.println("validar - caso 6");
        Cliente cliente = crearClienteValido();
        cliente.setCorreo("jramirez.example.com");
        String expResult = "El correo electrónico no tiene un formato válido.";
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso7_nivelIngresosNegativo() {
        System.out.println("validar - caso 7");
        Cliente cliente = crearClienteValido();
        cliente.setNivelIngresos(new BigDecimal("-1"));
        String expResult = "El nivel de ingresos no puede ser negativo.";
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso8_menorDeEdad() {
        System.out.println("validar - caso 8");
        Cliente cliente = crearClienteValido();
        cliente.setFechaNacimiento(LocalDate.now().minusYears(18).plusDays(1));
        String expResult = "El cliente debe ser mayor de edad (18 años o más).";
        String result = ejecutar(cliente::validar);
        assertEquals(expResult, result);
    }

    /**
     * Test of validar method, of class Cliente.
     */
    @Test
    public void testValidar_caso9_recortaLosEspacios() {
        System.out.println("validar - caso 9");
        Cliente cliente = crearClienteConEspacios();
        cliente.validar();
        String expResult = "45871236|Juan Carlos|Ramírez Torres|Av. Perú 123|987654321|jramirez@example.com|Dependiente|null|null|null";
        String result = resumir(cliente);
        assertEquals(expResult, result);
    }
}
