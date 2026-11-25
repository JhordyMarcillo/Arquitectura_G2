# 04_CLASES

Modelo PowerDesigner `CLA_EUREKABANK.oom`, dividido en cuatro diagramas.

| Imagen | Contenido |
|---|---|
| `CD01_EXPOSICION_SEGURIDAD.png` | `WSEurekaBank` (endpoint), `ManejadorAutenticacion`, `AdministradorTokens`, `SesionEmpleado`, `CifradoClave`, `FabricaRespuesta` y `ExcepcionNegocio` |
| `CD02_SERVICIOS_DATOS.png` | Servicios de autenticación, cuentas y operaciones; repositorios y `AccesoDB` |
| `CD03_MODELOS.png` | Respuestas SOAP, datos de cuenta y movimiento, enumeraciones de códigos y tipos de movimiento |
| `CD04_CLIENTES.png` | `IServicioEurekaBank` y sus implementaciones JAX-WS y SOAP manual (Android), con los controladores de cada cliente |
