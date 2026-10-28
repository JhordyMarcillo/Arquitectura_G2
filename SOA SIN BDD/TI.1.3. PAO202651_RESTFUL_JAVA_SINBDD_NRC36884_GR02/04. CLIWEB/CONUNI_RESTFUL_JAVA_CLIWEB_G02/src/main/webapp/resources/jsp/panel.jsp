<div class="grid">
  <div class="panel">
    <div id="temperatura" class="section active">
      <div class="title">Conversiones de Temperatura</div>
      <div class="desc">Convierte entre Celsius, Fahrenheit y Kelvin</div>
      <div class="row">
        <div class="fg"><label for="tempValue">Valor a convertir</label><input type="number" id="tempValue" step="0.01" placeholder="Ingrese la temperatura"></div>
        <div class="unit" id="unitBox">°C</div>
      </div>
      <div class="buttons">
        <button class="btn" onclick="convertirTemperatura('celsiusAFahrenheit')">°C → °F</button>
        <button class="btn" onclick="convertirTemperatura('fahrenheitACelsius')">°F → °C</button>
        <button class="btn" onclick="convertirTemperatura('celsiusAKelvin')">°C → K</button>
        <button class="btn" onclick="convertirTemperatura('kelvinACelsius')">K → °C</button>
        <button class="btn" onclick="convertirTemperatura('fahrenheitAKelvin')">°F → K</button>
        <button class="btn" onclick="convertirTemperatura('kelvinAFahrenheit')">K → °F</button>
      </div>
    </div>

    <div id="longitud" class="section">
      <div class="title">Conversiones de Longitud</div>
      <div class="desc">Metros, pies, pulgadas, kilómetros y millas</div>
      <div class="row"><div class="fg"><label for="lengthValue">Valor a convertir</label><input type="number" id="lengthValue" step="0.01"></div><div class="unit">m</div></div>
      <div class="buttons">
        <button class="btn" onclick="convertirLongitud('metrosAPies')">m → ft</button>
        <button class="btn" onclick="convertirLongitud('piesAMetros')">ft → m</button>
        <button class="btn" onclick="convertirLongitud('metrosAPulgadas')">m → in</button>
        <button class="btn" onclick="convertirLongitud('pulgadasAMetros')">in → m</button>
        <button class="btn" onclick="convertirLongitud('kilometrosAMillas')">km → mi</button>
        <button class="btn" onclick="convertirLongitud('millasAKilometros')">mi → km</button>
      </div>
    </div>

    <div id="peso" class="section">
      <div class="title">Conversiones de Peso/Masa</div>
      <div class="desc">Kilogramos, libras, gramos y onzas</div>
      <div class="row"><div class="fg"><label for="weightValue">Valor a convertir</label><input type="number" id="weightValue" step="0.01"></div><div class="unit">kg</div></div>
      <div class="buttons">
        <button class="btn" onclick="convertirPeso('kilogramosALibras')">kg → lb</button>
        <button class="btn" onclick="convertirPeso('librasAKilogramos')">lb → kg</button>
        <button class="btn" onclick="convertirPeso('gramosAOnzas')">g → oz</button>
        <button class="btn" onclick="convertirPeso('onzasAGramos')">oz → g</button>
      </div>
    </div>

      <div class="row"><div class="fg"><label for="volumeValue">Valor a convertir</label><input type="number" id="volumeValue" step="0.01"></div><div class="unit">L</div></div>
      <div class="buttons">
      </div>
    </div>

      <div class="title">Conversiones de Área</div>
      <div class="buttons">
      </div>
    </div>
  </div>

  <div class="panel">
    <div id="results" class="res hidden">
      <div class="title" id="resTitle">Resultado</div>
      <div class="value" id="resValue">0.0000</div>
      <button class="btn" style="background:var(--sullivan-gray)" onclick="toggleDebug()"><i class="fas fa-code"></i> Ver detalles técnicos</button>
      <pre id="debug" class="res hidden" style="margin-top:10px"></pre>
    </div>
  </div>
</div>


