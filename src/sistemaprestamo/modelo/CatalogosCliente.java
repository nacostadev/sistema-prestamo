package sistemaprestamo.modelo;

import java.util.List;

/** Catálogos que alimentan los combos del formulario de clientes. */
public class CatalogosCliente {

    private final List<ItemCatalogo> tiposDocumento;
    private final List<ItemCatalogo> estadosCiviles;
    private final List<ItemCatalogo> condicionesLaborales;

    public CatalogosCliente(List<ItemCatalogo> tiposDocumento,
                            List<ItemCatalogo> estadosCiviles,
                            List<ItemCatalogo> condicionesLaborales) {
        this.tiposDocumento = tiposDocumento;
        this.estadosCiviles = estadosCiviles;
        this.condicionesLaborales = condicionesLaborales;
    }

    public List<ItemCatalogo> getTiposDocumento() {
        return tiposDocumento;
    }

    public List<ItemCatalogo> getEstadosCiviles() {
        return estadosCiviles;
    }

    public List<ItemCatalogo> getCondicionesLaborales() {
        return condicionesLaborales;
    }
}
