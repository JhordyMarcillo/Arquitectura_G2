/* Global helpers para cargar paneles y convertir via REST */
(function(){
  const BASE_URL = (window.APP_BASE_URL) || (window.location.origin + window.location.pathname.replace(/\/$/, ''));
  const API_BASE = (window.API_BASE_URL) ? window.API_BASE_URL + '/conversion' : window.API_BASE;

  window.loadPanel = async function(nombre){
    const url = (window.CONTEXT_PATH || '') + '/resources/jsp/' + nombre + '-tailwind.jsp';
    const cont = document.getElementById('panel-dynamic');
    if (!cont) return;
    const res = await fetch(url, {credentials:'same-origin'});
    const html = await res.text();
    cont.innerHTML = html;
  };

  const unitMap = {
    temperatura: { 'celsius':'celsius','fahrenheit':'fahrenheit','kelvin':'kelvin' },
    longitud: { 'metros':'metros','pies':'pies','pulgadas':'pulgadas','kilómetros':'kilometros','kilometros':'kilometros','millas':'millas' },
    peso: { 'kilogramos':'kilogramos','libras':'libras','gramos':'gramos','onzas':'onzas' }
  };

  function normalize(str){
    return (str||'').toString().trim().toLowerCase();
  }

  window.convertPanel = async function(categoria, idAmount, idFrom, idTo, idResult){
    try{
      const amount = parseFloat(document.getElementById(idAmount).value);
      const fromTxt = normalize(document.getElementById(idFrom).value);
      const toTxt = normalize(document.getElementById(idTo).value);
      if (isNaN(amount)) { setResult(idResult, 'Ingrese un número válido'); return; }
      const from = unitMap[categoria][fromTxt];
      const to = unitMap[categoria][toTxt];
      if (!from || !to) { setResult(idResult, 'Unidades no válidas'); return; }

      const body = { valor: amount, unidadOrigen: from, unidadDestino: to, categoria: categoria };
      const resp = await fetch(API_BASE + '/convertir', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body) });
      if (resp.status !== 200) { setResult(idResult, 'Error del servidor: ' + resp.status); return; }
      const data = await resp.json();
      if (data && data.exito) {
        setResult(idResult, (data.valorConvertido ?? data.resultado ?? 0).toFixed(4));
      } else {
        setResult(idResult, data && data.mensaje ? data.mensaje : 'Conversión no válida');
      }
    } catch(e){
      setResult(idResult, 'ERROR: Algo monstruoso pasó. Servidor inactivo.');
    }
  };

  function setResult(id, text){
    const el = document.getElementById(id);
    if (el) el.textContent = text;
  }
})();




