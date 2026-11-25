# 04. SERVIDOR: WS_EurekaBank_JAVA_SOAP_GR02

Servicio SOAP de EurekaBank en **Jakarta EE 11 (JAX-WS, Metro)**, desplegado en **Payara 7**, sobre **MySQL** (probado con 9.7). Cumple el contrato común del grupo: [`CONTRATO_EUREKABANK_GR02.md`](../../CONTRATO_EUREKABANK_GR02.md), sección 4. Publica las mismas operaciones, el mismo espacio de nombres (`http://ws.monster.edu.ec/`) y el mismo XML que el servicio WCF del TI.1.2, así que los clientes SOAP de los dos TI son intercambiables.

## Ejecutar
1. **Base de datos:** crear `eurekabank` con los scripts de `03. BDD` (MySQL instalado o `docker compose up -d`).
2. **NetBeans:** abrir `WS_EurekaBank_JAVA_SOAP_GR02` y elegir **Run** con Payara como servidor. A mano:
   ```bash
   mvn package
   asadmin deploy --force=true target/WS_EurekaBank_JAVA_SOAP_GR02.war
   ```
3. **Servicio:** `http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank` (WSDL en `?wsdl`).

El driver de MySQL va dentro del WAR, así que no hace falta crear pools ni recursos JDBC en Payara.

**Conexión a la base.** Se resuelve en este orden:
1. Las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD` (las mismas del `.env` de `03. BDD`).
2. Las propiedades de sistema `db.url`, `db.user` y `db.password`.
3. `src/main/resources/db.properties`, que apunta a `localhost:3306` con `eureka/admin`.

## Operaciones
| Operación | Parámetros | Resultado |
|---|---|---|
| `iniciarSesion` | `usuario`, `clave` | `RespuestaSesion` (`token`, `expiracion`, `codigoEmpleado`, `nombreEmpleado`) |
| `listarCuentas` | `token` | `RespuestaCuentas` (`cuentas` → `DatosCuenta`) |
| `consultarMovimientos` | `token`, `cuenta` | `RespuestaMovimientos` (`cuenta`, `moneda`, `saldo`, `movimientos` → `DatosMovimiento`) |
| `registrarDeposito` | `token`, `cuenta`, `importe` | `RespuestaOperacion` (`cuenta`, `saldo`) |
| `registrarRetiro` | `token`, `cuenta`, `clave`, `importe` | `RespuestaOperacion` |
| `registrarTransferencia` | `token`, `cuentaOrigen`, `claveOrigen`, `cuentaDestino`, `importe` | `RespuestaOperacion` |

Todas las respuestas traen `estado`, `codigo` y `mensaje`. Los errores de negocio **no lanzan fault**: vuelven con `estado = ERROR` y su código (`AUTH-001`, `CTA-003`...).

## Arquitectura (paquetes)
| Paquete | Contenido |
|---|---|
| `ec.edu.monster.controladores.ws` | `WSEurekaBank`: el endpoint SOAP (`@WebService`) |
| `ec.edu.monster.controladores.manejadoresws` | `ManejadorAutenticacion`: `SOAPHandler` que lee el `token` de cada petición (salvo `iniciarSesion`); se registra en `manejador.xml` |
| `ec.edu.monster.datos` | `AccesoDB`, `ConfiguracionBaseDatos` y repositorios de `Usuario`, `Cuenta` y `Movimiento` (JDBC con `PreparedStatement`) |
| `ec.edu.monster.modelos.utilidades` | Respuestas SOAP (JAXB, elementos calificados en el espacio de nombres del contrato), `DatosCuenta`, `DatosMovimiento`, solicitudes internas, `FabricaRespuesta`, `ExcepcionNegocio` e `IServicioOperaciones` |
| `ec.edu.monster.modelos.utilidades.enums` | `CodigoRespuesta`, `EstadoRespuesta`, `EstadoCuenta`, `EstadoToken` y `TipoMovimiento` |
| `ec.edu.monster.modelos.utilidades.mapeadores` | Conversión de `ResultSet` a objetos y de tipo de movimiento a código |
| `ec.edu.monster.pruebas` | `PruebaConexion` y `PruebaServicios`: pruebas manuales con `main` |
| `ec.edu.monster.seguridad` | `AdministradorTokens` (sesión de 60 min) y `CifradoClave` (SHA1 en hexadecimal) |
| `ec.edu.monster.servicios` | `ServicioAutenticacion`, `ServicioCuentas` y `ServicioOperaciones` (cada operación en una transacción con `SELECT ... FOR UPDATE`) |

## Pruebas
- `mvn test`: pruebas JUnit 5 de cifrado, tokens y reglas de importe, clave y saldo.
- `python "../../pruebas/test_contrato_soap.py" http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank`: resultado **20/20 verificaciones correctas**.
- Compatibilidad con WCF: el proxy `IWSEurekaBank.cs` de los clientes del TI.1.2 funciona contra este servidor sin cambios (login, cuentas, movimientos, depósito y errores).
