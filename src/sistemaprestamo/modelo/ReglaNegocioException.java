package sistemaprestamo.modelo;

/**
 * Se lanza cuando una operación incumple una regla de negocio de las historias
 * de usuario (documento duplicado, cliente con préstamos, monto fuera de
 * política, etc.). El mensaje es apto para mostrarse al usuario.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

    public ReglaNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
