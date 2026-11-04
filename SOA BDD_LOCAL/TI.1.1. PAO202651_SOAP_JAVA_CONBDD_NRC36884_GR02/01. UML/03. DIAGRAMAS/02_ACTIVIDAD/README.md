# 02_ACTIVIDAD

Un modelo PowerDesigner por diagrama (`ACT_00` a `ACT_05`). Cada rama de error termina en su propio nodo final y muestra el código que devuelve el servicio.

| Imagen | Contenido |
|---|---|
| `AD00_FLUJO_GENERAL.png` | Recorrido del empleado: inicio de sesión, panel, consultas, operaciones y cierre |
| `AD01_INICIAR_SESION.png` | Validación de datos, clave SHA-1 y emisión del token (DATA-001, AUTH-003) |
| `AD02_CONSULTAR_CUENTAS_MOVIMIENTOS.png` | `listarCuentas` y `consultarMovimientos` con validación del token (AUTH-001/002, CTA-001) |
| `AD03_REGISTRAR_DEPOSITO.png` | Depósito: transacción con `SELECT ... FOR UPDATE`, movimientos 003 y 010 |
| `AD04_REGISTRAR_RETIRO.png` | Retiro: clave (CTA-003) y saldo suficiente (CTA-004), movimientos 004 y 010 |
| `AD05_REGISTRAR_TRANSFERENCIA.png` | Transferencia: cuentas distintas (CTA-006), clave, misma moneda (CTA-005) y saldo; movimientos 009/008 y 010 |
