package pe.edu.pucp.testsoft.app;

import pe.edu.pucp.testsoft.bo.alumno.AlumnoBO;
import pe.edu.pucp.testsoft.bo.alumno.AlumnoBOImpl;
import pe.edu.pucp.testsoft.bo.examen.ExamenBO;
import pe.edu.pucp.testsoft.bo.examen.ExamenBOImpl;
import pe.edu.pucp.testsoft.bo.preguntas.PreguntaBO;
import pe.edu.pucp.testsoft.bo.preguntas.PreguntaBOImpl;
import pe.edu.pucp.testsoft.modelo.Alumno;
import pe.edu.pucp.testsoft.modelo.Pregunta;

import java.util.List;

public class CrearExamenes {
    public static void main(String[] args) {
        AlumnoBO alumnoBO = new AlumnoBOImpl();
        ExamenBO examenBO = new ExamenBOImpl();
        PreguntaBO preguntaBO = new PreguntaBOImpl();
        List<Alumno> alumnos = alumnoBO.listar();
        for (Alumno alumno : alumnos) {
            List<Pregunta> preguntasSeleccionadas =
                    preguntaBO.seleccionarPreguntasAleatorias();
            examenBO.crearExamenConPreguntas(
                    alumno,
                    "Examen configurado para " + alumno.getCodigo(),
                    preguntasSeleccionadas
            );
        }
        System.out.println("Se generaron los exámenes.");
    }
}