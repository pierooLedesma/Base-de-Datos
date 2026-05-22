package pe.edu.pucp.transitsoft.daoimpl.captura;

import pe.edu.pucp.transitsoft.dao.BaseDAO;
import pe.edu.pucp.transitsoft.dao.*;
import pe.edu.pucp.transitsoft.modelo.*;
import pe.edu.pucp.transitsoft.dao.captura.CapturaDAO;

import java.sql.*;

public class CapturaDAOImpl extends BaseDAO<Captura, Integer> implements CapturaDAO {

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Captura modelo) throws SQLException {
        String sql = "{call modificarEstadoCaptura(?, ?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        cmd.setString("p_estado", modelo.getEstado().name());
        cmd.setInt("p_id", modelo.getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        String sql = "{call listarCapturas()}";
        return conn.prepareCall(sql);
    }

    @Override
    protected Captura mapearModelo(ResultSet rs) throws SQLException {
        Captura modelo = new Captura();
        modelo.setId(rs.getInt("id"));
        modelo.setPlaca(rs.getString("placa"));
        modelo.setVelocidad(rs.getDouble("velocidad"));
        modelo.setFechaCaptura(rs.getDate("fecha_captura"));
        modelo.setEstado(EstadoCaptura.valueOf(rs.getString("estado")));

        Camara camara = new Camara();
        camara.setId(rs.getInt("id_camara"));
        camara.setModelo(rs.getString("camara_modelo"));
        camara.setCodigoSerie(rs.getString("camara_codigo_serie"));
        camara.setLatitud(rs.getLong("camara_latitud"));
        camara.setLongitud(rs.getLong("camara_longitud"));

        modelo.setCamara(camara);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(rs.getInt("id_vehiculo"));
        vehiculo.setPlaca(rs.getString("vehiculo_placa"));
        vehiculo.setMarca(rs.getString("vehiculo_marca"));
        vehiculo.setModelo(rs.getString("vehiculo_modelo"));
        vehiculo.setAnho(rs.getInt("vehiculo_anho"));

        Propietario propietario = new Propietario();
        propietario.setId(rs.getInt("id_propietario"));
        propietario.setDni(rs.getString("propietario_dni"));
        propietario.setNombres(rs.getString("propietario_nombres"));
        propietario.setApellidos(rs.getString("propietario_apellidos"));
        propietario.setDireccion(rs.getString("propietario_direccion"));

        vehiculo.setPropietario(propietario);

        modelo.setVehiculo(vehiculo);

        return modelo;
    }
}
