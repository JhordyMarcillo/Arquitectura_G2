# Inicio rápido — Cliente de consola

## Requisitos
- JDK 17 o superior y Maven (NetBeans ya trae Maven).
- Servidor encendido en `http://192.168.137.1:5014` (API en `/api/conversor`).

## Ejecutar
- NetBeans: File > Open Project > `CONUNI_CLIENTE_CONSOLA` > Run.
- Terminal: doble clic en `CONUNI_CLIENTE_CONSOLA/ejecutar.bat`, o `mvn package` y
  `java -jar target/CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar`.
- Otro servidor: `java -Dconuni.url=http://localhost:5014/api/conversor -jar ...`

## Credenciales
`MONSTER` / `Monster9` (las valida el servidor).
