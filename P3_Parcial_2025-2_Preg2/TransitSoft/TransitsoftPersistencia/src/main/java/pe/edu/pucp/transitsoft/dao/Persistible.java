package pe.edu.pucp.transitsoft.dao;

import java.util.List;

public interface Persistible<M, I> {
    boolean actualizar(M modelo);

    List<M> leerTodos();
}
