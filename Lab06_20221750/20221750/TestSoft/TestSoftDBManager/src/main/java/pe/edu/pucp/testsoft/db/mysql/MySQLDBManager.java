package pe.edu.pucp.testsoft.db.mysql;

import pe.edu.pucp.testsoft.db.utils.TipoDB;
import pe.edu.pucp.testsoft.db.DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDBManager extends DBManager {
    private static MySQLDBManager instancia;
    
    protected MySQLDBManager() {}
    
    protected MySQLDBManager(String host, int puerto, String esquema, 
                             String usuario, String password) {
        super(host, puerto, esquema, usuario, password, TipoDB.MySQL);
    }
    
    static synchronized MySQLDBManager getInstance(String host, int puerto, 
                                                   String esquema, 
                                                   String usuario, 
                                                   String password) {
        if (instancia == null) {
            instancia = new MySQLDBManager(host, puerto, esquema, usuario, 
                                           password);
        }
        return instancia;
    }
    
    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String cadenaConexion = cadenaConexion();
            return DriverManager.getConnection(cadenaConexion, usuario, password);
        }
        catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
            throw e;
        }
    }
}
