<%@ include file="fragmentos/inicio.jspf" %>

<form method="get" action="${pageContext.request.contextPath}/movimientos" class="tarjeta busqueda" style="margin-bottom:24px">
    <div class="campo">
        <label for="cuenta">Numero de cuenta</label>
        <input id="cuenta" name="cuenta" value="<c:out value='${cuenta}' />" maxlength="8" pattern="\d{8}" title="8 digitos" autofocus required />
    </div>
    <button type="submit" class="boton primario">${iconos.buscar} Consultar</button>
</form>

<c:choose>
    <c:when test="${empty datos}">
        <c:if test="${empty mensajeError}">
            <div class="tarjeta vacio">
                ${iconos.historial}
                <strong>Ingrese una cuenta para ver su historial</strong>
                Por ejemplo 00100001 o 00200001.
            </div>
        </c:if>
    </c:when>
    <c:otherwise>
        <div class="rejilla-3">
            <div class="tarjeta indicador destacada">
                <div>
                    <div class="etiqueta">Cuenta ${datos.cuenta} · ${datos.moneda}</div>
                    <div class="valor">${saldo}</div>
                    <div class="nota">Saldo disponible</div>
                </div>
            </div>
            <div class="tarjeta indicador">
                <div>
                    <div class="etiqueta">Total ingresos</div>
                    <div class="valor ingreso">${ingresos}</div>
                    <div class="nota">${cantidadIngresos} movimientos</div>
                </div>
            </div>
            <div class="tarjeta indicador">
                <div>
                    <div class="etiqueta">Total salidas</div>
                    <div class="valor salida">${salidas}</div>
                    <div class="nota">${cantidadSalidas} movimientos</div>
                </div>
            </div>
        </div>

        <div class="tarjeta sin-relleno">
            <table class="datos">
                <thead>
                    <tr><th>N°</th><th>FECHA</th><th>DESCRIPCION</th><th>REFERENCIA</th><th class="derecha">IMPORTE</th></tr>
                </thead>
                <tbody>
                <c:forEach var="mov" items="${movimientos}">
                    <c:set var="clase" value="${mov.ingreso ? 'ingreso' : 'salida'}" />
                    <tr>
                        <td class="secundario">${mov.numero}</td>
                        <td>${mov.fecha}</td>
                        <td><span class="flecha ${clase}">${mov.ingreso ? iconos.arriba : iconos.abajo}</span><c:out value="${mov.descripcion}" /></td>
                        <td class="secundario">${mov.referencia}</td>
                        <td class="derecha ${clase}">${mov.importe}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="fragmentos/fin.jspf" %>
