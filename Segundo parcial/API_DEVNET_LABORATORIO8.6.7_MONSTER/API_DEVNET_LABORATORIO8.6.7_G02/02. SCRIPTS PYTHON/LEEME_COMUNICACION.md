# Comunicación entre los 3 integrantes por Webex

Una sola sala compartida. Cada integrante entra con su cuenta de Webex y envía mensajes con **su propio token**.

## Quién hace qué
| Paso | Quién | Qué |
|---|---|---|
| 1 | Cada integrante | Tener una cuenta de Webex **con el correo que figura en `participantes.py`** (gratis: https://web.webex.com) |
| 2 | Cada integrante | Completar su correo en `participantes.py` (hoy solo está el de David) |
| 3 | Un integrante (dueño de la sala) | Crear la sala y agregar a los demás |
| 4 | Cada integrante | Generar su token y enviar mensajes |

## 1. Cuenta de Webex (cada uno)
1. Entrar a https://web.webex.com y registrarse con el correo del grupo (por ejemplo `davidcepeda751@gmail.com`).
2. Confirmar el correo que llega y completar el nombre.
3. Iniciar sesión una vez en la aplicación o en la web para que la cuenta quede activa.

## 2. Correos (en este repositorio)
Editar `participantes.py` y reemplazar los dos `PENDIENTE-...` por los correos reales de German y Jhordy.

## 3. Crear la sala y agregar a todos (lo hace una sola persona)
```powershell
cd "02. SCRIPTS PYTHON"
$env:WEBEX_TOKEN='<token de quien crea la sala>'
python create-rooms.py        # anota el "id" de la sala que imprime
$env:WEBEX_ROOM_ID='<id de la sala>'
python add-all-members.py     # agrega a todos los de participantes.py
python list-memberships.py    # comprueba quién quedó en la sala
```
`add-all-members.py` informa `AGREGADO`, `YA ESTABA`, `OMITIDO` (correo pendiente) o `ERROR` por cada persona. Se puede repetir sin problema.

## 4. Enviar mensajes (cada uno, desde su equipo)
1. Entrar a https://developer.webex.com con su cuenta, abrir **Documentation > Getting Started** y copiar su **Personal Access Token** (dura **12 horas**).
2. Definir las variables (el id de la sala se lo pasa quien la creó):
```powershell
$env:WEBEX_TOKEN='<su token personal>'
$env:WEBEX_ROOM_ID='<id de la sala>'
python authentication.py                      # debe responder 200 y mostrar su nombre
python send-message.py "Hola equipo, aquí David"
```
3. Los mensajes aparecen en la sala, con el nombre de quien los envió, en la app o en https://web.webex.com.

## Problemas frecuentes
| Síntoma | Causa y solución |
|---|---|
| `HTTP 401` | Token vencido o mal copiado: generar uno nuevo. |
| `Falta la variable de entorno WEBEX_...` | No se definió la variable en esa ventana de PowerShell (se pierde al cerrarla). |
| `ERROR ... HTTP 400/404` al agregar | El correo no tiene cuenta de Webex o está mal escrito: crear la cuenta primero. |
| `YA ESTABA` | Esa persona ya está en la sala; no es un error. |
| No ven los mensajes de los demás | Solo ven la sala quienes ya fueron agregados (`list-memberships.py`). |

> Los tokens son personales y vencen a las 12 horas: **no los guardes en archivos ni los subas al repositorio**.
