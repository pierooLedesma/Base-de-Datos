package pe.edu.pucp.testsoft.db.mysql;

import pe.edu.pucp.testsoft.db.DBManager;
import pe.edu.pucp.testsoft.db.DBManagerFactory;

public class MySQLDBManagerFactory extends DBManagerFactory {
    @Override
    public DBManager crearDBManager(String host, int puerto, String esquema,
                                    String usuario, String password) {
        return MySQLDBManager.getInstance(host, puerto, esquema, usuario, 
                                          password);
    }
}
