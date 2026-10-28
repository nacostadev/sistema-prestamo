package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import sistemaprestamo.modelo.Prestamo;
import sistemaprestamo.modelo.TipoPrestamo;

public class PrestamoDAO {

    private static final String SQL_MAXIMO_CODIGO = "SELECT MAX(PrestamosCodigo) FROM Prestamo.Prestamos";

    private static final String SQL_INSERTAR =
            "INSERT INTO Prestamo.Prestamos "
            + "(PrestamosCodigo, ClientesCodigo, TiposPrestamoCodigo, PrestamosMontoSolicitado, "
            + "PrestamosPlazoCuotas, PrestamosTEA, ModalidadesPagoCodigo, PrestamosFechaDesembolso, "
            + "MonedasCodigo, GarantiasCodigo, PrestamosObservaciones, PrestamosDescripcionDestino, "
            + "EstadosPrestamoCodigo) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "(SELECT EstadosPrestamoCodigo FROM Prestamo.EstadosPrestamo WHERE EstadosPrestamoNombre = ?))";

    private static final String SQL_CONSULTAR =
            "SELECT p.PrestamosCodigo, p.ClientesCodigo, c.ClientesApellidos, c.ClientesNombres, "
            + "p.TiposPrestamoCodigo, t.TiposPrestamoNombre, p.PrestamosMontoSolicitado, "
            + "p.PrestamosPlazoCuotas, p.PrestamosTEA, p.ModalidadesPagoCodigo, mp.ModalidadesPagoNombre, "
            + "p.PrestamosFechaDesembolso, p.MonedasCodigo, m.MonedasSimbolo, p.GarantiasCodigo, "
            + "p.PrestamosObservaciones, p.PrestamosDescripcionDestino, p.EstadosPrestamoCodigo, "
            + "e.EstadosPrestamoNombre "
            + "FROM Prestamo.Prestamos AS p "
            + "INNER JOIN Cliente.Clientes AS c ON c.ClientesCodigo = p.ClientesCodigo "
            + "INNER JOIN Prestamo.TiposPrestamo AS t ON t.TiposPrestamoCodigo = p.TiposPrestamoCodigo "
            + "INNER JOIN Prestamo.ModalidadesPago AS mp ON mp.ModalidadesPagoCodigo = p.ModalidadesPagoCodigo "
            + "INNER JOIN Prestamo.Monedas AS m ON m.MonedasCodigo = p.MonedasCodigo "
            + "INNER JOIN Prestamo.EstadosPrestamo AS e ON e.EstadosPrestamoCodigo = p.EstadosPrestamoCodigo ";

    private static final String SQL_TIPO_PRESTAMO =
            "SELECT t.TiposPrestamoCodigo, t.TiposPrestamoNombre, "
            + "t.TiposPrestamoPlazoMinimo, t.TiposPrestamoPlazoMaximo, "
            + "t.TiposPrestamoTasaMinima, t.TiposPrestamoTasaMaxima, "
            + "p.PoliticaCreditoMontoMinimo, p.PoliticaCreditoMontoMaximo "
            + "FROM Prestamo.TiposPrestamo AS t "
            + "LEFT JOIN Prestamo.PoliticaCredito AS p ON p.TiposPrestamoCodigo = t.TiposPrestamoCodigo "
            + "WHERE t.TiposPrestamoCodigo = ?";

    public String registrar(Prestamo prestamo) throws SQLException {
        try (Connection conexion = Conexion.getConnection()) {
            String codigo = Conexion.siguienteCodigo(conexion, SQL_MAXIMO_CODIGO, "PR", 4);
            try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {
                sentencia.setString(1, codigo);
                sentencia.setString(2, prestamo.getClienteCodigo());
                sentencia.setString(3, prestamo.getTipoPrestamoCodigo());
                sentencia.setBigDecimal(4, prestamo.getMontoSolicitado());
                sentencia.setInt(5, prestamo.getPlazoCuotas());
                sentencia.setBigDecimal(6, prestamo.getTea());
                sentencia.setString(7, prestamo.getModalidadPagoCodigo());
                sentencia.setDate(8, java.sql.Date.valueOf(prestamo.getFechaDesembolso()));
                sentencia.setString(9, prestamo.getMonedaCodigo());
                asignarOpcional(sentencia, 10, prestamo.getGarantiaCodigo());
                asignarOpcional(sentencia, 11, prestamo.getObservaciones());
                asignarOpcional(sentencia, 12, prestamo.getDescripcionDestino());
                sentencia.setString(13, Prestamo.ESTADO_VIGENTE);
                sentencia.executeUpdate();
            }
            prestamo.setCodigo(codigo);
            return codigo;
        }
    }

    public List<Prestamo> listar() throws SQLException {
        return consultar("", null);
    }

    public Prestamo buscarPorCodigo(String codigoPrestamo) throws SQLException {
        List<Prestamo> encontrados = consultar("WHERE p.PrestamosCodigo = ? ", codigoPrestamo);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    public List<Prestamo> listarPorCliente(String codigoCliente) throws SQLException {
        return consultar("WHERE p.ClientesCodigo = ? ", codigoCliente);
    }


    public TipoPrestamo buscarTipoPrestamo(String codigoTipo) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_TIPO_PRESTAMO)) {
            sentencia.setString(1, codigoTipo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }
                return new TipoPrestamo(
                        resultado.getString("TiposPrestamoCodigo").trim(),
                        resultado.getString("TiposPrestamoNombre"),
                        resultado.getInt("TiposPrestamoPlazoMinimo"),
                        resultado.getInt("TiposPrestamoPlazoMaximo"),
                        resultado.getBigDecimal("TiposPrestamoTasaMinima"),
                        resultado.getBigDecimal("TiposPrestamoTasaMaxima"),
                        resultado.getBigDecimal("PoliticaCreditoMontoMinimo"),
                        resultado.getBigDecimal("PoliticaCreditoMontoMaximo"));
            }
        }
    }

    private List<Prestamo> consultar(String filtro, String valor) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(
                     SQL_CONSULTAR + filtro + "ORDER BY p.PrestamosCodigo")) {
            if (valor != null) {
                sentencia.setString(1, valor);
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    prestamos.add(mapearPrestamo(resultado));
                }
            }
        }
        return prestamos;
    }

    private void asignarOpcional(PreparedStatement sentencia, int posicion, String valor)
            throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            sentencia.setNull(posicion, Types.NVARCHAR);
        } else {
            sentencia.setString(posicion, valor.trim());
        }
    }

    private Prestamo mapearPrestamo(ResultSet resultado) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setCodigo(resultado.getString("PrestamosCodigo").trim());
        prestamo.setClienteCodigo(resultado.getString("ClientesCodigo").trim());
        prestamo.setClienteNombre(resultado.getString("ClientesNombres") + " "
                + resultado.getString("ClientesApellidos"));
        prestamo.setTipoPrestamoCodigo(resultado.getString("TiposPrestamoCodigo").trim());
        prestamo.setTipoPrestamoNombre(resultado.getString("TiposPrestamoNombre"));
        prestamo.setMontoSolicitado(resultado.getBigDecimal("PrestamosMontoSolicitado"));
        prestamo.setPlazoCuotas(resultado.getInt("PrestamosPlazoCuotas"));
        prestamo.setTea(resultado.getBigDecimal("PrestamosTEA"));
        prestamo.setModalidadPagoCodigo(resultado.getString("ModalidadesPagoCodigo").trim());
        prestamo.setModalidadPagoNombre(resultado.getString("ModalidadesPagoNombre"));
        prestamo.setFechaDesembolso(resultado.getDate("PrestamosFechaDesembolso").toLocalDate());
        prestamo.setMonedaCodigo(resultado.getString("MonedasCodigo").trim());
        prestamo.setMonedaSimbolo(resultado.getString("MonedasSimbolo"));
        String garantia = resultado.getString("GarantiasCodigo");
        prestamo.setGarantiaCodigo(garantia == null ? null : garantia.trim());
        prestamo.setObservaciones(resultado.getString("PrestamosObservaciones"));
        prestamo.setDescripcionDestino(resultado.getString("PrestamosDescripcionDestino"));
        prestamo.setEstadoCodigo(resultado.getString("EstadosPrestamoCodigo").trim());
        prestamo.setEstadoNombre(resultado.getString("EstadosPrestamoNombre"));
        return prestamo;
    }
}
