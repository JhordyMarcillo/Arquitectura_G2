# 01_CASOS_USO

Modelo PowerDesigner `CU_EUREKABANK.oom` con el diagrama general y un diagrama por caso de uso.

| Imagen | Contenido |
|---|---|
| `DCU_EUREKABANK.png` | Diagrama general: actor Empleado (con los cuatro clientes como especializaciones) y los casos CU 01 a CU 11 con sus `<<include>>` y `<<extend>>` |
| `ECUD1_INICIAR_SESION.png` | CU 01 Iniciar sesión |
| `ECUD2_VALIDAR_TOKEN.png` | CU 02 Validar token de seguridad, extendido por CU 10 y CU 11 |
| `ECUD3_CONSULTAR_CUENTAS.png` | CU 03 Consultar cuentas |
| `ECUD4_CONSULTAR_MOVIMIENTOS.png` | CU 04 Consultar movimientos |
| `ECUD5_REGISTRAR_DEPOSITO.png` | CU 05 Registrar depósito |
| `ECUD6_REGISTRAR_RETIRO.png` | CU 06 Registrar retiro |
| `ECUD7_REGISTRAR_TRANSFERENCIA.png` | CU 07 Registrar transferencia |

Las operaciones que necesitan sesión incluyen CU 02 (token). Depósito, retiro y transferencia incluyen CU 09 (movimiento y costo); retiro y transferencia incluyen además CU 08 (clave y saldo).
