package sistemaprestamo.modelo;

import java.util.Objects;

public class ItemCatalogo {

    private final String codigo;
    private final String nombre;

    public ItemCatalogo(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof ItemCatalogo)) {
            return false;
        }
        return Objects.equals(codigo, ((ItemCatalogo) otro).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
