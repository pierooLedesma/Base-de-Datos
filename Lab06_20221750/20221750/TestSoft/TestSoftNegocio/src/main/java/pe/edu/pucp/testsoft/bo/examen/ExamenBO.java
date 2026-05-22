package pe.edu.pucp.testsoft.bo.examen;

import pe.edu.pucp.testsoft.bo.Gestionable;
import pe.edu.pucp.testsoft.modelo.Alumno;
import pe.edu.pucp.testsoft.modelo.Examen;
import pe.edu.pucp.testsoft.modelo.Pregunta;

import java.util.List;

public interface ExamenBO extends Gestionable<Examen> {
    void crearExamenConPreguntas(Alumno alumno, String titulo, List<Pregunta> preguntas);
}
