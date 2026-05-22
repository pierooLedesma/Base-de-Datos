package pe.edu.pucp.testsoft.dao.examen;

import pe.edu.pucp.testsoft.dao.DefaultBaseDAO;
import pe.edu.pucp.testsoft.dao.alumno.AlumnoDAOImpl;
import pe.edu.pucp.testsoft.modelo.Examen;
import pe.edu.pucp.testsoft.modelo.Pregunta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamenDAOImpl extends DefaultBaseDAO<Examen> implements ExamenDAO {
    @Override
    public Integer crear(Examen modelo) {
        return ejecutarComando(conn -> {
            Integer idExamen = this.ejecutarComandoCrear(conn, modelo);
            if (idExamen == null) {
                return null;
            }
            modelo.setId(idExamen);
            this.asociarPreguntas(conn, idExamen, modelo.getPreguntas());
            return idExamen;
        });
    }

    
	@Override
	public List<Examen> leerTodos() {
		return ejecutarComando(conn -> {
			try (PreparedStatement cmd = this.comandoLeerTodos(conn);
				 ResultSet rs = cmd.executeQuery()) {
				List<Examen> modelos = new ArrayList<>();
				while (rs.next()) {
					Examen modelo = this.mapearModelo(rs);
					modelo.setPreguntas(this.leerPreguntasAsociadas(conn, modelo.getId()));
					modelos.add(modelo);
				}
				return modelos;
			}
		});
	}

    @Override
    public Examen leer(Integer id) {
        return ejecutarComando(conn -> {
            try (PreparedStatement cmd = this.comandoLeer(conn, id);
                 ResultSet rs = cmd.executeQuery()) {
                if (!rs.next()) {
                    System.err.println("No se encontro el examen con id: " + id);
                    return null;
                }

                Examen modelo = this.mapearModelo(rs);
                modelo.setPreguntas(this.leerPreguntasAsociadas(conn, modelo.getId()));
                return modelo;
            }
        });
    }

    @Override
    protected PreparedStatement comandoCrear(Connection conn, Examen modelo) throws SQLException {
        String sql = """
                INSERT INTO examen (id_alumno, titulo, fecha_creacion)
                VALUES (?, ?, ?)
                """;
        PreparedStatement cmd = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        cmd.setInt(1, modelo.getAlumno().getId());
        cmd.setString(2, modelo.getTitulo());
        cmd.setDate(3, new Date(modelo.getFechaCreacion().getTime()));
        return cmd;
    }


    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        String sql = """
                SELECT * FROM examen WHERE id = ?
                """;
        PreparedStatement cmd = conn.prepareStatement(sql);
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        String sql = """
                SELECT * FROM examen
                """;
        return conn.prepareStatement(sql);
    }

    @Override
    protected Examen mapearModelo(ResultSet rs) throws SQLException {
        Examen modelo = new Examen();
        modelo.setId(rs.getInt(1));
        modelo.setAlumno(new AlumnoDAOImpl().leer(rs.getInt(2)));
        modelo.setTitulo(rs.getString(3));
        modelo.setFechaCreacion(rs.getDate(4));
        return modelo;
    }

    // ======== OPERACIONES NO SOPORTADAS =======

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Examen modelo) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // ======== OPERACIONES TABLA INTERMEDIA =======

    protected PreparedStatement comandoAsociarPregunta(Connection conn,
                                                       Integer idExamen,
                                                       Pregunta pregunta,
                                                       int orden
    ) throws SQLException {
        String sql = """
			INSERT INTO examen_pregunta (id_examen, id_pregunta, orden)
			VALUES (?, ?, ?)
			""";
        PreparedStatement cmd = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        cmd.setInt(1, idExamen);
        cmd.setInt(2, pregunta.getId());
        cmd.setInt(3, orden);
        return cmd;
    }

    protected PreparedStatement comandoLeerPreguntasAsociadas(Connection conn,
                                                              Integer idExamen) throws SQLException {
        String sql = """
			SELECT P.id, P.enunciado, E.orden
			FROM examen_pregunta E, pregunta P
			WHERE E.id_examen = ? AND P.id = E.id_pregunta
			ORDER BY E.orden
			""";
        PreparedStatement cmd = conn.prepareStatement(sql);
        cmd.setInt(1, idExamen);
        return cmd;
    }

    protected void asociarPreguntas(Connection conn, Integer idExamen, List<Pregunta> preguntas) throws SQLException {
        if (preguntas == null || preguntas.isEmpty()) return;

        for (int i = 0; i < preguntas.size(); i++) {
            Pregunta pregunta = preguntas.get(i);
            try (PreparedStatement cmd = this.comandoAsociarPregunta(
                    conn,
                    idExamen,
                    pregunta,
                    i + 1 // Consultado con JP
            )) {
                if (cmd.executeUpdate() == 0) {
                    throw new SQLException("No se pudo asociar una pregunta a un examen");
                }
            }
        }
    }

    protected List<Pregunta> leerPreguntasAsociadas(Connection conn,
                                               Integer idExamen) throws SQLException {
        try (PreparedStatement cmd = this.comandoLeerPreguntasAsociadas(conn, idExamen);
             ResultSet rs = cmd.executeQuery()) {
            List<Pregunta> preguntas = new ArrayList<>();
            while (rs.next()) {
                Pregunta pregunta = new Pregunta();
                pregunta.setId(rs.getInt(1));
                pregunta.setEnunciado(rs.getString(2));
                preguntas.add(pregunta);
            }
            return preguntas;
        }
    }
}
