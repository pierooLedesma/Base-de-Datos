package pe.edu.pucp.testsoft.bo.preguntas;

import pe.edu.pucp.testsoft.bo.Gestionable;
import pe.edu.pucp.testsoft.modelo.Alumno;
import pe.edu.pucp.testsoft.modelo.Pregunta;

import java.util.List;

public interface PreguntaBO extends Gestionable<Pregunta> {
    List<Pregunta> seleccionarPreguntasAleatorias();
}
