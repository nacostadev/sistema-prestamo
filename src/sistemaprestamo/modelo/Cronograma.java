package sistemaprestamo.modelo;

import java.util.Collections;
import java.util.List;

/** Cronograma de pagos guardado y asociado a un préstamo (HU-03 RN-06). */
public class Cronograma {

    private final String codigo;
    private final String prestamoCodigo;
    private final List<DetalleCronograma> detalle;

    public Cronograma(String codigo, String prestamoCodigo, List<DetalleCronograma> detalle) {
        this.codigo = codigo;
        this.prestamoCodigo = prestamoCodigo;
        this.detalle = Collections.unmodifiableList(detalle);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getPrestamoCodigo() {
        return prestamoCodigo;
    }

    public List<DetalleCronograma> getDetalle() {
        return detalle;
    }
}
