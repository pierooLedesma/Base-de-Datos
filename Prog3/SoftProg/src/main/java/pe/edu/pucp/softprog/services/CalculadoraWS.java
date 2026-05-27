package pe.edu.pucp.softprog.services;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(
        serviceName = "CalculadoraWS",
        targetNamespace = "http://services.softprog.pucp.edu.pe/"
)
public class CalculadoraWS {

    @WebMethod(operationName = "sumar")
    public double sumar(
            @WebParam(name = "numero1") double numero1,
            @WebParam(name = "numero2") double numero2) {
        return numero1 + numero2;
    }

    @WebMethod(operationName = "restar")
    public double restar(
            @WebParam(name = "numero1") double numero1,
            @WebParam(name = "numero2") double numero2) {
        return numero1 - numero2;
    }

    @WebMethod(operationName = "multiplicar")
    public double multiplicar(
            @WebParam(name = "numero1") double numero1,
            @WebParam(name = "numero2") double numero2) {
        return numero1 * numero2;
    }

    @WebMethod(operationName = "dividir")
    public double dividir(
            @WebParam(name = "numero1") double numero1,
            @WebParam(name = "numero2") double numero2) {

        if (numero2 == 0) {
            throw new ArithmeticException(
                    "No es posible dividir entre cero.");
        }

        return numero1 / numero2;
    }
}
