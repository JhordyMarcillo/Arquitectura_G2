# Guía para la presentación (TI.1.1 a TI.1.4)

Todo se controla con **un solo archivo**: `CONFIGURACION_PRESENTACION.txt`.

```
IP=AUTO        # o la IP fija de esta PC, p. ej. IP=192.168.43.120
CELULAR=RED    # RED (Wi-Fi / hotspot) o USB (por el cable)
```

Luego, doble clic en el TI que vayan a presentar:

| Archivo | Servidor | Clientes que abre | Usuario / clave |
|---|---|---|---|
| `LEVANTAR_TI_1.1.bat` | SOAP Java en Payara `:8080` | consola Java, web Blazor `:5240`, escritorio MAUI, móvil MAUI | `MONSTER9` / `MONSTER9` |
| `LEVANTAR_TI_1.2.bat` | SOAP .NET en IIS Express `:8085` | consola .NET, escritorio WinForms, web ASP.NET `:5015`, móvil Android | `MONSTER` / `MONSTER9` |
| `LEVANTAR_TI_1.3.bat` | REST Java en Payara `:8080` | consola Java, escritorio Swing, web JSP `:8080`, móvil Android | `MONSTER` / `MONSTER9` |
| `LEVANTAR_TI_1.4.bat` | REST .NET `:5014` | consola Java, escritorio Java, web Flask `:3000`, móvil Android | `MONSTER` / `Monster9` |

Cada `.bat`:
1. Pide permiso de administrador (aceptar **Sí**): lo necesita para abrir el firewall.
2. Escribe la IP en la configuración de todos los clientes.
3. Compila y levanta el servidor y espera a que responda.
4. Abre la web en el navegador, la consola en su ventana y el escritorio.
5. Compila la app Android, **la instala en el celular conectado por USB y la abre**.
6. Al final muestra la IP, las URL y las credenciales. Si algo falló, lo lista en rojo.

Tarda entre 2 y 5 minutos (TI.1.1 es el más lento por MAUI). Para cerrar todo: `DETENER_TODO.bat`.
Si el celular no estaba conectado, el APK queda en `presentacion\APKs\TI_1.x.apk` para instalarlo a mano.

---

## Preparar el celular (una sola vez)

1. **Ajustes → Acerca del teléfono** → tocar 7 veces **Número de compilación** (activa las opciones de desarrollador).
2. **Ajustes → Opciones de desarrollador** → activar **Depuración USB**
   (en Xiaomi también **Instalar vía USB**).
3. Conectar el cable a la PC y, en el celular, aceptar **¿Permitir depuración USB?** marcando *Permitir siempre*.
4. Comprobar: el `.bat` dice *App instalada* y *App abierta en el celular*.

---

## ¿Cómo se conecta el celular al servidor?

### Opción A — Hotspot del celular (recomendada con `CELULAR=RED`)
1. En el celular: activar **datos móviles** y la **zona Wi-Fi / hotspot**.
2. Conectar la **PC** a ese hotspot.
3. Dejar `IP=AUTO` y ejecutar el `.bat`: detecta la IP que el hotspot le dio a la PC.

Funciona en cualquier lugar y no depende de la red de la ESPE. El tráfico entre la PC y el celular
se queda en el hotspot (casi no consume datos).

### Opción B — Wi-Fi de la ESPE (`CELULAR=RED`)
PC y celular conectados a la misma red de la ESPE. **Prueba primero**: abre en el navegador del celular
la URL web que muestra el `.bat` (p. ej. `http://10.x.x.x:5015`).
Si no carga, la red aísla a los equipos entre sí (es común en redes de universidades): usa la opción A o la C.

### Opción C — Cable USB (`CELULAR=USB`, la más segura)
1. Cambiar en el archivo: `CELULAR=USB`.
2. Ejecutar el `.bat` con el celular conectado por cable.

El celular llega al servidor **por el cable** (`adb reverse`), sin importar la red.
El resto de clientes sigue usando la IP. Hay que dejar el cable conectado durante la demostración.

### Opción D — Hotspot de la PC
Windows: **Configuración → Red e Internet → Zona con cobertura inalámbrica móvil** → activarla
y conectar el celular a la PC. La IP de la PC en esa red suele ser `192.168.137.1`
(poner `IP=192.168.137.1` si `AUTO` toma otra tarjeta).

> Si se cambia de red o de opción, **volver a ejecutar el `.bat`**: recompila la app móvil con la nueva IP.

---

## Antes de la presentación
- Ejecutar cada `.bat` una vez en casa (con internet) para que queden descargadas las dependencias.
- Payara, Maven, .NET 10, Python y Android Studio deben estar instalados (ya lo están en esta PC).
- Si el antivirus o Windows preguntan por permitir Java, dotnet o Python en la red: **Permitir**.

## Problemas frecuentes
| Síntoma | Solución |
|---|---|
| El celular no conecta (RED) | Probar la URL web en el navegador del celular; si no abre, usar hotspot (A) o USB (C) |
| *No hay celular conectado por USB* | Revisar el cable (que sea de datos), aceptar la depuración USB y volver a ejecutar |
| La IP detectada no es la correcta | Fijarla en `CONFIGURACION_PRESENTACION.txt` (ver `ipconfig`) |
| Un puerto ya está en uso | Ejecutar `DETENER_TODO.bat` y volver a levantar |
| TI.1.1: "Token invalido o sesion expirada" | El token dura 5 minutos: volver a iniciar sesión |
