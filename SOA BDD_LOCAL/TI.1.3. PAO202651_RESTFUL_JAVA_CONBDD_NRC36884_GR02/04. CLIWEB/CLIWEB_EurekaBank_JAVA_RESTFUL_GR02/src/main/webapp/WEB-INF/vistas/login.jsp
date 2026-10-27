<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Iniciar sesion · EurekaBank</title>
    <link rel="icon" href="${pageContext.request.contextPath}/recursos/img/logo.svg" type="image/svg+xml" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/eurekabank.css" />
</head>
<body>
<div class="acceso">
    <section class="acceso-panel">
        <div class="marca" style="padding:0">
            <img src="${pageContext.request.contextPath}/recursos/img/logo.svg" alt="" />
            <div><strong>EurekaBank</strong><span style="color:#cbd5e1">Sistema de cuentas de ahorro</span></div>
        </div>
        <h1>Banca segura,<br />siempre a la mano.</h1>
        <p>Consulte cuentas y movimientos, y registre depositos, retiros y transferencias en tiempo real.</p>
        <ul>
            <li>${iconos.check} Operaciones transaccionales con validacion de saldo</li>
            <li>${iconos.check} Sesiones protegidas con token</li>
            <li>${iconos.check} Compatible con cualquier servidor del grupo</li>
        </ul>
        <footer>RESTful Java · GR02</footer>
    </section>

    <section class="acceso-formulario">
        <form method="post" action="${pageContext.request.contextPath}/login" class="tarjeta">
            <h2>Bienvenido</h2>
            <p class="subtitulo">Ingrese con su usuario de empleado.</p>

            <c:if test="${not empty aviso}">
                <div class="alerta aviso">${iconos.info} <span><c:out value="${aviso}" /></span></div>
            </c:if>
            <c:if test="${not empty mensajeError}">
                <div class="alerta error">${iconos.alerta} <span><c:out value="${mensajeError}" /></span></div>
            </c:if>

            <div class="campo">
                <label for="usuario">Usuario</label>
                <input id="usuario" name="usuario" value="<c:out value='${usuarioIngresado}' />" autocomplete="username" autofocus required />
            </div>
            <div class="campo">
                <label for="clave">Clave</label>
                <input id="clave" name="clave" type="password" autocomplete="current-password" required />
            </div>
            <div class="campo">
                <label for="servidor">Servidor</label>
                <input id="servidor" name="urlServidor" value="<c:out value='${urlServidor}' />" class="tenue" required />
            </div>
            <button type="submit" class="boton primario ancho" style="margin-top:8px">Ingresar</button>
            <p class="pie-prueba">Usuario de prueba: MONSTER / Monster9</p>
        </form>
    </section>
</div>
</body>
</html>
