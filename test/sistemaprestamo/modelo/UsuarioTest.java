package sistemaprestamo.modelo;

import org.junit.Test;
import static org.junit.Assert.*;

/** Pruebas del login (HU-01 RN-07). El verificador de clave se simula comparando textos. */
public class UsuarioTest {

    private Usuario crearUsuario(String estado) {
        return new Usuario("U001", "nacosta", "hash-de-prueba", estado, "AN01");
    }

    /** Simula BCrypt: la clave correcta es "Prueba2026!". */
    private boolean verificar(String clave, String hash) {
        return "Prueba2026!".equals(clave) && "hash-de-prueba".equals(hash);
    }

    @Test
    public void testIniciarSesionCorrecto() {
        System.out.println("iniciarSesion - correcto");
        Usuario usuario = crearUsuario("A");
        assertTrue(usuario == Usuario.iniciarSesion("nacosta", "Prueba2026!", usuario, this::verificar));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIniciarSesionSinDatos() {
        System.out.println("iniciarSesion - sin datos");
        Usuario.iniciarSesion("", "", crearUsuario("A"), this::verificar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIniciarSesionUsuarioInexistente() {
        System.out.println("iniciarSesion - usuario inexistente");
        Usuario.iniciarSesion("otro", "Prueba2026!", null, this::verificar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIniciarSesionClaveIncorrecta() {
        System.out.println("iniciarSesion - clave incorrecta");
        Usuario.iniciarSesion("nacosta", "mala", crearUsuario("A"), this::verificar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIniciarSesionUsuarioInactivo() {
        System.out.println("iniciarSesion - usuario inactivo");
        Usuario.iniciarSesion("nacosta", "Prueba2026!", crearUsuario("I"), this::verificar);
    }

    @Test
    public void testValidarSesionIniciada() {
        System.out.println("validarSesionIniciada - con sesion");
        Usuario.validarSesionIniciada(crearUsuario("A"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidarSesionIniciadaSinSesion() {
        System.out.println("validarSesionIniciada - sin sesion");
        Usuario.validarSesionIniciada(null);
    }
}
