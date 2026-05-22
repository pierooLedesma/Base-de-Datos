package pe.edu.pucp.testsoft.dao.pregunta;

import pe.edu.pucp.testsoft.dao.DefaultBaseDAO;
import pe.edu.pucp.testsoft.modelo.Pregunta;

import java.sql.*;

public class PreguntaDAOImpl extends DefaultBaseDAO<Pregunta> implements PreguntaDAO {
    @Override
    protected PreparedStatement comandoCrear(Connection conn, Pregunta modelo) throws SQLException {
        String sql = """
                INSERT INTO pregunta (id, enunciado) VALUES (?, ?)
                """;
        PreparedStatement cmd = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        cmd.setInt(1, modelo.getId());
        cmd.setString(2, modelo.getEnunciado());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Pregunta modelo) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        String sql = """
                SELECT * FROM pregunta
                """;
        return conn.prepareStatement(sql);
    }

    @Override
    protected Pregunta mapearModelo(ResultSet rs) throws SQLException {
        Pregunta modelo = new Pregunta();
        modelo.setId(rs.getInt(1));
        modelo.setEnunciado(rs.getString(2));
        return modelo;
    }
}
