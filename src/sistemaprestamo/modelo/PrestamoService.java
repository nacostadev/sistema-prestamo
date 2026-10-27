package sistemaprestamo.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import sistemaprestamo.persistencia.CatalogoRepository;
import sistemaprestamo.persistencia.ClienteRepository;
import sistemaprestamo.persistencia.PrestamoRepository;

/**
 * Lógica de negocio del registro de préstamos (HU-02).
 * Valida los campos obligatorios y aplica las reglas de negocio (cliente
 * activo, sin morosidad, monto dentro de la política, plazo y TEA del tipo de
 * préstamo) antes de llamar al repositorio, cuyo procedimiento almacenado solo
 * inserta. No contiene consultas SQL ni código Swing.
 */
public class PrestamoService {

    private final PrestamoRepository repositorio;
    private final ClienteRepository clienteRepositorio;
    private final CatalogoRepository catalogoRepositorio;

    public PrestamoService() {
        this.repositorio = new PrestamoRepository();
        this.clienteRepositorio = new ClienteRepository();
        this.catalogoRepositorio = new CatalogoRepository();
    }

    /** Registra un préstamo Vigente (HU-02 CA-02) y devuelve el código asignado. */
    public String registrarPrestamo(Prestamo prestamo) throws SQLException {
        validarPrestamo(prestamo);
        verificarCliente(prestamo.getClienteCodigo());
        verificarCondicionesDelTipo(prestamo);
        prestamo.setEstadoCodigo(obtenerCodigoEstadoVigente());
        return repositorio.registrar(prestamo);
    }

    public List<Prestamo> listarPrestamos() throws SQLException {
        return repositorio.listar();
    }

    /** Devuelve el préstamo con ese código, o null si no existe. */
    public Prestamo consultarPrestamo(String codigoPrestamo) throws SQLException {
        return repositorio.buscarPorCodigo(codigoPrestamo);
    }

    public CatalogosPrestamo cargarCatalogos() throws SQLException {
        return new CatalogosPrestamo(
                catalogoRepositorio.listarTiposPrestamo(),
                catalogoRepositorio.listarModalidadesPago(),
                catalogoRepositorio.listarMonedas(),
                catalogoRepositorio.listarGarantias());
    }

    /** HU-02 RN-01 y RN-06: cliente registrado, activo y sin préstamos en mora. */
    private void verificarCliente(String codigoCliente) throws SQLException {
        Cliente cliente = clienteRepositorio.buscarPorCodigo(codigoCliente);
        if (cliente == null || !cliente.estaActivo()) {
            throw new ReglaNegocioException("El cliente no está registrado o se encuentra inactivo.");
        }
        for (Prestamo existente : repositorio.listarPorCliente(codigoCliente)) {
            if (existente.estaEnMora()) {
                throw new ReglaNegocioException(
                        "El cliente tiene préstamos en estado de morosidad o incumplimiento.");
            }
        }
    }

    /** HU-02 RN-04 y RN-05: monto dentro de la política y plazo y TEA dentro del tipo de préstamo. */
    private void verificarCondicionesDelTipo(Prestamo prestamo) throws SQLException {
        TipoPrestamo tipo = null;
        for (TipoPrestamo candidato : catalogoRepositorio.listarTiposPrestamo()) {
            if (candidato.getCodigo().equals(prestamo.getTipoPrestamoCodigo())) {
                tipo = candidato;
            }
        }
        if (tipo == null) {
            throw new ReglaNegocioException("El tipo de préstamo seleccionado no está registrado.");
        }
        if (prestamo.getPlazoCuotas() < tipo.getPlazoMinimo()
                || prestamo.getPlazoCuotas() > tipo.getPlazoMaximo()) {
            throw new ReglaNegocioException("El plazo no cumple con las condiciones del tipo de préstamo ("
                    + tipo.getPlazoMinimo() + " a " + tipo.getPlazoMaximo() + " cuotas).");
        }
        if (prestamo.getTea().compareTo(tipo.getTasaMinima()) < 0
                || prestamo.getTea().compareTo(tipo.getTasaMaxima()) > 0) {
            throw new ReglaNegocioException("La TEA no cumple con las condiciones del tipo de préstamo ("
                    + porcentaje(tipo.getTasaMinima()) + " a " + porcentaje(tipo.getTasaMaxima()) + ").");
        }
        if (tipo.getMontoMinimo() == null || tipo.getMontoMaximo() == null) {
            throw new ReglaNegocioException(
                    "No existe una política crediticia definida para el tipo de préstamo.");
        }
        if (prestamo.getMontoSolicitado().compareTo(tipo.getMontoMinimo()) < 0
                || prestamo.getMontoSolicitado().compareTo(tipo.getMontoMaximo()) > 0) {
            throw new ReglaNegocioException("El monto no es válido: debe estar entre "
                    + tipo.getMontoMinimo() + " y " + tipo.getMontoMaximo() + ".");
        }
    }

