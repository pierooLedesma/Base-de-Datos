package pe.edu.pucp.transitsoft.estrategias;

import pe.edu.pucp.transitsoft.modelo.Captura;

public class EstrategiaViaRapida implements Estrategia {
    @Override
    public boolean tieneExceso(Captura captura) {
        return captura.getVelocidad() > 80;
    }
}
