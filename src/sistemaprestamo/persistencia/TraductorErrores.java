package sistemaprestamo.persistencia;

import java.sql.SQLException;
import sistemaprestamo.modelo.ReglaNegocioException;

/**
 * Traduce los errores de restricción única de SQL Server (2601 y 2627) a una
 * ReglaNegocioException con un mensaje claro. Las reglas de negocio se validan
 * antes en los servicios; esto cubre el caso de que dos usuarios registren el
 * mismo dato a la vez. Cualquier otro error se devuelve sin cambios.
 */
final class TraductorErrores {

    private static final int INDICE_UNICO_DUPLICADO = 2601;
    private static final int RESTRICCION_UNICA_DUPLICADA = 2627;

    private TraductorErrores() {
    }

    /**
     * Lanza ReglaNegocioException si el error es un valor duplicado; si no,
     * devuelve la misma excepción para que el llamador la relance.
     */
    static SQLException traducir(SQLException excepcion) {
        int codigo = excepcion.getErrorCode();
        if (codigo == INDICE_UNICO_DUPLICADO || codigo == RESTRICCION_UNICA_DUPLICADA) {
            String detalle = excepcion.getMessage() == null ? "" : excepcion.getMessage();
            if (detalle.contains("ClientesDocumentoUQ")) {
                throw new ReglaNegocioException(
                        "Ya existe un cliente registrado con ese número de documento.", excepcion);
            }
            if (detalle.contains("CronogramasPrestamosUQ")) {
                throw new ReglaNegocioException(
                        "El préstamo ya tiene un cronograma generado y no puede modificarse.", excepcion);
            }
            throw new ReglaNegocioException("Ya existe un registro con esos datos.", excepcion);
        }
        return excepcion;
    }
}
