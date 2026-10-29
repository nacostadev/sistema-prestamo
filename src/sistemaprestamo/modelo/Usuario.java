package sistemaprestamo.modelo;

import java.util.function.BiPredicate;


public class Usuario {

    public static final String ESTADO_ACTIVO = "A";

    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos.";

    private String codigo;
    private String nombre;
    private String contraseniaHash;
    private String estado;
    private String analistaCodigo;
    private String analistaNombre;

    public Usuario() {
    }

    public Usuario(String codigo, String nombre, String contraseniaHash, String estado, String analistaCodigo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.contraseniaHash = contraseniaHash;
        this.estado = estado;
        this.analistaCodigo = analistaCodigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseniaHash() {
        return contraseniaHash;
    }

    public void setContraseniaHash(String contraseniaHash) {
        this.contraseniaHash = contraseniaHash;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAnalistaCodigo() {
        return analistaCodigo;
    }

    public void setAnalistaCodigo(String analistaCodigo) {
        this.analistaCodigo = analistaCodigo;
    }

    /** Nombres y apellidos del analista (se muestra en el menú). */
    public String getAnalistaNombre() {
        return analistaNombre;
    }

    public void setAnalistaNombre(String analistaNombre) {
        this.analistaNombre = analistaNombre;
    }

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }

    // REGLAS DE NEGOCIO

    public static Usuario iniciarSesion(String nombreIngresado, String claveIngresada,
                                        Usuario usuarioRegistrado,
                                        BiPredicate<String, String> verificadorClave) {
        if (nombreIngresado == null || nombreIngresado.trim().isEmpty()
                || claveIngresada == null || claveIngresada.isEmpty()) {
            throw new IllegalArgumentException("Ingrese su usuario y contraseña.");
        }
        if (usuarioRegistrado == null
                || !usuarioRegistrado.getNombre().equals(nombreIngresado.trim())
                || !verificadorClave.test(claveIngresada, usuarioRegistrado.getContraseniaHash())) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES_INVALIDAS);
        }
        if (!usuarioRegistrado.estaActivo()) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo.");
        }
        return usuarioRegistrado;
    }

    public static void validarSesionIniciada(Usuario usuarioEnSesion) {
        if (usuarioEnSesion == null) {
            throw new IllegalArgumentException("Debe iniciar sesión como Analista de Crédito para realizar esta acción.");
        }
    }
}
