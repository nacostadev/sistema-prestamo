package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias de la clase Cliente (HU-01: Gestionar Cliente).
 *
 * <p>Proceso seguido (Sesión 7 - Pruebas Unitarias):
 * <ol>
 *   <li><b>Requisito funcional:</b> reglas de negocio RN-01 a RN-06 y criterios
 *       CA-02 a CA-06 de la HU-01 (ver cada método más abajo).</li>
 *   <li><b>Diseño de clases:</b> los métodos de la clase Cliente que se prueban son
 *       <pre>
 *       estaActivo(): boolean
 *       validar(): void
 *       validarDocumentoUnico(List&lt;Cliente&gt;): void
 *       validarRegistrado(Cliente): void              (estático)
 *       validarPuedeDesactivarse(int): void
 *       obtenerCamposObligatoriosFaltantes(): String
 *       quitarEspacios(): void
 *       validarMayorDeEdad(): void
 *       validarDocumento(): void
 *       validarLongitud(String, String, int): void
 *       </pre></li>
 *   <li><b>Implementación:</b> clase Cliente (paquete modelo).</li>
 *   <li><b>Pruebas unitarias:</b> técnica de caja blanca "Cobertura de caminos".
 *       Por cada método se identifican sus decisiones, se calcula la complejidad
 *       ciclomática (CC = decisiones + 1; una condición compuesta con &amp;&amp; o ||
 *       cuenta una decisión por cada operando) y se diseña al menos un caso por
 *       camino independiente.</li>
 * </ol>
 *
 * <p>Convención: cada prueba se llama {@code test<Método>_caso<N>_<descripción>}
 * y el caso N corresponde a la fila CP0N de la tabla de su método.
 * Los métodos de Cliente que no están aquí (getters y setters) no tienen lógica.
 */
public class ClienteTest {

    private static final String OK = "OK";

    public ClienteTest() {
    }

    // ======================================================================
    // Datos de apoyo
    // ======================================================================

    /** Cliente con todos los datos obligatorios correctos (mayor de edad, DNI de 8 dígitos). */
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

    /**
     * Ejecuta la acción y devuelve "OK" si termina sin error o el mensaje de la
     * IllegalArgumentException que lanzó. Así cada prueba compara un resultado
     * esperado (expResult) con el obtenido (result), igual que las demás.
     */
    private String ejecutar(Runnable accion) {
        try {
            accion.run();
            return OK;
        } catch (IllegalArgumentException excepcion) {
            return excepcion.getMessage();
        }
    }

    /** Resumen de los campos que modifica quitarEspacios(), separados por "|". */
    private String resumir(Cliente cliente) {
        return cliente.getNumeroDocumento() + "|" + cliente.getNombres() + "|"
                + cliente.getApellidos() + "|" + cliente.getDireccion() + "|"
                + cliente.getTelefono() + "|" + cliente.getCorreo() + "|"
                + cliente.getCondicionLaboral() + "|"
                + cliente.getNombreConyuge() + "|" + cliente.getReferenciasPersonales() + "|"
                + cliente.getObservaciones();
    }

