package pe.edu.pucp.transitsoft.estrategias;

import pe.edu.pucp.transitsoft.modelo.Captura;

public interface Estrategia {
    boolean tieneExceso(Captura captura);
}
