package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.servicios.ConversionService;
import ec.edu.monster.vista.ClienteConsolaREST;

import java.util.Scanner;

/**
 * Controlador para manejar la lógica de la aplicación cliente
 * @author Jonathan
 */
public class ClienteController {
    
    private final ConversionService conversionService;
    private final Scanner scanner;
    private final ClienteConsolaREST vista;
    private final Autenticador autenticador;

    public ClienteController() {
        this.conversionService = new ConversionService();
        this.scanner = ec.edu.monster.utilidades.Entrada.SCANNER;
        this.vista = new ClienteConsolaREST();
        this.autenticador = new Autenticador();
    }

    public void ejecutar() {
        // Primero autenticar al usuario
        if (!autenticador.autenticar()) {
            System.out.println("Acceso denegado. La aplicación se cerrará.");
            return;
        }
        
        vista.mostrarBienvenida();
        
        boolean continuar = true;
        while (continuar) {
            try {
                int opcion = vista.mostrarMenu();
                
                switch (opcion) {
                    case 1 -> manejarTemperatura();
                    case 2 -> manejarLongitud();
                    case 3 -> manejarPeso();
                    case 0 -> continuar = false;
                    default -> vista.mostrarError("Opción inválida. Intente nuevamente.");
                }
                
                if (continuar) {
                    vista.mostrarPausa();
                }
                
            } catch (java.util.NoSuchElementException e) {
                continuar = false; // entrada cerrada
            } catch (Exception e) {
                vista.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
        
        vista.mostrarDespedida();
        conversionService.cerrar();
        autenticador.cerrar();
        scanner.close();
    }

    private void manejarTemperatura() {
        vista.mostrarSubmenuTemperatura();
        
        try {
            String entrada = scanner.nextLine().trim();
            int opcion = Integer.parseInt(entrada);
            
            switch (opcion) {
                case 1 -> {
                    double celsius = vista.solicitarValor("Celsius");
                    ConversionResponse response = conversionService.celsiusAFahrenheit(celsius);
                    vista.mostrarResultado(response);
                }
                case 2 -> {
                    double fahrenheit = vista.solicitarValor("Fahrenheit");
                    ConversionResponse response = conversionService.fahrenheitACelsius(fahrenheit);
                    vista.mostrarResultado(response);
                }
                case 3 -> {
                    double celsius = vista.solicitarValor("Celsius");
                    ConversionResponse response = conversionService.celsiusAKelvin(celsius);
                    vista.mostrarResultado(response);
                }
                case 4 -> {
                    double kelvin = vista.solicitarValor("Kelvin");
                    ConversionResponse response = conversionService.kelvinACelsius(kelvin);
                    vista.mostrarResultado(response);
                }
                case 5 -> {
                    double fahrenheit = vista.solicitarValor("Fahrenheit");
                    ConversionResponse response = conversionService.fahrenheitAKelvin(fahrenheit);
                    vista.mostrarResultado(response);
                }
                case 6 -> {
                    double kelvin = vista.solicitarValor("Kelvin");
                    ConversionResponse response = conversionService.kelvinAFahrenheit(kelvin);
                    vista.mostrarResultado(response);
                }
                default -> vista.mostrarError("Opción inválida.");
            }
        } catch (NumberFormatException e) {
            vista.mostrarError("Debe ingresar un número válido.");
        }
    }

    private void manejarLongitud() {
        vista.mostrarSubmenuLongitud();
        
        try {
            String entrada = scanner.nextLine().trim();
            int opcion = Integer.parseInt(entrada);
            
            switch (opcion) {
            case 1 -> {
                double metros = vista.solicitarValor("Metros");
                ConversionResponse response = conversionService.metrosAPies(metros);
                vista.mostrarResultado(response);
            }
            case 2 -> {
                double pies = vista.solicitarValor("Pies");
                ConversionResponse response = conversionService.piesAMetros(pies);
                vista.mostrarResultado(response);
            }
            case 3 -> {
                double metros = vista.solicitarValor("Metros");
                ConversionResponse response = conversionService.metrosAPulgadas(metros);
                vista.mostrarResultado(response);
            }
            case 4 -> {
                double pulgadas = vista.solicitarValor("Pulgadas");
                ConversionResponse response = conversionService.pulgadasAMetros(pulgadas);
                vista.mostrarResultado(response);
            }
            case 5 -> {
                double kilometros = vista.solicitarValor("Kilómetros");
                ConversionResponse response = conversionService.kilometrosAMillas(kilometros);
                vista.mostrarResultado(response);
            }
            case 6 -> {
                double millas = vista.solicitarValor("Millas");
                ConversionResponse response = conversionService.millasAKilometros(millas);
                vista.mostrarResultado(response);
            }
            default -> vista.mostrarError("Opción inválida.");
            }
        } catch (NumberFormatException e) {
            vista.mostrarError("Debe ingresar un número válido.");
        }
    }

    private void manejarPeso() {
        vista.mostrarSubmenuPeso();
        
        try {
            String entrada = scanner.nextLine().trim();
            int opcion = Integer.parseInt(entrada);
            
            switch (opcion) {
            case 1 -> {
                double kilogramos = vista.solicitarValor("Kilogramos");
                ConversionResponse response = conversionService.kilogramosALibras(kilogramos);
                vista.mostrarResultado(response);
            }
            case 2 -> {
                double libras = vista.solicitarValor("Libras");
                ConversionResponse response = conversionService.librasAKilogramos(libras);
                vista.mostrarResultado(response);
            }
            case 3 -> {
                double gramos = vista.solicitarValor("Gramos");
                ConversionResponse response = conversionService.gramosAOnzas(gramos);
                vista.mostrarResultado(response);
            }
            case 4 -> {
                double onzas = vista.solicitarValor("Onzas");
                ConversionResponse response = conversionService.onzasAGramos(onzas);
                vista.mostrarResultado(response);
            }
            default -> vista.mostrarError("Opción inválida.");
            }
        } catch (NumberFormatException e) {
            vista.mostrarError("Debe ingresar un número válido.");
        }
    }
}