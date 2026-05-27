using ServiciosWeb;

namespace SoftProgCliente {
    public class Program
    {
        public static void Main(string[] arg)
        {
            CalculadoraWSClient ws = new CalculadoraWSClient();

            double suma = ws.sumar(10, 5);
            double resta = ws.restar(10, 5);
            double multiplicacion = ws.multiplicar(10, 5);
            double division = ws.dividir(10, 5);

            Console.WriteLine("Suma: " + suma);
            Console.WriteLine("Resta: " + resta);
            Console.WriteLine("Multiplicación: " + multiplicacion);
            Console.WriteLine("División: " + division);
        }
    }
}