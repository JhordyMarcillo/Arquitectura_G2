# Contrato de servicios EurekaBank (GR02)

Todos los servidores de `SOA BDD_LOCAL` implementan **las mismas operaciones, con los mismos nombres, datos y códigos de error**:

- **REST:** TI.1.3 (Java) y TI.1.4 (.NET) exponen exactamente la misma API. Cualquier cliente REST funciona con cualquiera de los dos servidores; solo cambia la URL base.
- **SOAP:** TI.1.1 (Java) y TI.1.2 (.NET) exponen las mismas operaciones y el mismo espacio de nombres.

Las reglas de negocio replican los procedimientos oficiales de EurekaBank del docente (`usp_deposito`, `usp_egcc_retiro`, `usp_egcc_transferencia`).

---

## 1. Reglas de negocio

| Operación | Validaciones | Efecto en la base |
|---|---|---|
| Iniciar sesión | El usuario existe en `Usuario`, su estado es `ACTIVO` y `SHA1(clave)` coincide con `vch_emplclave` | Devuelve un token de sesión (60 min), el código y el nombre del empleado |
| Depósito | La cuenta existe y está `ACTIVO`, importe > 0 | `saldo += importe - costo`; movimientos `003` (importe) y `010` (costo); `int_cuencontmov += 2` |
| Retiro | La cuenta existe y está `ACTIVO`, la clave de la cuenta es correcta, importe > 0, `saldo >= importe + costo` | `saldo -= importe + costo`; movimientos `004` y `010`; `int_cuencontmov += 2` |
| Transferencia | Ambas cuentas existen y están `ACTIVO`, son distintas, la clave de origen es correcta, misma moneda, importe > 0, `saldo origen >= importe + costo` | Origen: `saldo -= importe + costo`, movimientos `009` (referencia = destino) y `010`. Destino: `saldo += importe - costo`, movimientos `008` (referencia = origen) y `010`. `int_cuencontmov += 2` en cada cuenta |

- **Costo por movimiento:** `CostoMovimiento.dec_costimporte` según la moneda de la cuenta (Soles 2.00, Dólares 0.60).
- **Empleado:** el que figura en el movimiento es el que inició la sesión (se obtiene del token).
- **Atomicidad:** cada operación es una transacción. Si algo falla, no se modifica nada.

## 2. Códigos de respuesta

Toda respuesta incluye `estado` (`OK` o `ERROR`), `codigo` y `mensaje`. Los mensajes van sin tildes ni ñ.

| Código | HTTP (REST) | Significado |
|---|---|---|
| `200` | 200 | Operación correcta |
| `AUTH-001` | 401 | Token inválido o ausente |
| `AUTH-002` | 401 | Sesión expirada |
| `AUTH-003` | 401 | Usuario o clave incorrectos |
| `CTA-001` | 404 | La cuenta no existe |
| `CTA-002` | 409 | La cuenta no está activa |
| `CTA-003` | 401 | Clave de la cuenta incorrecta |
| `CTA-004` | 409 | Saldo insuficiente |
| `CTA-005` | 409 | Las cuentas tienen monedas distintas |
| `CTA-006` | 400 | La cuenta de origen y la de destino son la misma |
| `DATA-001` | 400 | Datos incompletos o inválidos (por ejemplo, importe <= 0) |
| `SRV-001` | 500 | Error interno (por ejemplo, la base no responde) |

## 3. API REST (TI.1.3 y TI.1.4)

URL base: `http://HOST:PUERTO/<contexto>/api/eurekabank`

| Servidor | URL base por defecto |
|---|---|
| TI.1.4 .NET | `http://localhost:5080/api/eurekabank` |
| TI.1.3 Java (Payara) | `http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank` |

Todas las rutas, salvo `login`, exigen la cabecera `Authorization: Bearer <token>`. El JSON va en camelCase.

