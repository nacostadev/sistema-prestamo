package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Préstamo: representa un registro de Prestamo.Prestamos.
 * La TEA se maneja como fracción decimal (0.2000 = 20 %), igual que en la base
 * de datos. Los atributos terminados en "Nombre" o "Simbolo" se llenan al
 * consultar y no se envían a la base de datos.
 */
public class Prestamo {

    public static final String ESTADO_VIGENTE = "Vigente";
    public static final String ESTADO_MOROSO = "Moroso";
    public static final String ESTADO_INCUMPLIMIENTO = "Incumplimiento";

    private String codigo;
    private String clienteCodigo;
    private String clienteNombre;
    private String tipoPrestamoCodigo;
    private String tipoPrestamoNombre;
    private BigDecimal montoSolicitado;
    private int plazoCuotas;
    private BigDecimal tea;
    private String modalidadPagoCodigo;
    private String modalidadPagoNombre;
    private LocalDate fechaDesembolso;
    private String monedaCodigo;
    private String monedaSimbolo;
    private String garantiaCodigo;
    private String observaciones;
    private String descripcionDestino;
    private String estadoCodigo;
    private String estadoNombre;

    public Prestamo() {
    }

    public boolean estaVigente() {
        return ESTADO_VIGENTE.equals(estadoNombre);
    }

    /** Moroso o en incumplimiento (HU-02 RN-06). */
    public boolean estaEnMora() {
        return ESTADO_MOROSO.equals(estadoNombre) || ESTADO_INCUMPLIMIENTO.equals(estadoNombre);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getClienteCodigo() {
        return clienteCodigo;
    }

    public void setClienteCodigo(String clienteCodigo) {
        this.clienteCodigo = clienteCodigo;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getTipoPrestamoCodigo() {
        return tipoPrestamoCodigo;
    }

    public void setTipoPrestamoCodigo(String tipoPrestamoCodigo) {
        this.tipoPrestamoCodigo = tipoPrestamoCodigo;
    }

    public String getTipoPrestamoNombre() {
        return tipoPrestamoNombre;
    }

    public void setTipoPrestamoNombre(String tipoPrestamoNombre) {
        this.tipoPrestamoNombre = tipoPrestamoNombre;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(BigDecimal montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public int getPlazoCuotas() {
        return plazoCuotas;
    }

    public void setPlazoCuotas(int plazoCuotas) {
        this.plazoCuotas = plazoCuotas;
    }

    public BigDecimal getTea() {
        return tea;
    }

    public void setTea(BigDecimal tea) {
        this.tea = tea;
    }

    public String getModalidadPagoCodigo() {
        return modalidadPagoCodigo;
    }

    public void setModalidadPagoCodigo(String modalidadPagoCodigo) {
        this.modalidadPagoCodigo = modalidadPagoCodigo;
    }

    public String getModalidadPagoNombre() {
        return modalidadPagoNombre;
    }

    public void setModalidadPagoNombre(String modalidadPagoNombre) {
        this.modalidadPagoNombre = modalidadPagoNombre;
    }

    public LocalDate getFechaDesembolso() {
        return fechaDesembolso;
    }

    public void setFechaDesembolso(LocalDate fechaDesembolso) {
        this.fechaDesembolso = fechaDesembolso;
    }

    public String getMonedaCodigo() {
        return monedaCodigo;
    }

    public void setMonedaCodigo(String monedaCodigo) {
        this.monedaCodigo = monedaCodigo;
    }

    public String getMonedaSimbolo() {
        return monedaSimbolo;
    }

    public void setMonedaSimbolo(String monedaSimbolo) {
        this.monedaSimbolo = monedaSimbolo;
    }

    public String getGarantiaCodigo() {
        return garantiaCodigo;
    }

    public void setGarantiaCodigo(String garantiaCodigo) {
        this.garantiaCodigo = garantiaCodigo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDescripcionDestino() {
        return descripcionDestino;
    }

    public void setDescripcionDestino(String descripcionDestino) {
        this.descripcionDestino = descripcionDestino;
    }

    public String getEstadoCodigo() {
        return estadoCodigo;
    }

    public void setEstadoCodigo(String estadoCodigo) {
        this.estadoCodigo = estadoCodigo;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    @Override
    public String toString() {
        return codigo + " - " + clienteNombre + " (" + estadoNombre + ")";
    }
}
