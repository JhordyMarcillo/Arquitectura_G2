package ec.edu.monster.view;

import ec.edu.monster.ws.*;
import java.util.List;
import java.util.Scanner;

public class MainView {

  private static LoginService loginService = new LoginService();
  private static CompraServicio compraServicio = new CompraServicio();
  private static TelefonoServicio telefonoServicio = new TelefonoServicio();

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    // Login
    System.out.println("Ingrese su nombre de usuario:");
    String username = scanner.nextLine();
    System.out.println("Ingrese su contraseña:");
    String password = scanner.nextLine();

    if (loginService.auth(username, password)) {
      System.out.println("Login exitoso!");
      mainMenu(scanner);
    } else {
      System.out.println("Login fallido!");
    }
  }

  private static void mainMenu(Scanner scanner) {
    while (true) {
      System.out.println("Seleccione una opción:");
      System.out.println("1. Vender");
      System.out.println("2. Consultar tabla de amortizaciones");
      System.out.println("3. Gestionar teléfonos");
      System.out.println("4. Obtener facturas");
      System.out.println("5. Salir");

      int choice = scanner.nextInt();
      scanner.nextLine(); // consume newline

      switch (choice) {
        case 1:
          venderMenu(scanner);
          break;
        case 2:
          consultarTablaAmortizacion(scanner);
          break;
        case 3:
          gestionarTelefonos(scanner);
          break;
        case 4:
          obtenerFacturas(scanner);
          break;
        case 5:
          System.out.println("Saliendo...");
          return;
        default:
          System.out.println("Opción no válida.");
      }
    }
  }

  private static void venderMenu(Scanner scanner) {
    while (true) {
      System.out.println("Seleccione una opción:");
      System.out.println("1. Comprar Efectivo");
      System.out.println("2. Comprar Crédito");
      System.out.println("3. Regresar");

      int choice = scanner.nextInt();
      scanner.nextLine(); // consume newline

      switch (choice) {
        case 1:
              List<Telefonos> telefonos = telefonoServicio.getAllTelefonos();
                      StringBuilder resultado = new StringBuilder();
          resultado.append("\n===== LISTA DE TELÉFONOS =====\n");
          resultado.append(String.format("%-15s %-20s %-10s %-15s %-10s%n",
              "CodTelefono", "Nombre", "Precio", "Marca", "Disponible"));
          resultado.append("-------------------------------------------------------------\n");

          for (Telefonos telefono : telefonos) {
            resultado.append(String.format("%-15d %-20s %-10.2f %-15s %-10d%n",
                telefono.getCodTelefono(), telefono.getNombre(), telefono.getPrecio(), telefono.getMarca(),
                telefono.getDisponible()));
          }

          System.out.println(resultado.toString());
          System.out.println("Ingrese el código del teléfono:");
          int codTelefono = scanner.nextInt();
          scanner.nextLine(); // consume newline
          System.out.println("Ingrese la cédula:");
          String cedula = scanner.nextLine();
          String resultEfectivo = compraServicio.comprarEfectivo(codTelefono, cedula);
          System.out.println(resultEfectivo);
          break;
        case 2:
             List<Telefonos> telefonos2 = telefonoServicio.getAllTelefonos();
                      StringBuilder resultado2 = new StringBuilder();
          resultado2.append("\n===== LISTA DE TELÉFONOS =====\n");
          resultado2.append(String.format("%-15s %-20s %-10s %-15s %-10s%n",
              "CodTelefono", "Nombre", "Precio", "Marca", "Disponible"));
          resultado2.append("-------------------------------------------------------------\n");

          for (Telefonos telefono : telefonos2) {
            resultado2.append(String.format("%-15d %-20s %-10.2f %-15s %-10d%n",
                telefono.getCodTelefono(), telefono.getNombre(), telefono.getPrecio(), telefono.getMarca(),
                telefono.getDisponible()));
          }
           System.out.println(resultado2.toString());
          System.out.println("Ingrese el código del teléfono:");
          codTelefono = scanner.nextInt();
          scanner.nextLine(); // consume newline
          System.out.println("Ingrese la cédula:");
          cedula = scanner.nextLine();
          System.out.println("Ingrese el plazo en meses:");
          int plazoMeses = scanner.nextInt();
          scanner.nextLine(); // consume newline
          String resultCredito = compraServicio.comprarCredito(codTelefono, cedula, plazoMeses);
          System.out.println(resultCredito);
          break;
        case 3:
          return;
        default:
          System.out.println("Opción no válida.");
      }
    }
  }

  private static void consultarTablaAmortizacion(Scanner scanner) {
    System.out.println("Ingrese la cédula:");
    String cedula = scanner.nextLine();
    List<Tabla> tabla = compraServicio.consultarTablaAmortizacion(cedula);

    if (tabla == null || tabla.isEmpty()) {
      System.out.println("No se encontraron datos de amortización para la cédula: " + cedula);
      return;
    }

    StringBuilder resultado = new StringBuilder();
    resultado.append("\n===== TABLA DE AMORTIZACIÓN =====\n");
    resultado.append(String.format("%-15s %-10s %-15s %-15s %-15s %-15s%n",
        "CodCredito", "Cuota", "ValorCuota", "InteresPagado", "CapitalPagado", "Saldo"));
    resultado.append("------------------------------------------------------------------------\n");

    for (Tabla t : tabla) {
      resultado.append(String.format("%-15d %-10d %-15.2f %-15.2f %-15.2f %-15.2f%n",
          t.getCodCredito(), t.getCuota(), t.getValorCuota(), t.getInteresPagado(), t.getCapitalPagado(),
          t.getSaldo()));
    }

    System.out.println(resultado.toString());

  }

  private static void gestionarTelefonos(Scanner scanner) {
    while (true) {
      System.out.println("Seleccione una opción:");
      System.out.println("1. Mostrar teléfonos");
      System.out.println("2. Crear teléfono");
      System.out.println("3. Actualizar teléfono");
      System.out.println("4. Regresar");

      int choice = scanner.nextInt();
      scanner.nextLine(); // consume newline

      switch (choice) {
        case 1:
          List<Telefonos> telefonos = telefonoServicio.getAllTelefonos();

          if (telefonos == null || telefonos.isEmpty()) {
            System.out.println("No se encontraron teléfonos.");
            break;
          }

          StringBuilder resultado = new StringBuilder();
          resultado.append("\n===== LISTA DE TELÉFONOS =====\n");
          resultado.append(String.format("%-15s %-20s %-10s %-15s %-10s%n",
              "CodTelefono", "Nombre", "Precio", "Marca", "Disponible"));
          resultado.append("-------------------------------------------------------------\n");

          for (Telefonos telefono : telefonos) {
            resultado.append(String.format("%-15d %-20s %-10.2f %-15s %-10d%n",
                telefono.getCodTelefono(), telefono.getNombre(), telefono.getPrecio(), telefono.getMarca(),
                telefono.getDisponible()));
          }

          System.out.println(resultado.toString());
          break;
        case 2:
          System.out.println("Ingrese los detalles del teléfono:");
          System.out.print("Nombre: ");
          String nombre = scanner.nextLine();
          System.out.print("Precio: ");
          double precio = scanner.nextDouble();
          scanner.nextLine(); // consume newline
          System.out.print("Marca: ");
          String marca = scanner.nextLine();
          System.out.print("Disponible (1 para sí, 0 para no): ");
          int disponible = scanner.nextInt();
          scanner.nextLine(); // consume newline

          Telefonos nuevoTelefono = new Telefonos();
          nuevoTelefono.setNombre(nombre);
          nuevoTelefono.setPrecio(precio);
          nuevoTelefono.setMarca(marca);
          nuevoTelefono.setDisponible(disponible);

          String resultInsert = telefonoServicio.insertTelefono(nuevoTelefono);
          System.out.println(resultInsert);
          break;
        case 3:
             List<Telefonos> telefonos1 = telefonoServicio.getAllTelefonos();
             StringBuilder resultado2 = new StringBuilder();
          resultado2.append("\n===== LISTA DE TELÉFONOS =====\n");
          resultado2.append(String.format("%-15s %-20s %-10s %-15s %-10s%n",
              "CodTelefono", "Nombre", "Precio", "Marca", "Disponible"));
          resultado2.append("-------------------------------------------------------------\n");

          for (Telefonos telefono : telefonos1) {
            resultado2.append(String.format("%-15d %-20s %-10.2f %-15s %-10d%n",
                telefono.getCodTelefono(), telefono.getNombre(), telefono.getPrecio(), telefono.getMarca(),
                telefono.getDisponible()));
          }

          System.out.println(resultado2.toString());
         System.out.println("Ingrese el código del teléfono a actualizar:");
                int codTelefonoActualizar = scanner.nextInt();
                scanner.nextLine(); // consume newline

                System.out.println("Ingrese los nuevos detalles del teléfono:");
                System.out.print("Nombre: ");
                String nombreActualizar = scanner.nextLine();
                System.out.print("Precio: ");
                double precioActualizar = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                System.out.print("Marca: ");
                String marcaActualizar = scanner.nextLine();
                System.out.print("Disponible (1 para sí, 0 para no): ");
                int disponibleActualizar = scanner.nextInt();
                scanner.nextLine(); // consume newline

                Telefonos telefonoActualizado = new Telefonos();
                telefonoActualizado.setCodTelefono(codTelefonoActualizar);
                telefonoActualizado.setNombre(nombreActualizar);
                telefonoActualizado.setPrecio(precioActualizar);
                telefonoActualizado.setMarca(marcaActualizar);
                telefonoActualizado.setDisponible(disponibleActualizar);

                String resultUpdate = telefonoServicio.updateTelefono(telefonoActualizado);
                System.out.println(resultUpdate);
                break;
        case 4:
          return;
        default:
          System.out.println("Opción no válida.");
      }
    }
  }

  private static void obtenerFacturas(Scanner scanner) {
    System.out.println("Ingrese la cédula:");
    String cedula = scanner.nextLine();
    List<Factura> facturas = compraServicio.obtenerFactura(cedula);

    if (facturas == null || facturas.isEmpty()) {
      System.out.println("No se encontraron facturas para la cédula: " + cedula);
      return;
    }

    StringBuilder resultado = new StringBuilder();
    resultado.append("\n===== LISTA DE FACTURAS =====\n");
    resultado.append(String.format("%-10s %-15s %-15s %-10s %-15s %-20s %-20s %-15s%n",
        "CodCompra", "FormaPago", "Fecha", "Descuento", "PrecioFinal", "NombreCliente", "NombreTelefono",
        "MarcaTelefono"));
    resultado.append("---------------------------------------------------------------------------------------------\n");

    for (Factura factura : facturas) {
      resultado.append(String.format("%-10d %-15s %-15s %-10.2f %-15.2f %-20s %-20s %-15s%n",
          factura.getCodCompra(), factura.getFormaPago(), factura.getFecha(), factura.getDescuento(),
          factura.getPreciofinal(),
          factura.getNombreCliente(), factura.getNombreTelefono(), factura.getMarcaTelefono()));
    }

    System.out.println(resultado.toString());
  }

}