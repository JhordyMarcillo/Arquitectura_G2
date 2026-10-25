const BASE_URL = window.API_BASE_URL || 'http://192.168.100.2:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/convertir';
const unitBox = document.getElementById('unitBox');
const res = document.getElementById('results');
const resValue = document.getElementById('resValue');
const debug = document.getElementById('debug');

const units = { temperatura:'°C', longitud:'m', peso:'kg' };
document.querySelectorAll('.category-buttons .btn').forEach(b=>{
  b.addEventListener('click',()=>{
    document.querySelectorAll('.category-buttons .btn').forEach(x=>x.classList.remove('active'));
    b.classList.add('active');
    const cat = b.dataset.cat;
    document.querySelectorAll('.section').forEach(s=>s.classList.remove('active'));
    document.getElementById(cat).classList.add('active');
    unitBox.textContent = units[cat] || '°C';
    res.classList.add('hidden');
  })
});

window.toggleDebug = () => { debug.classList.toggle('hidden'); };

function mapOp(op){
  const m={celsiusAFahrenheit:['celsius','fahrenheit'],fahrenheitACelsius:['fahrenheit','celsius'],celsiusAKelvin:['celsius','kelvin'],kelvinACelsius:['kelvin','celsius'],fahrenheitAKelvin:['fahrenheit','kelvin'],kelvinAFahrenheit:['kelvin','fahrenheit'],metrosAPies:['metros','pies'],piesAMetros:['pies','metros'],metrosAPulgadas:['metros','pulgadas'],pulgadasAMetros:['pulgadas','metros'],kilometrosAMillas:['kilometros','millas'],millasAKilometros:['millas','kilometros'],kilogramosALibras:['kilogramos','libras'],librasAKilogramos:['libras','kilogramos'],gramosAOnzas:['gramos','onzas'],onzasAGramos:['onzas','gramos'],litrosAGalones:['litros','galones'],galonesALitros:['galones','litros'],mililitrosAOnzasFluidas:['mililitros','onzasFluidas'],onzasFluidasAMililitros:['onzasFluidas','mililitros'],metrosCuadradosAPiesCuadrados:['metrosCuadrados','piesCuadrados'],piesCuadradosAMetrosCuadrados:['piesCuadrados','metrosCuadrados'],hectareasAAcres:['hectareas','acres'],acresAHectareas:['acres','hectareas']};
  return m[op];
}

async function convertir(op, valor, categoria){
  if (valor==='' || valor===null || isNaN(valor)) { pintaError('Por favor, ingrese un valor numérico válido'); return; }
  pintaLoading(`${valor} → ...`);
  const [u1,u2] = mapOp(op);
  try{
    const r = await fetch(BASE_URL,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({valor:parseFloat(valor),unidadOrigen:u1,unidadDestino:u2,categoria})});
    const data = await r.json();
    if (r.ok && data.exito){
      pintaOk(`${data.valorOriginal} ${simbolo(data.unidadOrigen)} → ${data.valorConvertido.toFixed(4)} ${simbolo(data.unidadDestino)}`, JSON.stringify(data,null,2));
    } else {
      pintaError(data.mensaje || `Algo monstruoso pasó. Código ${r.status}.`, JSON.stringify(data,null,2));
    }
  }catch(e){
    pintaError('Algo monstruoso pasó. El servidor está temporalmente inactivo. Inténtalo más tarde.');
  }
}

function simbolo(u){const s={celsius:'°C',fahrenheit:'°F',kelvin:'K',metros:'m',pies:'ft',pulgadas:'in',kilometros:'km',millas:'mi',kilogramos:'kg',libras:'lb',gramos:'g',onzas:'oz',litros:'L',galones:'gal',mililitros:'mL',onzasFluidas:'fl oz',metrosCuadrados:'m²',piesCuadrados:'ft²',hectareas:'ha',acres:'ac'};return s[u]||u;}
function pintaLoading(txt){res.className='res loading';res.classList.remove('hidden');resValue.textContent=txt;debug.classList.add('hidden');}
function pintaOk(txt,dbg){res.className='res success';res.classList.remove('hidden');resValue.textContent=txt;if(dbg){debug.textContent=dbg;}}
function pintaError(msg,dbg){res.className='res error';res.classList.remove('hidden');resValue.textContent=msg;if(dbg){debug.textContent=dbg;}}

window.convertirTemperatura = (op)=> convertir(op, document.getElementById('tempValue').value, 'temperatura');
window.convertirLongitud    = (op)=> convertir(op, document.getElementById('lengthValue').value, 'longitud');
window.convertirPeso        = (op)=> convertir(op, document.getElementById('weightValue').value, 'peso');





