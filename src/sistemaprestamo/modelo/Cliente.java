package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Cliente: representa un registro de Cliente.Clientes.
 * Solo contiene atributos, getters y setters. Los atributos terminados en
 * "Nombre" se llenan al consultar y no se envían a la base de datos.
 */
public class Cliente {

    public static final String ESTADO_ACTIVO = "A";
    public static final String ESTADO_INACTIVO = "I";

    private String codigo;
    private String tipoDocumentoCodigo;
    private String tipoDocumentoNombre;
    private String numeroDocumento;
    private String apellidos;
    private String nombres;
    private LocalDate fechaNacimiento;
    private String estadoCivilCodigo;
    private String estadoCivilNombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String condicionLaboralCodigo;
    private String condicionLaboralNombre;
    private BigDecimal nivelIngresos;
    private String nombreConyuge;
    private String referenciasPersonales;
    private String observaciones;
    private String estado;

    public Cliente() {
    }

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipoDocumentoCodigo() {
        return tipoDocumentoCodigo;
    }

    public void setTipoDocumentoCodigo(String tipoDocumentoCodigo) {
        this.tipoDocumentoCodigo = tipoDocumentoCodigo;
    }

    public String getTipoDocumentoNombre() {
        return tipoDocumentoNombre;
    }

    public void setTipoDocumentoNombre(String tipoDocumentoNombre) {
        this.tipoDocumentoNombre = tipoDocumentoNombre;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEstadoCivilCodigo() {
        return estadoCivilCodigo;
    }

    public void setEstadoCivilCodigo(String estadoCivilCodigo) {
        this.estadoCivilCodigo = estadoCivilCodigo;
    }

    public String getEstadoCivilNombre() {
        return estadoCivilNombre;
    }

    public void setEstadoCivilNombre(String estadoCivilNombre) {
        this.estadoCivilNombre = estadoCivilNombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCondicionLaboralCodigo() {
        return condicionLaboralCodigo;
    }

    public void setCondicionLaboralCodigo(String condicionLaboralCodigo) {
        this.condicionLaboralCodigo = condicionLaboralCodigo;
    }

    public String getCondicionLaboralNombre() {
        return condicionLaboralNombre;
    }

    public void setCondicionLaboralNombre(String condicionLaboralNombre) {
        this.condicionLaboralNombre = condicionLaboralNombre;
    }

    public BigDecimal getNivelIngresos() {
        return nivelIngresos;
    }

    public void setNivelIngresos(BigDecimal nivelIngresos) {
        this.nivelIngresos = nivelIngresos;
    }

    public String getNombreConyuge() {
        return nombreConyuge;
    }

    public void setNombreConyuge(String nombreConyuge) {
        this.nombreConyuge = nombreConyuge;
    }

    public String getReferenciasPersonales() {
        return referenciasPersonales;
    }

    public void setReferenciasPersonales(String referenciasPersonales) {
        this.referenciasPersonales = referenciasPersonales;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombres + " " + apellidos + " (" + numeroDocumento + ")";
    }
}