    // ======================================================================
    // MÉTODO: estaActivo()                                     CC = 1
    // Requisito: HU-01 (estado A/I del cliente; desactivar no elimina).
    // Flujo: return ESTADO_ACTIVO.equals(estado)   (sin decisiones)
    //
    //   CP01  único camino      estado = "A"                      -> true
    //   CP02  (dato adicional)  estado = "I"                      -> false
    //   CP03  (dato adicional)  estado = null (cliente sin cargar) -> false
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: validarRegistrado(Cliente)  [estático]           CC = 2
    // Requisito: HU-01 RN-05 (solo se modifica un cliente registrado).
    // Flujo: if (clienteRegistrado == null) -> lanza error
    //
    //   CP01  null = verdadero   cliente inexistente   -> IllegalArgumentException
    //   CP02  null = falso       cliente registrado    -> no lanza error
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: validarPuedeDesactivarse(int cantidadPrestamosVigentes)   CC = 2
    // Requisito: HU-01 RN-06 / CA-06 (no se desactiva con préstamos vigentes).
    // Flujo: if (cantidadPrestamosVigentes > 0) -> lanza error
    //
    //   CP01  condición falsa   0 préstamos vigentes   -> no lanza error
    //   CP02  condición verdadera  1 préstamo vigente  -> IllegalArgumentException
    //   CP03  (valor frontera)  3 préstamos vigentes   -> IllegalArgumentException
    // ======================================================================

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso1_sinPrestamosVigentes() {
        System.out.println("validarPuedeDesactivarse - caso 1");
        Cliente cliente = crearClienteValido();
        String expResult = OK;
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(0));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso2_conUnPrestamoVigente() {
        System.out.println("validarPuedeDesactivarse - caso 2");
        Cliente cliente = crearClienteValido();
        String expResult = "No se puede desactivar al cliente porque tiene préstamos vigentes.";
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(1));
        assertEquals(expResult, result);
    }

    /**
     * Test of validarPuedeDesactivarse method, of class Cliente.
     */
    @Test
    public void testValidarPuedeDesactivarse_caso3_conVariosPrestamosVigentes() {
        System.out.println("validarPuedeDesactivarse - caso 3");
        Cliente cliente = crearClienteValido();
        String expResult = "No se puede desactivar al cliente porque tiene préstamos vigentes.";
        String result = ejecutar(() -> cliente.validarPuedeDesactivarse(3));
        assertEquals(expResult, result);
    }

    // ======================================================================
    // MÉTODO: validarDocumentoUnico(List<Cliente>)             CC = 3
    // Requisito: HU-01 RN-02 / CA-03 (documento de identidad no repetido).
    // Flujo: for (cada existente)            decisión 1: ¿hay otro elemento?
    //            if (código distinto)         decisión 2 -> lanza error
    //
    //   CP01  el for no entra       lista vacía                         -> no lanza error
    //   CP02  entra, if falso       el único cliente es el mismo (CL05) -> no lanza error
    //                               (caso de actualizar sus propios datos)
    //   CP03  entra, if verdadero   existe otro cliente (CL02)          -> IllegalArgumentException
    //   CP04  (dato adicional)      cliente nuevo (sin código) y existe uno
    //                               con ese documento                   -> IllegalArgumentException
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: validarMayorDeEdad()                             CC = 2
    // Requisito: HU-01 RN-01 (el cliente debe ser mayor de edad: 18 años).
    // Flujo: if (años cumplidos < 18) -> lanza error
    //
    //   CP01  condición verdadera   cumple 18 mañana (17 años)  -> IllegalArgumentException
    //   CP02  condición falsa       cumple 18 hoy (18 años)     -> no lanza error
    //   CP03  (dato adicional)      40 años                     -> no lanza error
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: validarLongitud(String valor, String campo, int maximo)   CC = 3
    // Requisito: HU-01 CA-02 (datos con formato válido; largos de las columnas).
    // Flujo: if (valor != null  &&  valor.length() > maximo) -> lanza error
    //                decisión 1        decisión 2
    //
    //   CP01  decisión 1 falsa                valor = null             -> no lanza error
    //                                         (campo opcional no ingresado)
    //   CP02  decisión 1 verdadera, 2 falsa   "abc", máximo 5          -> no lanza error
    //   CP03  decisión 1 y 2 verdaderas       "abcdef", máximo 5       -> IllegalArgumentException
    //   CP04  (valor frontera)                "abcde", máximo 5        -> no lanza error
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: validarDocumento()                               CC = 3
    // Requisito: HU-01 RN-01 (documento válido: DNI, pasaporte o carnet de
    //            extranjería) y CA-02 (DNI de 8 dígitos).
    // Flujo: if (tipo es DNI)                      decisión 1
    //            if (!número tiene 8 dígitos)      decisión 2 -> lanza error
    //        else validarLongitud(número, 12)      (otro tipo de documento)
    //
    //   CP01  DNI, 8 dígitos                 TD01 "45871236"           -> no lanza error
    //   CP02  DNI, formato inválido          TD01 "1234"               -> IllegalArgumentException
    //   CP03  otro documento, largo válido   TD02 "AB1234567"          -> no lanza error
    //   CP04  (dato adicional) DNI con letras  TD01 "4587123A"         -> IllegalArgumentException
    //   CP05  (dato adicional) otro documento, largo mayor a 12
    //                                        TD03 de 13 caracteres     -> IllegalArgumentException
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: obtenerCamposObligatoriosFaltantes()             CC = 3
    //   (incluye agregarSiVacio(...), cuya condición "valor == null ||
    //    valor.trim().isEmpty()" se prueba con valor nulo, en blanco y completo)
    // Requisito: HU-01 RN-03 / CA-05 (campos obligatorios).
    // Flujo: agregarSiVacio(...) para los campos de texto
    //        if (fechaNacimiento == null)   decisión 1
    //        if (nivelIngresos == null)     decisión 2
    //
    //   CP01  ninguna decisión verdadera   cliente completo                -> ""
    //   CP02  solo decisión 1              sin fecha de nacimiento         -> "fecha de nacimiento"
    //   CP03  solo decisión 2              sin nivel de ingresos           -> "nivel de ingresos"
    //   CP04  ambas + texto nulo y en blanco
    //                                      tipo documento null, número "   ",
    //                                      sin fecha y sin ingresos        -> "tipo de documento,
    //                                        número de documento, fecha de nacimiento, nivel de ingresos"
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: quitarEspacios()                                 CC = 4
    // Requisito: HU-01 RN-04 (campos opcionales: cónyuge, referencias y
    //            observaciones) y CA-02 (datos con formato válido).
    // Flujo: se recortan los campos obligatorios (sin decisiones) y, para cada
    //        campo opcional, "si es null se deja null; si no, se recorta":
    //        decisión 1 (cónyuge), decisión 2 (referencias), decisión 3 (observaciones)
    //
    //   CP01  las 3 decisiones con valor null    opcionales sin ingresar    -> siguen null
    //   CP02  solo cónyuge con valor             "  María Pérez  "          -> "María Pérez"
    //   CP03  solo referencias con valor         "  Vecino  "               -> "Vecino"
    //   CP04  solo observaciones con valor       "  Buen pagador  "         -> "Buen pagador"
    //   (en todos los casos los obligatorios con espacios quedan recortados)
    // ======================================================================

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

    // ======================================================================
    // MÉTODO: validar()                                        CC = 7
    // Requisito: HU-01 RN-01, RN-03 y CA-02 / CA-05 (datos obligatorios con
    //            formato válido). Las reglas de documento, mayoría de edad y
    //            largos se prueban en sus propios métodos más arriba; aquí se
    //            cubren las decisiones propias de validar().
    // Flujo: D1  if (hay campos obligatorios faltantes)       -> error
    //        (quitarEspacios, validarDocumento, validarLongitud nombres/apellidos)
    //        D2  if (fecha de nacimiento es futura)           -> error
    //        (validarMayorDeEdad, validarLongitud dirección)
    //        D3  if (teléfono no tiene 9 dígitos)             -> error
    //        D4  if (correo.length() > 100                    -> error
    //        D5      || formato de correo inválido)           -> error
    //        D6  if (nivel de ingresos < 0)                   -> error
    //        (validarLongitud cónyuge, referencias, observaciones)
    //
    //   CP01  todas las decisiones falsas   cliente válido                  -> no lanza error
    //   CP02  D1 verdadera                  cliente vacío                   -> error "campos obligatorios"
    //   CP03  D2 verdadera                  fecha de nacimiento mañana      -> error "fecha de nacimiento"
    //   CP04  D3 verdadera                  teléfono "98765"                -> error "teléfono"
    //   CP05  D4 verdadera                  correo de 101 caracteres        -> error "correo"
    //   CP06  D4 falsa, D5 verdadera        correo "jramirez.example.com"   -> error "correo"
    //   CP07  D6 verdadera                  ingresos -1                     -> error "ingresos"
    //   CP08  (integración) menor de edad   fecha: 17 años                  -> error "mayor de edad"
    //   CP09  (integración) recorta espacios  datos con espacios            -> no lanza error y quedan recortados
    // ======================================================================

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
