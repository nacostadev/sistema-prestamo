package sistemaprestamo.modelo;

import java.util.Objects;

/**
 * Elemento de un catálogo (tipo de documento, estado civil, moneda, etc.)
 * formado por un código y un nombre. Su texto es el nombre, por lo que puede
 * usarse directamente en un JComboBox.
 */
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
