/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package sistemaprestamo.modelo;

/**
 * Guarda al Analista de Crédito que inició sesión (HU-01 RN-07).
 * Los controladores consultan esta clase antes de registrar, modificar o desactivar.
 */
public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static void cerrar() {
        usuarioActual = null;
    }
}
