package pe.edu.pucp.testsoft.bo.preguntas;

import pe.edu.pucp.testsoft.bo.BaseBO;
import pe.edu.pucp.testsoft.dao.pregunta.PreguntaDAO;
import pe.edu.pucp.testsoft.dao.pregunta.PreguntaDAOImpl;
import pe.edu.pucp.testsoft.modelo.Alumno;
import pe.edu.pucp.testsoft.modelo.Estado;
import pe.edu.pucp.testsoft.modelo.Pregunta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PreguntaBOImpl extends BaseBO implements PreguntaBO {
    private final PreguntaDAO preguntaDAO = new PreguntaDAOImpl();

    @Override
    public List<Pregunta> listar() {
        return preguntaDAO.leerTodos();
    }

    @Override
    public Pregunta obtener(int id) {
        validarIdPositivo(id, "id");
        return this.preguntaDAO.leer(id);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id");
        if (!this.preguntaDAO.eliminar(id)) {
            throw new IllegalStateException("No se pudo eliminar la pregunta con id: " + id);
        }
    }

    @Override
    public void guardar(Pregunta modelo, Estado estado) {
        validarPregunta(modelo);
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            int id = this.preguntaDAO.crear(modelo);
            if (id <= 0) {
                throw new IllegalStateException("No se pudo crear la pregunta");
            }
            modelo.setId(id);
        }
        else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getId(), "id de la pregunta");
            if (!this.preguntaDAO.actualizar(modelo)) {
                throw new IllegalStateException("No se pudo actualizar la pregunta con id: " + modelo.getId());
            }
        }
        else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    private void validarPregunta(Pregunta modelo) {
        Objects.requireNonNull(modelo, "La pregunta es obligatoria");
        if (modelo.getEnunciado() == null || modelo.getEnunciado().isBlank()) {
            throw new IllegalArgumentException("El enunciado de la pregunta es obligatoria");
        }
    }

    @Override
    public List<Pregunta> seleccionarPreguntasAleatorias() {
        List<Pregunta> banco = listar();
        Collections.shuffle(banco);
        return new ArrayList<>(banco.subList(0, 10));
    }
}
