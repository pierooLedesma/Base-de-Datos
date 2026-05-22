package pe.edu.pucp.testsoft.dao.alumno;

import pe.edu.pucp.testsoft.dao.DefaultBaseDAO;
import pe.edu.pucp.testsoft.modelo.Alumno;

import java.sql.*;

public class AlumnoDAOImpl extends DefaultBaseDAO<Alumno> implements AlumnoDAO {
    @Override
    protected PreparedStatement comandoCrear(Connection conn, Alumno modelo) throws SQLException {
        String sql = """
                INSERT INTO alumno (id, codigo, nombre, correo) VALUES (?, ?, ?, ?)
                """;

        PreparedStatement cmd = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        cmd.setInt(1, modelo.getId());
        cmd.setString(2, modelo.getCodigo());
        cmd.setString(3, modelo.getNombre());
        cmd.setString(4, modelo.getCorreo());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Alumno modelo) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        String sql = """
                SELECT * FROM alumno WHERE id = ?
                """;
        PreparedStatement cmd = conn.prepareStatement(sql);
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        String sql = """
                SELECT * FROM alumno;
                """;
        return conn.prepareStatement(sql);
    }

    @Override
    protected Alumno mapearModelo(ResultSet rs) throws SQLException {
        Alumno modelo = new Alumno();
        modelo.setId(rs.getInt(1));
        modelo.setCodigo(rs.getString(2));
        modelo.setNombre(rs.getString(3));
        modelo.setCorreo(rs.getString(4));
        return modelo;
    }
}
