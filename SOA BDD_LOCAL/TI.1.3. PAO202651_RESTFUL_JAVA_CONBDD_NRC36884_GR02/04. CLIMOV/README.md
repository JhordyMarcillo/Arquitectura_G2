# 04. CLIMOV: CLIMOV_EurekaBank_JAVA_RESTFUL_GR02

Cliente **móvil Android nativo** EurekaBank (RESTful Java) con **Material 3**: login de marca, pestañas Cuentas / Movimientos / Operar, tarjetas, historial con colores, selector segmentado y comprobante. Las llamadas al servicio van en hilos de fondo.

## Ejecutar
- **Android Studio:** abrir esta carpeta y ejecutar en un emulador o celular.
- **Línea de comandos:** `gradlew assembleDebug` (requiere la variable `ANDROID_HOME`); el APK queda en `%LOCALAPPDATA%\EurekaBankGR02\CLIMOV_EurekaBank_JAVA_RESTFUL_GR02\app\outputs\apk\debug`.
- **URL del servidor:** en el emulador se usa `10.0.2.2` (la PC); en un celular real, la IP de la máquina del servidor. También funciona con el servidor REST .NET del TI.1.4 (`http://localhost:5080/api/eurekabank`): basta cambiar la URL en el inicio de sesión.

> La carpeta `build` se genera en `%LOCALAPPDATA%\EurekaBankGR02` porque aapt2 no soporta rutas largas. HTTP sin cifrar está habilitado para la red local.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Paquete / carpeta | Contenido |
|---|---|
| `ec.edu.monster.vistas` | `ActividadLogin`, `ActividadPrincipal`, fragmentos `FragmentoCuentas`, `FragmentoMovimientos`, `FragmentoOperaciones` y adaptadores |
| `ec.edu.monster.controladores` | `SesionApp`: servicio compartido, URL recordada y tareas en segundo plano |
| `ec.edu.monster.servicios` / `modelos` / `utilidades` | Núcleo Java común con el resto de clientes del TI.1.3 |
| `res` | Colores, tema, fondos, iconos vectoriales y layouts |
