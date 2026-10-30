/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemaprestamo.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sistemaprestamo.modelo.Usuario;

/**
 * Consulta de usuarios para el inicio de sesión (esquemas Seguridad y Personal).
 * Solo lee datos: la validación de la contraseña y del estado se hace en Java (Usuario.iniciarSesion).
 */
public class UsuarioDAO {

    private static final String SQL_BUSCAR_POR_NOMBRE =
            "SELECT u.UsuariosCodigo, u.UsuariosNombre, u.UsuariosContrasenia, u.UsuariosEstado, "
            + "u.AnalistasCodigo, a.AnalistasNombres, a.AnalistasApellidos "
            + "FROM Seguridad.Usuarios AS u "
            + "INNER JOIN Personal.Analistas AS a ON a.AnalistasCodigo = u.AnalistasCodigo "
            + "WHERE u.UsuariosNombre = ?";

    private static final String SQL_ANALISTA_AUTORIZADO =
            "SELECT 1 FROM Seguridad.Usuarios AS u "
            + "INNER JOIN Personal.Analistas AS a ON a.AnalistasCodigo = u.AnalistasCodigo "
            + "WHERE u.UsuariosCodigo = ? AND u.UsuariosEstado = 'A'";

    /** Devuelve el usuario con ese nombre, o null si no existe. */
    public Usuario buscarPorNombre(String nombre) throws SQLException {
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {

            sentencia.setString(1, nombre);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }
                Usuario usuario = new Usuario(
                        resultado.getString("UsuariosCodigo").trim(),
                        resultado.getString("UsuariosNombre"),
                        resultado.getString("UsuariosContrasenia"),
                        resultado.getString("UsuariosEstado").trim(),
                        resultado.getString("AnalistasCodigo").trim());
                usuario.setAnalistaNombre(resultado.getString("AnalistasNombres").trim()
                        + " " + resultado.getString("AnalistasApellidos").trim());
                return usuario;
            }
        }
    }

    /** Comprueba que el usuario de la sesión siga activo y asociado a un analista. */
    public boolean estaAutorizado(Usuario usuario) throws SQLException {
        if (usuario == null || usuario.getCodigo() == null) {
            return false;
        }
        try (Connection conexion = Conexion.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ANALISTA_AUTORIZADO)) {
            sentencia.setString(1, usuario.getCodigo());
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }
}
