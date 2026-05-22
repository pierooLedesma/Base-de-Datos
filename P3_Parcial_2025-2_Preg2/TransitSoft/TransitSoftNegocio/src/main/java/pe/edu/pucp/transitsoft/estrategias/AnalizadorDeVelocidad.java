package pe.edu.pucp.transitsoft.estrategias;

import pe.edu.pucp.transitsoft.modelo.Captura;

public class AnalizadorDeVelocidad {
    private final Estrategia estrategia;

    public AnalizadorDeVelocidad(Estrategia estrategia) {
        this.estrategia = estrategia;
    }

    public boolean evaluarExceso(Captura captura) {
        return estrategia.tieneExceso(captura);
    }
}