    /** Todo préstamo nuevo se registra como Vigente. */
    private String obtenerCodigoEstadoVigente() throws SQLException {
        for (ItemCatalogo estado : catalogoRepositorio.listarEstadosPrestamo()) {
            if (Prestamo.ESTADO_VIGENTE.equals(estado.getNombre())) {
                return estado.getCodigo();
            }
        }
        throw new ReglaNegocioException("No existe el estado de préstamo \"Vigente\".");
    }

    private String porcentaje(BigDecimal fraccion) {
        return fraccion.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + " %";
    }

    /** Valida campos obligatorios (RN-02, CA-05) y ajusta la escala de los importes. */
    private void validarPrestamo(Prestamo prestamo) {
        if (prestamo == null) {
            throw new IllegalArgumentException("Los datos del préstamo no pueden estar vacíos.");
        }

        StringBuilder faltantes = new StringBuilder();
        agregarSiVacio(faltantes, prestamo.getClienteCodigo(), "cliente");
        agregarSiVacio(faltantes, prestamo.getTipoPrestamoCodigo(), "tipo de préstamo");
        if (prestamo.getMontoSolicitado() == null) {
            agregarFaltante(faltantes, "monto solicitado");
        }
        if (prestamo.getPlazoCuotas() <= 0) {
            agregarFaltante(faltantes, "plazo (número de cuotas)");
        }
        if (prestamo.getTea() == null) {
            agregarFaltante(faltantes, "TEA");
        }
        agregarSiVacio(faltantes, prestamo.getModalidadPagoCodigo(), "modalidad de pago");
        if (prestamo.getFechaDesembolso() == null) {
            agregarFaltante(faltantes, "fecha de desembolso");
        }
        agregarSiVacio(faltantes, prestamo.getMonedaCodigo(), "moneda");
        if (faltantes.length() > 0) {
            throw new IllegalArgumentException("Complete los campos obligatorios: " + faltantes + ".");
        }

        if (prestamo.getMontoSolicitado().signum() <= 0) {
            throw new IllegalArgumentException("El monto solicitado debe ser mayor que cero.");
        }
        if (prestamo.getTea().signum() <= 0) {
            throw new IllegalArgumentException("La TEA debe ser mayor que cero.");
        }
        validarLongitud(prestamo.getObservaciones(), "observaciones");
        validarLongitud(prestamo.getDescripcionDestino(), "descripción del destino del crédito");

        prestamo.setMontoSolicitado(prestamo.getMontoSolicitado().setScale(2, RoundingMode.HALF_UP));
        prestamo.setTea(prestamo.getTea().setScale(4, RoundingMode.HALF_UP));
        if (prestamo.getTea().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La TEA es demasiado pequeña.");
        }
    }

    private void validarLongitud(String valor, String campo) {
        if (valor != null && valor.length() > 255) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' no puede superar los 255 caracteres.");
        }
    }

    private void agregarSiVacio(StringBuilder faltantes, String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            agregarFaltante(faltantes, campo);
        }
    }

    private void agregarFaltante(StringBuilder faltantes, String campo) {
        if (faltantes.length() > 0) {
            faltantes.append(", ");
        }
        faltantes.append(campo);
    }
}
