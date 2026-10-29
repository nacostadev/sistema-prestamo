package sistemaprestamo.modelo;

/** HU-02 RN-04 / CA-04: el monto no está dentro de los límites de la política crediticia. */
public class MontoInvalidoException extends IllegalArgumentException {

    public MontoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
