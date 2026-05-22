package pe.edu.pucp.transitsoft.bo;

import java.util.List;
import pe.edu.pucp.transitsoft.dto.Infraccion;
import pe.edu.pucp.transitsoft.modelo.Captura;

public interface InfraccionBO {
    List<Infraccion> crearInfracciones(List<Captura> capturasConExceso);
}
