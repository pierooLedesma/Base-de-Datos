package pe.edu.pucp.transitsoft.estrategias;

import pe.edu.pucp.transitsoft.modelo.Captura;

public class EstrategiaViaRegular implements Estrategia {
    @Override
    public boolean tieneExceso(Captura captura) {
        return captura.getVelocidad() > 50;
    }
}
