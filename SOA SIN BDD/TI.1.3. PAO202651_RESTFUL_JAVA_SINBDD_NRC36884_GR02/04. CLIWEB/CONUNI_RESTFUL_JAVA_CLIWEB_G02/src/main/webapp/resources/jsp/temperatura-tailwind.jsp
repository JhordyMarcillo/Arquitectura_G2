<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="w-full max-w-4xl rounded-xl bg-white border border-gray-200 p-6 sm:p-8 flex flex-col gap-6 shadow-xl shadow-gray-200/50">
  <div class="flex flex-col md:flex-row gap-4">
    <div class="flex-1">
      <label class="block text-sm font-medium text-slate-500 pb-2" for="tempAmount">Cantidad</label>
      <input id="tempAmount" class="form-input w-full rounded-lg border border-gray-300 bg-gray-50 h-14 p-4" type="number" step="0.01" placeholder="100" />
    </div>
    <div class="flex-1">
      <label class="block text-sm font-medium text-slate-500 pb-2" for="tempFrom">De</label>
      <select id="tempFrom" class="form-input w-full rounded-lg border border-gray-300 bg-gray-50 h-14 p-4">
        <option>Celsius</option>
        <option>Fahrenheit</option>
        <option>Kelvin</option>
      </select>
    </div>
    <div class="flex-1">
      <label class="block text-sm font-medium text-slate-500 pb-2" for="tempTo">A</label>
      <select id="tempTo" class="form-input w-full rounded-lg border border-gray-300 bg-gray-50 h-14 p-4">
        <option>Fahrenheit</option>
        <option>Celsius</option>
        <option>Kelvin</option>
      </select>
    </div>
  </div>
  <button class="flex w-full items-center justify-center rounded-lg h-12 px-6 text-white font-bold bg-[#3ab4d9] hover:opacity-90"
          onclick="convertPanel('temperatura','tempAmount','tempFrom','tempTo','tempResult')">Convertir</button>
  <div class="w-full rounded-lg bg-[#afe0f8]/20 p-6 text-center">
    <p class="text-slate-600 text-lg">Valor convertido</p>
    <p id="tempResult" class="text-[#3ab4d9] text-4xl font-bold mt-2">--</p>
  </div>
</div>



