package ec.edu.monster.vista;

import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.controlador.Configuracion;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.modelo.ExcepcionApi;
import ec.edu.monster.modelo.SesionUsuario;

import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsolaApp {

    private static final String LINEA = "=".repeat(50);

    private static final Map<String, String> ICONOS_CATEGORIA = Map.of(
            "MASA", "⚖️ ",
            "LONGITUD", "📏",
            "TEMPERATURA", "🌡️ ");

    private enum FinMenu { SALIR, CERRAR_SESION, REAUTENTICAR }

    private final ApiService apiService = new ApiService();
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    public static void main(String[] args) {
        // Emojis y braille: la consola debe estar en UTF-8 (chcp 65001)
        activarUtf8EnWindows();
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        try {
            new ConsolaApp().ejecutar();
        } catch (NoSuchElementException e) {
            System.out.println("\n\n⚠️ Programa interrumpido por el usuario.");
        }
    }

    // Hace el chcp 65001 de ejecutar.bat, para cuando se lanza con java -jar
    private static void activarUtf8EnWindows() {
        if (!System.getProperty("os.name", "").startsWith("Windows")) {
            return;
        }
        try {
            new ProcessBuilder("cmd", "/c", "chcp 65001 > nul")
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch (IOException e) {
            // Sin cmd la consola queda como estaba
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void ejecutar() {
        System.out.println(LINEA);
        System.out.println("🚀 CONVERSOR DE UNIDADES");
        System.out.println(LINEA);
        System.out.println("Cliente de consola Java para API RESTful .NET");
        System.out.println("Servidor: " + Configuracion.URL_BASE);
        System.out.println(LINEA);

        // Informativo, no bloquea
        verificarConexionServidor();

        while (true) {
            if (!iniciarSesion()) {
                System.out.println("\n💡 Pista: Usuario = MONSTER, Contraseña = Monster9");
                return;
            }

            FinMenu fin = menuPrincipal();
            if (fin == FinMenu.SALIR) {
                return;
            }
            if (fin == FinMenu.REAUTENTICAR) {
                // CU-09: el servidor respondio AUTH-001 o AUTH-002
                System.out.println("\n🔒 Su sesión terminó. Vuelva a iniciar sesión.");
            }
        }
    }

    // ------------------------------------------------------------------
    // Conexion
    // ------------------------------------------------------------------
    private boolean verificarConexionServidor() {
        System.out.println("\n🔌 Verificando conexión con el servidor...");
        System.out.println("   Endpoint: " + Configuracion.URL_BASE);
        if (apiService.verificarConexion()) {
            System.out.println("✅ Servidor ACTIVO");
            return true;
        }
        System.out.println("⚠️  No se pudo conectar con el servidor");
        System.out.println("   Las conversiones necesitan el servidor encendido");
        return false;
    }

    // ------------------------------------------------------------------
    // CU-01 Autenticarse
    // ------------------------------------------------------------------
    private boolean iniciarSesion() {
        System.out.println("\n" + LINEA);
        System.out.println("🔐 INICIAR SESIÓN");
        System.out.println(LINEA);

        int intentos = 3;
        while (intentos > 0) {
            System.out.println("\n📌 Intentos restantes: " + intentos);
            String usuario = leerLinea("👤 Usuario: ").trim();
            String contrasena = leerContrasena("🔑 Contraseña: ");

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                System.out.println("⚠️  Usuario y contraseña son obligatorios");
                continue;
            }

            System.out.println("\n" + LINEA);
            System.out.println("🔐 Verificando credenciales...");
            System.out.println(LINEA);

            try {
                apiService.login(usuario, contrasena);
                System.out.println("\n✅ Login exitoso!");
                ArteSullivan.mostrar();
                System.out.println("\n👤 Bienvenido/a: " + usuario);
                return true;
            } catch (ExcepcionApi e) {
                mostrarError(e);
            }

            intentos--;
            if (intentos > 0) {
                System.out.println("\n⚠️  Intenta nuevamente.");
            } else {
                System.out.println("\n❌ Número máximo de intentos alcanzado. Programa terminado.");
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // CU-02 Consultar catalogo (menu principal)
    // ------------------------------------------------------------------
    private FinMenu menuPrincipal() {
        while (SesionUsuario.estaActiva()) {
            List<String> categorias;
            try {
                categorias = apiService.obtenerCategorias();
            } catch (ExcepcionApi e) {
                mostrarError(e);
                if (e.esErrorDeSesion()) {
                    break;
                }
                pausa("\n⏸️  Presione ENTER para reintentar...");
                continue;
            }

            int opcionVerificar = categorias.size() + 1;
            int opcionCerrarSesion = Configuracion.PERMITIR_CERRAR_SESION ? categorias.size() + 2 : -1;
            int opcionSalir = Configuracion.PERMITIR_CERRAR_SESION ? categorias.size() + 3 : categorias.size() + 2;

            System.out.println("\n" + LINEA);
            System.out.println("🌐 CONVERSOR DE UNIDADES");
            System.out.println(LINEA);
            System.out.println("👤 Usuario: " + SesionUsuario.getUsuario());
            System.out.println("🔗 Servidor: " + Configuracion.URL_BASE);
            System.out.println(LINEA);
            for (int i = 0; i < categorias.size(); i++) {
                String categoria = categorias.get(i);
                System.out.println((i + 1) + ". " + ICONOS_CATEGORIA.getOrDefault(categoria, "📐") + " Conversiones de " + categoria);
            }
            System.out.println(opcionVerificar + ". 🔌 Verificar conexión con servidor");
            if (Configuracion.PERMITIR_CERRAR_SESION) {
                System.out.println(opcionCerrarSesion + ". 🔒 Cerrar sesión");
            }
            System.out.println(opcionSalir + ". 🚪 Salir");
            System.out.println(LINEA);

            int opcion = leerEntero("\n👉 Seleccione una opción: ");

            if (opcion >= 1 && opcion <= categorias.size()) {
                menuConversiones(categorias.get(opcion - 1));
            } else if (opcion == opcionVerificar) {
                verificarConexionServidor();
                pausa("\n⏸️  Presione ENTER para continuar...");
            } else if (Configuracion.PERMITIR_CERRAR_SESION && opcion == opcionCerrarSesion) {
                System.out.println("\n🔒 Cerrando sesión de " + SesionUsuario.getUsuario() + "...");
                SesionUsuario.cerrar();
                return FinMenu.CERRAR_SESION;
            } else if (opcion == opcionSalir) {
                System.out.println("\n👋 Saliendo...");
                System.out.println("Hasta pronto, " + SesionUsuario.getUsuario() + "!");
                ArteDespedida.mostrar();
                return FinMenu.SALIR;
            } else {
                System.out.println("\n❌ Opción no válida. Por favor, seleccione una opción del 1 al " + opcionSalir + ".");
            }
        }
        return FinMenu.REAUTENTICAR;
    }

    // ------------------------------------------------------------------
    // CU-03 Consultar unidades y CU-04 Realizar conversion
    // ------------------------------------------------------------------
    private void menuConversiones(String categoria) {
        List<String> unidades;
        try {
            unidades = apiService.obtenerUnidades(categoria);
        } catch (ExcepcionApi e) {
            mostrarError(e);
            return;
        }

        String icono = ICONOS_CATEGORIA.getOrDefault(categoria, "📐");
        while (SesionUsuario.estaActiva()) {
            System.out.println("\n" + LINEA);
            System.out.println(icono + " CONVERSIONES DE " + categoria);
            System.out.println(LINEA);
            for (int i = 0; i < unidades.size(); i++) {
                System.out.println((i + 1) + ". " + unidades.get(i));
            }
            System.out.println("0. ⬅️  Volver al menú principal");
            System.out.println(LINEA);

            String origen = elegirUnidad(unidades, "origen");
            if (origen == null) {
                break;
            }
            String destino = elegirUnidad(unidades, "destino");
            if (destino == null) {
                break;
            }

            Double valor = leerValor(origen);
            if (valor != null) {
                realizarConversion(categoria, origen, destino, valor);
            }

            pausa("\n⏸️  Presione ENTER para continuar...");
        }
    }

    private void realizarConversion(String categoria, String origen, String destino, double valor) {
        System.out.println("\n" + LINEA);
        try {
            ConversionResponse respuesta = apiService.realizarConversion(
                    new ConversionRequest(categoria, origen, destino, valor));
            System.out.println("✅ RESULTADO DE LA CONVERSIÓN");
            System.out.println(LINEA);
            System.out.println("   " + FormatoNumero.formatear(valor) + " " + respuesta.getUnidadOrigen()
                    + " = " + FormatoNumero.formatear(respuesta.getResultado()) + " " + respuesta.getUnidadDestino());
            System.out.println("   " + respuesta.getMensaje());
        } catch (ExcepcionApi e) {
            mostrarError(e);
        }
        System.out.println(LINEA);
    }

    // ------------------------------------------------------------------
    // Utilidades de consola
    // ------------------------------------------------------------------
    // CU-08 a CU-11: el cliente presenta el mensaje del servidor
    private void mostrarError(ExcepcionApi e) {
        if (e.esErrorDeRed()) {
            System.out.println("❌ " + e.getMessage());
        } else {
            System.out.println("❌ [" + e.getCodigo() + "] " + e.getMessage());
        }
    }

    private String elegirUnidad(List<String> unidades, String rol) {
        while (true) {
            int opcion = leerEntero("\n👉 Seleccione la unidad de " + rol + ": ");
            if (opcion == 0) {
                return null;
            }
            if (opcion >= 1 && opcion <= unidades.size()) {
                return unidades.get(opcion - 1);
            }
            System.out.println("❌ Opción no válida");
        }
    }

    // Acepta coma o punto decimal
    private Double leerValor(String unidad) {
        String texto = leerLinea("\n🔢 Ingrese el valor en " + unidad + ": ").trim().replace(',', '.');
        try {
            double valor = Double.parseDouble(texto);
            if (Double.isFinite(valor)) {
                return valor;
            }
        } catch (NumberFormatException e) {
            // se informa abajo
        }
        System.out.println("❌ Error: Debe ingresar un número válido");
        return null;
    }

    private int leerEntero(String mensaje) {
        try {
            return Integer.parseInt(leerLinea(mensaje).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    // Oculta la contrasena si hay consola real; en el IDE se lee como texto normal
    private String leerContrasena(String mensaje) {
        Console consola = System.console();
        if (consola != null) {
            char[] clave = consola.readPassword(mensaje);
            return clave == null ? "" : new String(clave);
        }
        return leerLinea(mensaje);
    }

    private void pausa(String mensaje) {
        leerLinea(mensaje);
    }
}
