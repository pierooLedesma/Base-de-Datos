package pe.edu.pucp.transitsoft.app;

import java.util.List;

import pe.edu.pucp.transitsoft.bo.CapturaBO;
import pe.edu.pucp.transitsoft.bo.CapturaBOImpl;
import pe.edu.pucp.transitsoft.bo.InfraccionBO;
import pe.edu.pucp.transitsoft.bo.InfraccionBOImpl;
import pe.edu.pucp.transitsoft.dto.Infraccion;
import pe.edu.pucp.transitsoft.modelo.Captura;

public class Main {

    public static void main(String[] args) {

        CapturaBO capturaBO = new CapturaBOImpl();
        InfraccionBO infraccionBO = new InfraccionBOImpl();

        // 1. Obtener capturas con exceso
        List<Captura> capturasConExceso =
                capturaBO.obtenerCapturasConExcesoDeVelocidad();

        System.out.println("CAPTURAS CON EXCESO:");
        System.out.println();

        for (Captura captura : capturasConExceso) {

            System.out.println("ID: " + captura.getId());
            System.out.println("Placa: " + captura.getPlaca());
            System.out.println("Velocidad: " + captura.getVelocidad());
            System.out.println("Camara: " +
                    captura.getCamara().getCodigoSerie());

            System.out.println("------------------------");
        }

        // 2. Crear infracciones
        List<Infraccion> infracciones =
                infraccionBO.crearInfracciones(capturasConExceso);

        System.out.println();
        System.out.println("INFRACCIONES GENERADAS:");
        System.out.println();

        for (Infraccion infraccion : infracciones) {
            System.out.println(infraccion);
        }

        // 3. Actualizar estado de capturas
        for (Captura captura : capturasConExceso) {
            capturaBO.actualizar(captura);
        }

        System.out.println();
        System.out.println("Capturas actualizadas a PROCESADO.");
    }
}
