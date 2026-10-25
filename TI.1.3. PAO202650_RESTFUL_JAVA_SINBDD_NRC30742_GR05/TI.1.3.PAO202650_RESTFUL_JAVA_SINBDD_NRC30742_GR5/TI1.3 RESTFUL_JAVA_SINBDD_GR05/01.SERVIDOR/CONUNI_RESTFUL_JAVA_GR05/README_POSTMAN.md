# 🦕 RESTFUL JAVA MONSTER - Colección de Postman

## 📋 Descripción
Colección completa de Postman para probar el servicio RESTful de conversiones **Monsters Inc.** - Powered by Sullivan's Energy.

## 🚀 Instalación en Postman

### 1. Importar la Colección
1. Abre **Postman**
2. Haz clic en **Import** (botón azul arriba a la izquierda)
3. Selecciona **Upload Files**
4. Busca y selecciona: `RESTFULL_JAVA_MONSTER.postman_collection.json`
5. Haz clic en **Import**

### 2. Importar el Entorno
1. En Postman, haz clic en el ícono de **engranaje** (⚙️) arriba a la derecha
2. Selecciona **Import**
3. Busca y selecciona: `RESTFULL_JAVA_MONSTER.postman_environment.json`
4. Haz clic en **Import**

### 3. Seleccionar el Entorno
1. En la esquina superior derecha, haz clic en el dropdown de entornos
2. Selecciona **"RESTFUL JAVA MONSTER - Environment"**

## 📁 Estructura de la Colección

### 🏠 Información del Servicio
- **Información Base** - `GET /api/conversion/`
- **Info del Servicio** - `GET /api/conversion/info`
- **Lista de Endpoints** - `GET /api/conversion/endpoints`

### 🌡️ Conversiones de Temperatura (6 endpoints)
- Celsius ↔ Fahrenheit
- Celsius ↔ Kelvin  
- Fahrenheit ↔ Kelvin

### 📏 Conversiones de Longitud (6 endpoints)
- Metros ↔ Pies
- Metros ↔ Pulgadas
- Kilómetros ↔ Millas

### ⚖️ Conversiones de Peso/Masa (4 endpoints)
- Kilogramos ↔ Libras
- Gramos ↔ Onzas

### 🧪 Conversiones de Volumen (4 endpoints)
- Litros ↔ Galones
- Mililitros ↔ Onzas Fluidas

### 📐 Conversiones de Área (4 endpoints)
- Metros Cuadrados ↔ Pies Cuadrados
- Hectáreas ↔ Acres

### 🔄 Conversión Genérica (1 endpoint)
- **Conversión Personalizada** - `POST /api/conversion/convertir`

### 🧪 Ejemplos de Prueba (3 endpoints)
- Ejemplos listos con valores específicos para probar

## 🔧 Variables de Entorno Incluidas

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `base_url` | `http://localhost:8080/CONUNI_RESTFUL_JAVA_GR09` | URL base del servicio |
| `celsius_value` | `100` | Valor en Celsius para pruebas |
| `fahrenheit_value` | `212` | Valor en Fahrenheit para pruebas |
| `metros_value` | `1` | Valor en metros para pruebas |
| `pies_value` | `3.28084` | Valor en pies para pruebas |
| ... | ... | Y muchas más variables preconfiguradas |

## 🎯 Cómo Usar

### 1. Verificar que el Servicio esté Funcionando
1. Selecciona **"Información Base"** en la carpeta "🏠 Información del Servicio"
2. Haz clic en **Send**
3. Deberías ver información del servicio

### 2. Probar Conversiones Específicas
1. Ve a **"🌡️ Conversiones de Temperatura"**
2. Selecciona **"Celsius a Fahrenheit"**
3. Haz clic en **Send**
4. Deberías ver: `{"valorOriginal": 100, "resultado": 212, "unidadOrigen": "celsius", "unidadDestino": "fahrenheit", ...}`

### 3. Probar Conversión Genérica
1. Ve a **"🔄 Conversión Genérica"**
2. Selecciona **"Conversión Personalizada"**
3. En el **Body**, modifica el JSON si quieres:
```json
{
  "valor": 25,
  "unidadOrigen": "celsius",
  "unidadDestino": "fahrenheit",
  "categoria": "temperatura"
}
```
4. Haz clic en **Send**

### 4. Usar Ejemplos Predefinidos
1. Ve a **"🧪 Ejemplos de Prueba"**
2. Selecciona cualquier ejemplo
3. Haz clic en **Send**
4. Los valores ya están configurados para pruebas rápidas

## 📊 Respuestas Esperadas

### ✅ Respuesta Exitosa
```json
{
  "valorOriginal": 100,
  "resultado": 212,
  "unidadOrigen": "celsius",
  "unidadDestino": "fahrenheit",
  "categoria": "temperatura",
  "exitoso": true,
  "mensaje": "Conversión exitosa"
}
```

### ❌ Respuesta de Error
```json
{
  "valorOriginal": 0,
  "resultado": 0,
  "unidadOrigen": "",
  "unidadDestino": "",
  "categoria": "",
  "exitoso": false,
  "mensaje": "Error en la conversión: [descripción del error]"
}
```

## 🔍 Troubleshooting

### ❌ Error 404 - Not Found
- Verifica que Payara esté ejecutándose
- Verifica que el servicio esté desplegado correctamente
- Verifica la URL en la variable `base_url`

### ❌ Error 500 - Internal Server Error
- Revisa los logs de Payara
- Verifica que no haya errores de compilación
- Verifica que todas las dependencias estén correctas

### ❌ Error de Conexión
- Verifica que Payara esté ejecutándose en el puerto 8080
- Verifica que no haya firewall bloqueando la conexión
- Verifica la URL base

## 🎨 Características Especiales

### 🏷️ Variables Dinámicas
- Todas las URLs usan variables `{{variable_name}}`
- Fácil cambio de valores desde el entorno
- Valores predefinidos para pruebas rápidas

### 📝 Documentación Incluida
- Cada endpoint tiene descripción detallada
- Ejemplos de uso en cada request
- Valores esperados documentados

### 🧪 Ejemplos Listos
- Carpeta especial con ejemplos predefinidos
- Valores que producen resultados conocidos
- Fácil verificación de funcionamiento

## 🚀 Próximos Pasos

1. **Importa** ambos archivos en Postman
2. **Selecciona** el entorno correcto
3. **Verifica** que Payara esté ejecutándose
4. **Prueba** los endpoints de información primero
5. **Experimenta** con diferentes valores de conversión

---

**¡Disfruta probando tu servicio RESTful Monsters Inc.! 🦕⚡**

*Powered by Sullivan's Energy - ACER NITRO V15*


