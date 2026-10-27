<%@ include file="fragmentos/inicio.jspf" %>

<div class="rejilla-3">
    <div class="tarjeta indicador">
        <div>
            <div class="etiqueta">Cuentas activas</div>
            <div class="valor">${activas}</div>
            <div class="nota">de ${cuentas.size()} cuentas registradas</div>
        </div>
        <span class="icono-caja azul">${iconos.banco}</span>
    </div>
    <div class="tarjeta indicador">
        <div>
            <div class="etiqueta">Saldo total en soles</div>
            <div class="valor">${totalSoles}</div>
            <div class="nota">Cuentas activas en S/</div>
        </div>
        <span class="icono-caja verde">S/</span>
    </div>
    <div class="tarjeta indicador">
        <div>
            <div class="etiqueta">Saldo total en dolares</div>
            <div class="valor">${totalDolares}</div>
            <div class="nota">Cuentas activas en US$</div>
        </div>
        <span class="icono-caja ambar">US$</span>
    </div>
</div>

<div class="tarjeta sin-relleno">
    <div class="titulo-tarjeta">
        <div>
            <h2>Listado de cuentas</h2>
            <p>Haga clic en una cuenta para ver sus movimientos.</p>
        </div>
        <a href="${pageContext.request.contextPath}/cuentas" class="boton secundario">${iconos.actualizar} Actualizar</a>
    </div>
    <table class="datos">
        <thead>
            <tr><th>CUENTA</th><th>CLIENTE</th><th>MONEDA</th><th class="derecha">SALDO</th><th>ESTADO</th></tr>
        </thead>
        <tbody>
        <c:forEach var="cuenta" items="${cuentas}">
            <tr class="enlace" onclick="location.href='${pageContext.request.contextPath}/movimientos?cuenta=${cuenta.codigo}'">
                <td class="negrita">${cuenta.codigo}</td>
                <td><c:out value="${cuenta.cliente}" /></td>
                <td>${cuenta.moneda}</td>
                <td class="derecha negrita">${cuenta.saldo}</td>
                <td><span class="etiqueta-estado ${cuenta.activa ? '' : 'inactiva'}">${cuenta.estado}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="fragmentos/fin.jspf" %>