| Método | Ruta | Cuerpo | Respuesta (además de `estado`, `codigo` y `mensaje`) |
|---|---|---|---|
| POST | `/login` | `{ "usuario", "clave" }` | `token`, `expiracion`, `codigoEmpleado`, `nombreEmpleado` |
| GET | `/cuentas` | — | `cuentas[]`: `{ codigo, cliente, moneda, saldo, estado }` |
| GET | `/cuentas/{codigo}/movimientos` | — | `cuenta`, `moneda`, `saldo`, `movimientos[]`: `{ numero, fecha, tipo, descripcion, accion, importe, cuentaReferencia }` |
| POST | `/depositos` | `{ "cuenta", "importe" }` | `cuenta`, `saldo` (saldo nuevo) |
| POST | `/retiros` | `{ "cuenta", "clave", "importe" }` | `cuenta`, `saldo` |
| POST | `/transferencias` | `{ "cuentaOrigen", "claveOrigen", "cuentaDestino", "importe" }` | `cuenta` (origen), `saldo` (saldo nuevo del origen) |

Formatos:
- `fecha`: `yyyy-MM-dd`.
- `expiracion`: `yyyy-MM-ddTHH:mm:ss` (hora local del servidor).
- Importes y saldos: número con 2 decimales.
- `accion`: `INGRESO` o `SALIDA`.
- `cuentaReferencia`: `null` cuando no aplica.

Ejemplo de respuesta de `/depositos`:
```json
{ "estado": "OK", "codigo": "200", "mensaje": "Deposito realizado correctamente", "cuenta": "00100001", "saldo": 6998.00 }
```

## 4. Servicio SOAP (TI.1.1 y TI.1.2)

- **Espacio de nombres:** `http://ws.monster.edu.ec/`
- **Servicio:** `WSEurekaBank`
- **Estilo:** document/literal wrapped
- **Rutas:**
  - TI.1.2 .NET: `http://localhost:5090/WSEurekaBank.svc` (WSDL en `?wsdl`)
  - TI.1.1 Java (Payara): `http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank` (WSDL en `?wsdl`)

| Operación | Parámetros | Resultado |
|---|---|---|
| `iniciarSesion` | `usuario`, `clave` | `RespuestaSesion`: `estado, codigo, mensaje, token, expiracion, codigoEmpleado, nombreEmpleado` |
| `listarCuentas` | `token` | `RespuestaCuentas`: `estado, codigo, mensaje, cuentas[] (DatosCuenta)` |
| `consultarMovimientos` | `token`, `cuenta` | `RespuestaMovimientos`: `estado, codigo, mensaje, cuenta, moneda, saldo, movimientos[] (DatosMovimiento)` |
| `registrarDeposito` | `token`, `cuenta`, `importe` | `RespuestaOperacion`: `estado, codigo, mensaje, cuenta, saldo` |
| `registrarRetiro` | `token`, `cuenta`, `clave`, `importe` | `RespuestaOperacion` |
| `registrarTransferencia` | `token`, `cuentaOrigen`, `claveOrigen`, `cuentaDestino`, `importe` | `RespuestaOperacion` |

En SOAP, los errores de negocio **no lanzan fault**: se devuelven en la respuesta con `estado = ERROR` y su `codigo`, igual que en REST.

## 5. Datos de prueba

| Usuario | Clave | Estado |
|---|---|---|
| `MONSTER` | `Monster9` | ACTIVO (usuario del GR02, empleado 0015) |
| `cromero` | `chicho` | ACTIVO |
| `lcastro` | `flaca` | ACTIVO |
| `creyes` | `linda` | ANULADO (no puede entrar) |

| Cuenta | Moneda | Clave | Estado |
|---|---|---|---|
| `00100001` | Soles | `123456` | ACTIVO |
| `00200001` | Soles | `123456` | ACTIVO |
| `00100002` | Dólares | `123456` | ACTIVO |
| `00300001` | Soles | `123456` | CANCELADO |
