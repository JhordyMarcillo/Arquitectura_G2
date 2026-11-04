# 03_SECUENCIA

Modelo PowerDesigner `SEC_EUREKABANK.oom`. Los participantes son las clases reales del proyecto: vista del cliente, `ServicioEurekaBankSoap`, `ManejadorAutenticacion`, `WSEurekaBank`, `AdministradorTokens`, servicios, repositorios y MySQL.

| Imagen | Contenido |
|---|---|
| `SD01_INICIAR_SESION.png` | `iniciarSesion`: búsqueda del usuario y generación del token |
| `SD02_VALIDAR_TOKEN.png` | Lectura del `<token>` del sobre SOAP en el handler y rechazo AUTH-001 / AUTH-002 |
| `SD03_CONSULTAR_CUENTAS.png` | `listarCuentas` |
| `SD04_CONSULTAR_MOVIMIENTOS.png` | `consultarMovimientos` |
| `SD05_REGISTRAR_DEPOSITO.png` | `registrarDeposito` dentro de una transacción |
| `SD06_REGISTRAR_RETIRO.png` | `registrarRetiro` con validación de clave y saldo |
| `SD07_REGISTRAR_TRANSFERENCIA.png` | `registrarTransferencia` con bloqueo de ambas cuentas |
