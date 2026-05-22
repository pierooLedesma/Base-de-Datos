package pe.edu.pucp.testsoft.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Examen extends Registro {
    private Alumno alumno;
    private String titulo;
    private Date fechaCreacion = new Date();
    // Dado que el orden es implícito del orden de esta lista, no se ha creado
    // un modelo aparte. En caso hubiera más datos propios de la relación Examen <=> Pregunta (como puntaje específico del examen),
    // y que esté en la tabla examen_pregunta, sí se tendría que crear un modelo aparte.
    private List<Pregunta> preguntas;

    public Examen() {
        this.preguntas = new ArrayList<>();
    }

    Examen(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void addPregunta(Pregunta pregunta) {
        this.preguntas.add(pregunta);
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    public void removePregunta(Pregunta pregunta) {
        this.preguntas.remove(pregunta);
    }

    public List<Pregunta> getPreguntas() {
        return new ArrayList<>(preguntas);
    }
}
