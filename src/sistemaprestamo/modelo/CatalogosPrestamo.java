package sistemaprestamo.modelo;

import java.util.List;

/** Catálogos que alimentan los combos del formulario de préstamos. */
public class CatalogosPrestamo {

    private final List<TipoPrestamo> tiposPrestamo;
    private final List<ItemCatalogo> modalidadesPago;
    private final List<ItemCatalogo> monedas;
    private final List<ItemCatalogo> garantias;

    public CatalogosPrestamo(List<TipoPrestamo> tiposPrestamo,
                             List<ItemCatalogo> modalidadesPago,
                             List<ItemCatalogo> monedas,
                             List<ItemCatalogo> garantias) {
        this.tiposPrestamo = tiposPrestamo;
        this.modalidadesPago = modalidadesPago;
        this.monedas = monedas;
        this.garantias = garantias;
    }

    public List<TipoPrestamo> getTiposPrestamo() {
        return tiposPrestamo;
    }

    public List<ItemCatalogo> getModalidadesPago() {
        return modalidadesPago;
    }

    public List<ItemCatalogo> getMonedas() {
        return monedas;
    }

    public List<ItemCatalogo> getGarantias() {
        return garantias;
    }
}
