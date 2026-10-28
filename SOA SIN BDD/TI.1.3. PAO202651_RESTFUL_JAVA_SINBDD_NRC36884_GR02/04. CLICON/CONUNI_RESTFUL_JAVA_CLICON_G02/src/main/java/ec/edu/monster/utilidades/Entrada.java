package ec.edu.monster.utilidades;

import java.util.Scanner;

/**
 * Único Scanner sobre System.in compartido por todas las vistas.
 * Varios Scanner sobre la misma entrada se "roban" líneas entre sí.
 */
public final class Entrada {
    public static final Scanner SCANNER = new Scanner(System.in, "UTF-8");

    private Entrada() { }
}
