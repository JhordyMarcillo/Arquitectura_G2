<%@ include file="fragmentos/inicio.jspf" %>

<div class="rejilla-2">
    <form method="post" action="${pageContext.request.contextPath}/operacion?tipo=${tipo}" class="tarjeta" style="padding:32px">
        <div class="encabezado-form">
            <span class="icono-caja azul" style="width:48px;height:48px">${iconos[tipo]}</span>
            <div>
                <h2>Registrar ${tipo}</h2>
                <p>${descripcion}</p>
            </div>
        </div>

        <div class="campo">
            <label for="cuenta">${esTransferencia ? 'Cuenta de origen' : 'Cuenta'}</label>
            <input id="cuenta" name="cuenta" value="<c:out value='${cuenta}' />" maxlength="8" pattern="\d{8}" title="8 digitos" autofocus required />
        </div>
        <c:if test="${tipo != 'deposito'}">
            <div class="campo">
                <label for="clave">Clave de la cuenta</label>
                <input id="clave" name="clave" type="password" maxlength="6" required />
            </div>
        </c:if>
        <c:if test="${esTransferencia}">
            <div class="campo">
                <label for="destino">Cuenta de destino</label>
                <input id="destino" name="cuentaDestino" value="<c:out value='${cuentaDestino}' />" maxlength="8" pattern="\d{8}" title="8 digitos" required />
            </div>
        </c:if>
        <div class="campo">
            <label for="importe">Importe</label>
            <input id="importe" name="importe" inputmode="decimal" placeholder="0.00" class="grande" required />
        </div>
        <div class="nota-info">
            ${iconos.info}
            <span>${esTransferencia
                ? 'Cada cuenta paga el costo por movimiento: S/ 2.00 en soles y US$ 0.60 en dolares.'
                : 'Se aplica el costo por movimiento: S/ 2.00 en cuentas en soles y US$ 0.60 en dolares.'}</span>
        </div>
        <button type="submit" class="boton primario ancho">Registrar ${tipo}</button>
    </form>

    <div class="tarjeta" style="padding:32px">
        <c:choose>
            <c:when test="${not empty resultado}">
                <div class="comprobante">
                    <div class="circulo exito">${iconos.check}</div>
                    <h3>Operacion exitosa</h3>
                    <p><c:out value="${resultado.mensaje}" /></p>
                    <dl class="detalle">
                        <c:forEach var="linea" items="${detalle}">
                            <div><dt>${linea.etiqueta}</dt><dd class="${linea.destacada ? 'destacado' : ''}"><c:out value="${linea.valor}" /></dd></div>
                        </c:forEach>
                    </dl>
                </div>
            </c:when>
            <c:when test="${not empty mensajeError}">
                <div class="comprobante">
                    <div class="circulo error">${iconos.x}</div>
                    <h3>Operacion rechazada</h3>
                    <p><c:out value="${mensajeError}" /></p>
                    <c:if test="${not empty codigoError}">
                        <dl class="detalle"><div><dt>Codigo</dt><dd>${codigoError}</dd></div></dl>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div class="vacio">
                    ${iconos.recibo}
                    <strong>El comprobante aparecera aqui</strong>
                    Complete el formulario y registre la operacion.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="fragmentos/fin.jspf" %>
