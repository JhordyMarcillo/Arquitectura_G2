# 04. SERVIDOR: WS_EurekaBank_JAVA_RESTFUL_GR02

Servicio REST de EurekaBank en **Jakarta EE 10 (JAX-RS)**, desplegado en **Payara 6**, sobre **MySQL 8**. Cumple el contrato común del grupo: [`CONTRATO_EUREKABANK_GR02.md`](../../CONTRATO_EUREKABANK_GR02.md). Devuelve exactamente el mismo JSON que el servidor .NET del TI.1.4, así que cualquier cliente REST funciona con los dos.

## Ejecutar
1. **Base de datos:** en `03. BDD`, ejecutar `docker compose up -d`.
2. **NetBeans:** abrir `WS_EurekaBank_JAVA_RESTFUL_GR02`, elegir **Run** con Payara como servidor. Para hacerlo a mano: `mvn package` y copiar `target/WS_EurekaBank_JAVA_RESTFUL_GR02.war` a la carpeta de autodespliegue de Payara.
3. **URL base:** `http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank`

El driver de MySQL va dentro del WAR, así que no hace falta crear pools ni recursos JDBC en Payara.

**Conexión a la base.** Se resuelve en este orden:
1. Las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD` (las mismas del `.env` de `03. BDD`).
2. Las propiedades de sistema `db.url`, `db.user` y `db.password`.
3. `src/main/resources/db.properties`, que apunta a `localhost:3306` con `eureka/admin`.

**Payara en Docker (opcional):** sirve si no tienes Payara instalado.
```bash
docker run -d --name eurekabank-payara -p 8080:8080 -p 4848:4848 \
  -e "DB_URL=jdbc:mysql://host.docker.internal:3306/eurekabank?useSSL=false&allowPublicKeyRetrieval=true" \
  -v "<carpeta con el .war>:/opt/payara/deployments" payara/server-full:6.2024.6-jdk17
```

## Arquitectura (paquetes)
| Paquete | Contenido |
|---|---|
| `ec.edu.monster.controladores.api` | `AplicacionRest` (`/api`) y `ControladorEurekaBank` (rutas REST) |
| `ec.edu.monster.controladores.manejadores` | `ManejadorAutenticacion` (filtro del token Bearer), `ManejadorErrores` (respuesta de error uniforme), `ConfiguracionJson` y `PermitirAnonimo` |
| `ec.edu.monster.datos` | `AccesoDB`, `ConfiguracionBaseDatos` y repositorios de `Usuario`, `Cuenta` y `Movimiento` (JDBC con `PreparedStatement`) |
| `ec.edu.monster.modelos.utilidades` | Solicitudes, respuestas, `DatosCuenta`, `DatosMovimiento`, `FabricaRespuesta`, `ExcepcionNegocio` e `IServicioOperaciones` |
| `ec.edu.monster.modelos.utilidades.enums` | `CodigoRespuesta` (código y estado HTTP), `EstadoRespuesta`, `EstadoCuenta`, `EstadoToken` y `TipoMovimiento` |
| `ec.edu.monster.modelos.utilidades.mapeadores` | Conversión de `ResultSet` a objetos y de tipo de movimiento a código |
| `ec.edu.monster.pruebas` | `PruebaConexion` y `PruebaServicios`: pruebas manuales con `main` |
| `ec.edu.monster.seguridad` | `AdministradorTokens`, `CifradoClave` (SHA1, igual que `SHA()` de MySQL) y `FiltroCors` |
| `ec.edu.monster.servicios` | `ServicioAutenticacion`, `ServicioCuentas` y `ServicioOperaciones` (cada operación en una transacción con `SELECT ... FOR UPDATE`) |

## Pruebas
- `mvn test`: pruebas JUnit 5 de cifrado, tokens y reglas de importe, clave y saldo.
- `python "../../pruebas/test_contrato_rest.py" http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank`: resultado **32/32 verificaciones correctas**.
