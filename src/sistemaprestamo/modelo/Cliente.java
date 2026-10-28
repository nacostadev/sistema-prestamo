package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Entidad Cliente (HU-01): representa un registro de Cliente.Clientes y
 * contiene sus reglas de negocio (sección REGLAS DE NEGOCIO).
 * Los atributos terminados en "Nombre" se llenan al consultar y no se envían
 * a la base de datos.
 */
public class Cliente {

    public static final String ESTADO_ACTIVO = "A";
    public static final String ESTADO_INACTIVO = "I";

    /** Código de "DNI" en Cliente.TiposDocumento (datos iniciales del script). */
    private static final String TIPO_DOCUMENTO_DNI = "TD01";

    // Opciones de los combos. Sus códigos coinciden con los datos iniciales del script SQL.
    /** HU-01 RN-01: DNI, pasaporte o carnet de extranjería. */
    public static final List<ItemCatalogo> TIPOS_DOCUMENTO = List.of(
            new ItemCatalogo("TD01", "DNI"),
            new ItemCatalogo("TD02", "Pasaporte"),
            new ItemCatalogo("TD03", "Carnet de Extranjería"));
    /** Valores supuestos: la HU-01 no define la lista de estados civiles. */
    public static final List<ItemCatalogo> ESTADOS_CIVILES = List.of(
            new ItemCatalogo("EC01", "Soltero(a)"),
            new ItemCatalogo("EC02", "Casado(a)"),
            new ItemCatalogo("EC03", "Divorciado(a)"),
            new ItemCatalogo("EC04", "Viudo(a)"),
            new ItemCatalogo("EC05", "Conviviente"));
    /** Valores supuestos: la HU-01 no define la lista de condiciones laborales. */
    public static final List<ItemCatalogo> CONDICIONES_LABORALES = List.of(
            new ItemCatalogo("CL01", "Dependiente"),
            new ItemCatalogo("CL02", "Independiente"),
            new ItemCatalogo("CL03", "Desempleado"),
            new ItemCatalogo("CL04", "Jubilado"));

    /** HU-01 RN-01: edad mínima para ser cliente. */
    private static final int EDAD_MINIMA = 18;

    private static final String EXPRESION_DNI = "^\\d{8}$";
    private static final String EXPRESION_CORREO = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";
    private static final String EXPRESION_TELEFONO = "^\\d{9}$";

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

    // REGLAS DE NEGOCIO

    /**
     * HU-01 RN-01, RN-03 y CA-05: verifica los campos obligatorios y sus
     * formatos. Quita los espacios sobrantes de los textos.
     */
    public void validar() {
        String faltantes = obtenerCamposObligatoriosFaltantes();
        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException("Complete los campos obligatorios: " + faltantes + ".");
        }

        quitarEspacios();

        validarDocumento();
        validarLongitud(nombres, "nombres", 100);
        validarLongitud(apellidos, "apellidos", 100);
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        validarMayorDeEdad();
        validarLongitud(direccion, "dirección", 150);
        if (!telefono.matches(EXPRESION_TELEFONO)) {
            throw new IllegalArgumentException("El teléfono debe contener exactamente 9 dígitos.");
        }
        if (correo.length() > 100 || !correo.matches(EXPRESION_CORREO)) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        if (nivelIngresos.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El nivel de ingresos no puede ser negativo.");
        }
        validarLongitud(nombreConyuge, "nombre del cónyuge", 200);
        validarLongitud(referenciasPersonales, "referencias personales", 255);
        validarLongitud(observaciones, "observaciones", 255);
    }

    /**
     * HU-01 RN-02 y CA-03: el número de documento no puede repetirse.
     *
     * @param clientesConElMismoDocumento clientes que ya tienen este número de documento
     */
    public void validarDocumentoUnico(List<Cliente> clientesConElMismoDocumento) {
        for (Cliente existente : clientesConElMismoDocumento) {
            if (!existente.getCodigo().equals(codigo)) {
                throw new IllegalArgumentException(
                        "Ya existe un cliente registrado con ese número de documento.");
            }
        }
    }

    /** HU-01 RN-05: solo se opera sobre clientes registrados. */
    public static void validarRegistrado(Cliente clienteRegistrado) {
        if (clienteRegistrado == null) {
            throw new IllegalArgumentException("El cliente no está registrado en el sistema.");
        }
    }

    /**
     * HU-01 RN-06 y CA-06: no se desactiva un cliente con préstamos.
     * DEFINICIÓN (indicada por el equipo): "préstamo activo" es un préstamo en
     * estado Vigente. Las solicitudes en evaluación no existen en el sistema.
     *
     * @param cantidadPrestamosVigentes préstamos del cliente en estado Vigente
     */
    public void validarPuedeDesactivarse(int cantidadPrestamosVigentes) {
        if (cantidadPrestamosVigentes > 0) {
            throw new IllegalArgumentException(
                    "No se puede desactivar al cliente porque tiene préstamos vigentes.");
        }
    }

    private String obtenerCamposObligatoriosFaltantes() {
        List<String> faltantes = new java.util.ArrayList<>();
        agregarSiVacio(faltantes, tipoDocumentoCodigo, "tipo de documento");
        agregarSiVacio(faltantes, numeroDocumento, "número de documento");
        agregarSiVacio(faltantes, nombres, "nombres");
        agregarSiVacio(faltantes, apellidos, "apellidos");
        if (fechaNacimiento == null) {
            faltantes.add("fecha de nacimiento");
        }
        agregarSiVacio(faltantes, estadoCivilCodigo, "estado civil");
        agregarSiVacio(faltantes, direccion, "dirección");
        agregarSiVacio(faltantes, telefono, "teléfono");
        agregarSiVacio(faltantes, correo, "correo electrónico");
        agregarSiVacio(faltantes, condicionLaboralCodigo, "condición laboral");
        if (nivelIngresos == null) {
            faltantes.add("nivel de ingresos");
        }
        return String.join(", ", faltantes);
    }

    private void agregarSiVacio(List<String> faltantes, String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            faltantes.add(campo);
        }
    }

    private void quitarEspacios() {
        numeroDocumento = numeroDocumento.trim();
        nombres = nombres.trim();
        apellidos = apellidos.trim();
        direccion = direccion.trim();
        telefono = telefono.trim();
        correo = correo.trim();
        nombreConyuge = nombreConyuge == null ? null : nombreConyuge.trim();
        referenciasPersonales = referenciasPersonales == null ? null : referenciasPersonales.trim();
        observaciones = observaciones == null ? null : observaciones.trim();
    }

    /** HU-01 RN-01: el cliente debe ser mayor de edad (18 años cumplidos). */
    private void validarMayorDeEdad() {
        if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < EDAD_MINIMA) {
            throw new IllegalArgumentException("El cliente debe ser mayor de edad (18 años o más).");
        }
    }

    private void validarDocumento() {
        if (TIPO_DOCUMENTO_DNI.equals(tipoDocumentoCodigo)) {
            if (!numeroDocumento.matches(EXPRESION_DNI)) {
                throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos.");
            }
        } else {
            // El formato de pasaporte y carnet de extranjería no está definido en la HU:
            // solo se respeta el largo máximo de la columna.
            validarLongitud(numeroDocumento, "número de documento", 12);
        }
    }

    private void validarLongitud(String valor, String campo, int maximo) {
        if (valor != null && valor.length() > maximo) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' no puede superar los " + maximo + " caracteres.");
        }
    }
}
