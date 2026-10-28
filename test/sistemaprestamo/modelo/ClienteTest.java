package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/** Pruebas de las reglas de Cliente (HU-01). */
public class ClienteTest {

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
        cliente.setCondicionLaboralCodigo("CL01");
        cliente.setNivelIngresos(new BigDecimal("2500.00"));
        cliente.setEstado(Cliente.ESTADO_ACTIVO);
        return cliente;
    }

    @Test
    public void testValidarClienteCompleto() {
        System.out.println("validar - cliente completo");
        crearClienteValido().validar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarSinCamposObligatorios() {
        System.out.println("validar - sin campos obligatorios");
        new Cliente().validar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarMenorDeEdad() {
        System.out.println("validar - menor de edad");
        Cliente cliente = crearClienteValido();
        cliente.setFechaNacimiento(LocalDate.now().minusYears(18).plusDays(1));
        cliente.validar();
    }

    @Test
    public void testValidarExactamente18Anios() {
        System.out.println("validar - 18 anios cumplidos hoy");
        Cliente cliente = crearClienteValido();
        cliente.setFechaNacimiento(LocalDate.now().minusYears(18));
        cliente.validar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarDniInvalido() {
        System.out.println("validar - dni invalido");
        Cliente cliente = crearClienteValido();
        cliente.setNumeroDocumento("1234");
        cliente.validar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarTelefonoInvalido() {
        System.out.println("validar - telefono invalido");
        Cliente cliente = crearClienteValido();
        cliente.setTelefono("12345");
        cliente.validar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarCorreoInvalido() {
        System.out.println("validar - correo invalido");
        Cliente cliente = crearClienteValido();
        cliente.setCorreo("correo-sin-arroba");
        cliente.validar();
    }

    @Test
    public void testValidarDocumentoUnicoSinCoincidencias() {
        System.out.println("validarDocumentoUnico - sin coincidencias");
        crearClienteValido().validarDocumentoUnico(Collections.<Cliente>emptyList());
    }

    @Test
    public void testValidarDocumentoUnicoConElMismoCliente() {
        System.out.println("validarDocumentoUnico - el mismo cliente");
        Cliente cliente = crearClienteValido();
        cliente.validarDocumentoUnico(Collections.singletonList(crearClienteValido()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarDocumentoUnicoDuplicado() {
        System.out.println("validarDocumentoUnico - duplicado");
        Cliente otro = crearClienteValido();
        otro.setCodigo("CL09");
        crearClienteValido().validarDocumentoUnico(Collections.singletonList(otro));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarRegistradoInexistente() {
        System.out.println("validarRegistrado - inexistente");
        Cliente.validarRegistrado(null);
    }

    @Test
    public void testValidarPuedeDesactivarseSinPrestamos() {
        System.out.println("validarPuedeDesactivarse - sin prestamos vigentes");
        crearClienteValido().validarPuedeDesactivarse(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarPuedeDesactivarseConPrestamos() {
        System.out.println("validarPuedeDesactivarse - con prestamo vigente");
        crearClienteValido().validarPuedeDesactivarse(1);
    }
}
