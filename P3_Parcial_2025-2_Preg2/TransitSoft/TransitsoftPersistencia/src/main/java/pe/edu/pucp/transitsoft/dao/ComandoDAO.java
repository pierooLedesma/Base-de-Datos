package pe.edu.pucp.transitsoft.dao;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ComandoDAO<R> {
    R ejecutar(Connection conn) throws SQLException;
}
