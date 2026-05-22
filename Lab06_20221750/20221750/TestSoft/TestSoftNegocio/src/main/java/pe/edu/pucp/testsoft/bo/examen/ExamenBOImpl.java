package pe.edu.pucp.testsoft.bo.examen;

import pe.edu.pucp.testsoft.bo.BaseBO;
import pe.edu.pucp.testsoft.dao.TransactionsManager;
import pe.edu.pucp.testsoft.dao.examen.ExamenDAO;
import pe.edu.pucp.testsoft.dao.examen.ExamenDAOImpl;
import pe.edu.pucp.testsoft.modelo.Alumno;
import pe.edu.pucp.testsoft.modelo.Estado;
import pe.edu.pucp.testsoft.modelo.Examen;
import pe.edu.pucp.testsoft.modelo.Pregunta;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExamenBOImpl extends BaseBO implements ExamenBO {
    private final ExamenDAO examenDAO = new ExamenDAOImpl();

    @Override
    public List<Examen> listar() {
        return examenDAO.leerTodos();
    }

    @Override
    public Examen obtener(int id) {
        validarIdPositivo(id, "id");
        return this.examenDAO.leer(id);
    }

    @Override
    public void eliminar(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void guardar(Examen modelo, Estado estado) {
        validarExamen(modelo);
        validarEstado(estado);

        TransactionsManager.iniciarTransaccion();
        try {
            if (estado == Estado.Nuevo) {
                int id = this.examenDAO.crear(modelo);
                if (id <= 0) {
                    throw new IllegalStateException("No se pudo crear el examen");
                }
                modelo.setId(id);
            } else if (estado == Estado.Modificado) {
                validarIdPositivo(modelo.getId(), "id del examen");
                if (!this.examenDAO.actualizar(modelo)) {
                    throw new IllegalStateException("No se pudo actualizar el examen con id: " + modelo.getId());
                }
            } else {
                throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
            }
            TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }

    @Override public void crearExamenConPreguntas(Alumno alumno, String titulo, List<Pregunta> preguntas) {
        Examen examen = new Examen();
        examen.setTitulo(titulo);
        examen.setPreguntas(preguntas);
        examen.setFechaCreacion(new Date());
        examen.setAlumno(alumno);
        guardar(examen, Estado.Nuevo);
    }

    private void validarExamen(Examen modelo) {
        Objects.requireNonNull(modelo, "El examen es obligatorio");
        if (modelo.getTitulo() == null || modelo.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El titulo del examen es obligatorio");
        }
        if (modelo.getAlumno() == null) {
            throw new IllegalArgumentException("El alumno del examen es obligatorio");
        }
        if (modelo.getFechaCreacion() == null) {
            throw new IllegalArgumentException("La fecha de creación es obligatoria");
        }
    }
}
