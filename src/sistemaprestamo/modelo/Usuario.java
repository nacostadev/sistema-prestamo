package sistemaprestamo.modelo;

import java.util.function.BiPredicate;

/**
 * Usuario del sistema (Seguridad.Usuarios): el Analista de Crédito que inicia
 * sesión. Contiene la lógica del login (HU-01 RN-07).
 * Por ahora es solo lógica: aún no hay formulario de login ni consulta a la
 * base de datos.
 */
public class Usuario {

    public static final String ESTADO_ACTIVO = "A";

    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos.";

    private String codigo;
    private String nombre;
    private String contraseniaHash;
    private String estado;
    private String analistaCodigo;

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

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }

    // REGLAS DE NEGOCIO

    /**
     * HU-01 RN-07: verifica las credenciales ingresadas contra el usuario
     * registrado. Falla con un mensaje genérico para no revelar si el error
     * fue el usuario o la contraseña.
     *
     * @param nombreIngresado    usuario escrito en el login
     * @param claveIngresada     contraseña escrita en el login
     * @param usuarioRegistrado  usuario encontrado en la base de datos por su nombre, o null
     * @param verificadorClave   compara (clave ingresada, hash guardado); las contraseñas
     *                           están guardadas con BCrypt, que se conectará aquí
     *                           (por ejemplo BCrypt::checkpw)
     * @return el usuario autenticado
     */
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

    /**
     * HU-01 RN-07: registrar, modificar o desactivar clientes exige un analista
     * autenticado.
     *
     * @param usuarioEnSesion usuario que inició sesión, o null si nadie lo hizo
     */
    public static void validarSesionIniciada(Usuario usuarioEnSesion) {
        if (usuarioEnSesion == null) {
            throw new IllegalArgumentException("Debe iniciar sesión como Analista de Crédito para realizar esta acción.");
        }
    }
}
