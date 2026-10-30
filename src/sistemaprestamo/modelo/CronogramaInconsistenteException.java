package sistemaprestamo.modelo;

public class CronogramaInconsistenteException extends IllegalArgumentException {

    public CronogramaInconsistenteException(String mensaje) {
        super(mensaje);
    }
}
