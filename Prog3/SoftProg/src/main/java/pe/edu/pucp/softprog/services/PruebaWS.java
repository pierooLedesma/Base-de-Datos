package pe.edu.pucp.softprog.services;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(
        serviceName = "PruebaWS",
        targetNamespace = "http://services.softprog.pucp.edu.pe/"
)
public class PruebaWS {
    @WebMethod(operationName = "saludar")
    public String saludar(String nombre) {
        return "Hola " + nombre;
    }
}
