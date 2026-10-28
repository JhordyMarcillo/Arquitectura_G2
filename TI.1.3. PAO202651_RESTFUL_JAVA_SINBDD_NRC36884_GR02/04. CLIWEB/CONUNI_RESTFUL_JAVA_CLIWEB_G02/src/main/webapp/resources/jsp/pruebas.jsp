<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="ec.edu.monster.servicio.ConfiguracionCliente" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String apiBaseUrl = ConfiguracionCliente.getBaseUrl();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Pruebas - Monsters Inc. Client</title>
  <link rel="stylesheet" href="<c:url value='/resources/css/estilos.css'/>">
  <style> .box{max-width:800px;margin:40px auto;background:#fff;padding:20px;border:1px solid #ddd;border-radius:8px} pre{background:#f7f7f7;padding:12px;border-radius:6px} </style>
  <script>
    window.API_BASE_URL = '<%= apiBaseUrl %>';
  </script>
  </head>
<body>
  <div class="box">
    <h2>Pruebas del Cliente</h2>
    <p><a href="<c:url value='/pruebas/ping'/>">/pruebas/ping</a> (GET) verifica comunicación con el servicio REST.</p>
    <button onclick="probar()" class="btn" style="margin-top:10px">Probar fetch al /convertir (Celsius→Fahrenheit)</button>
    <pre id="out"></pre>
  </div>

  <script>
    async function probar(){
      const out = document.getElementById('out');
      out.textContent = 'Ejecutando...';
      try{
        const apiUrl = window.API_BASE_URL + '/conversion/convertir';
        const res = await fetch(apiUrl, {
          method:'POST', headers:{'Content-Type':'application/json'},
          body: JSON.stringify({ valor: 100, unidadOrigen: 'celsius', unidadDestino: 'fahrenheit', categoria: 'temperatura' })
        });
        const json = await res.json();
        out.textContent = JSON.stringify(json, null, 2);
      }catch(e){ out.textContent = 'Error: ' + e.message; }
    }
  </script>
</body>
</html>

