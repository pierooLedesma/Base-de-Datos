package pe.edu.pucp.transitsoft.bo;

import java.util.ArrayList;
import java.util.List;

import pe.edu.pucp.transitsoft.daoimpl.captura.CapturaDAOImpl;
import pe.edu.pucp.transitsoft.estrategias.AnalizadorDeVelocidad;
import pe.edu.pucp.transitsoft.estrategias.Estrategia;
import pe.edu.pucp.transitsoft.estrategias.EstrategiaViaRapida;
import pe.edu.pucp.transitsoft.estrategias.EstrategiaViaRegular;
import pe.edu.pucp.transitsoft.modelo.Captura;
import pe.edu.pucp.transitsoft.modelo.EstadoCaptura;

public class CapturaBOImpl implements CapturaBO {
    /** Declarar el atributo CapturaDAO */
    private final CapturaDAOImpl capturaDao;
    private final EstrategiaViaRapida estrategiaViaRapida;
    private final EstrategiaViaRegular estrategiaViaRegular;
    private static final List<String> CAMARAS_VIA_RAPIDA =
            List.of("CAM-LIM-002");

    public CapturaBOImpl() {
        /** Instanciar al CapturaDAO */
        this.capturaDao = new CapturaDAOImpl();
        this.estrategiaViaRapida = new EstrategiaViaRapida();
        this.estrategiaViaRegular = new EstrategiaViaRegular();
    }

    @Override
    public List<Captura> obtenerCapturasConExcesoDeVelocidad() {
        /**  Implementar la obtención de capturas con exceso de velocidad */
        /**  usando el patrón estrategia. **/

        List<Captura> capturas = this.capturaDao.leerTodos();
        List<Captura> capturasConExcesoDeVelocidad = new ArrayList<>();

        for (Captura una_captura : capturas) {
            AnalizadorDeVelocidad el_analizador = analizador(una_captura);

            if (el_analizador.evaluarExceso(una_captura)) {
                capturasConExcesoDeVelocidad.add(una_captura);
            }
        }

        return capturasConExcesoDeVelocidad;
//        return capturaDao.leerTodos().
//                stream().filter(
//                        una_captura -> analizador(una_captura).evaluarExceso(una_captura))
//                .toList();
    }

    @Override
    public void actualizar(Captura captura) {
        // TODO: Implementar la actualizacion del estado de una captura a procesado
        captura.setEstado(EstadoCaptura.PROCESADO);
        capturaDao.actualizar(captura);
    }

    protected AnalizadorDeVelocidad analizador(Captura captura) {
        String codigo = captura.getCamara().getCodigoSerie();
        Estrategia estrategia = CAMARAS_VIA_RAPIDA.contains(codigo)
                ? estrategiaViaRapida
                : estrategiaViaRegular;

        return new AnalizadorDeVelocidad(estrategia);
    }
}
