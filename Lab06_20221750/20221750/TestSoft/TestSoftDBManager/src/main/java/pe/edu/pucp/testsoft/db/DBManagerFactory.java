package pe.edu.pucp.testsoft.db;

public abstract class DBManagerFactory {
    public abstract DBManager crearDBManager(String host, int puerto,
                                             String esquema, String usuario,
                                             String password);
}
