package pe.edu.pucp.testsoft.bo.alumno;

import pe.edu.pucp.testsoft.bo.BaseBO;
import pe.edu.pucp.testsoft.dao.alumno.AlumnoDAO;
import pe.edu.pucp.testsoft.dao.alumno.AlumnoDAOImpl;
import pe.edu.pucp.testsoft.modelo.Alumno;
import pe.edu.pucp.testsoft.modelo.Estado;

import java.util.List;
import java.util.Objects;

public class AlumnoBOImpl extends BaseBO implements AlumnoBO {
    private final AlumnoDAO alumnoDAO = new AlumnoDAOImpl();

    @Override
    public List<Alumno> listar() {
        return alumnoDAO.leerTodos();
    }

    @Override
    public Alumno obtener(int id) {
        validarIdPositivo(id, "id");
        return this.alumnoDAO.leer(id);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id");
        if (!this.alumnoDAO.eliminar(id)) {
            throw new IllegalStateException("No se pudo eliminar el alumno con id: " + id);
        }
    }

    @Override
    public void guardar(Alumno modelo, Estado estado) {
        validarAlumno(modelo);
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            int id = this.alumnoDAO.crear(modelo);
            if (id <= 0) {
                throw new IllegalStateException("No se pudo crear el alumno");
            }
            modelo.setId(id);
        }
        else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getId(), "id del alumno");
            if (!this.alumnoDAO.actualizar(modelo)) {
                throw new IllegalStateException("No se pudo actualizar el alumno con id: " + modelo.getId());
            }
        }
        else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    private void validarAlumno(Alumno modelo) {
        Objects.requireNonNull(modelo, "El alumno es obligatorio");
        if (modelo.getCodigo() == null || modelo.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El codigo del alumno es obligatorio");
        }
        if (modelo.getCorreo() == null) {
            throw new IllegalArgumentException("El correo del alumno es obligatorio");
        }
        if (modelo.getNombre() == null) {
            throw new IllegalArgumentException("El nombre del alumno es obligatorio");
        }
    }
}
