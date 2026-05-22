package pe.edu.pucp.transitsoft.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pe.edu.pucp.transitsoft.db.DBFactoryProvider;
import pe.edu.pucp.transitsoft.db.DBManager;

public abstract class BaseDAO<M, I> implements Persistible<M, I> {

    @Override
    public boolean actualizar(M modelo) {
        return ejecutarComando(conn -> ejecutarComandoActualizar(conn, modelo));
    }

    @Override
    public List<M> leerTodos() {
        return ejecutarComando(conn -> {
            try (PreparedStatement cmd = this.comandoLeerTodos(conn);
                 ResultSet rs = cmd.executeQuery()) {
                List<M> modelos = new ArrayList<>();
                while (rs.next()) {
                    modelos.add(this.mapearModelo(rs));
                }
                return modelos;
            }
        });
    }


    protected <R> R ejecutarComando(ComandoDAO<R> command) {
        Connection txConnection = TransactionsManager.obtenerConexionActual();
        if (txConnection != null) {
            return ejecutarComandoConTransaccion(command, txConnection);
        }

        return ejecutarComandoSinTransaccion(command);
    }

    protected <R> R ejecutarComandoConTransaccion(ComandoDAO<R> command,
                                                  Connection txConnection) {
        try {
            return command.ejecutar(txConnection);
        }
        catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected <R> R ejecutarComandoSinTransaccion(ComandoDAO<R> command) {
        DBManager dbManager = DBFactoryProvider.getManager();
        try (Connection conn = dbManager.getConnection()) {
            return command.ejecutar(conn);
        }
        catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    protected boolean ejecutarComandoActualizar(Connection conn, M modelo) {
        try (PreparedStatement cmd = this.comandoActualizar(conn, modelo)) {
            return cmd.executeUpdate() > 0;
        }
        catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    protected abstract PreparedStatement comandoActualizar(Connection conn,
                                                           M modelo) throws SQLException;

    protected abstract PreparedStatement comandoLeerTodos(Connection conn) throws SQLException;

    protected abstract M mapearModelo(ResultSet rs) throws SQLException;


    protected void setEnteroNullable(PreparedStatement cmd, int index,
                                     Integer value) throws SQLException {
        if (value == null) {
            cmd.setNull(index, java.sql.Types.INTEGER);
        }
        else {
            cmd.setInt(index, value);
        }
    }

    protected Integer leerEnteroNullable(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
}
