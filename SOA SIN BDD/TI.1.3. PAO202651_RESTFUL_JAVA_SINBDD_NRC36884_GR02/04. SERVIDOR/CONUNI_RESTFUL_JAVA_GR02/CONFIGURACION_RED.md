# 🌐 CONFIGURACIÓN DE RED - Servicio RESTful Monsters Inc.

## 📡 **CONFIGURACIÓN PARA ACCESO DESDE OTRAS COMPUTADORAS**

### **1. 🔍 Obtener tu IP de red:**
```bash
# En Windows (PowerShell):
ipconfig

# Busca "Adaptador de LAN inalámbrica Wi-Fi" o "Adaptador Ethernet"
# Anota la "Dirección IPv4" (ejemplo: 192.168.1.100)
```

### **2. 🌐 URLs para diferentes entornos:**

#### **🏠 Desarrollo Local:**
```
http://localhost:8080/CONUNI_RESTFUL_JAVA_GR09
```

#### **🏢 Red Local (otras computadoras):**
```
http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09
```
*Cambia `192.168.1.100` por tu IP real*

#### **🌍 Internet (si tienes IP pública):**
```
http://TU_IP_PUBLICA:8080/CONUNI_RESTFUL_JAVA_GR09
```

## 📱 **CLIENTES QUE PUEDEN CONSUMIR EL SERVICIO:**

### **💻 Aplicaciones Web (JavaScript/HTML):**
```javascript
// Ejemplo de consumo desde una página web
fetch('http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/temperatura/celsius-to-fahrenheit?celsius=100')
  .then(response => response.json())
  .then(data => console.log(data));
```

### **📱 Aplicaciones Móviles (Android/iOS):**
```java
// Android - Java
String url = "http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/temperatura/celsius-to-fahrenheit?celsius=100";
// Usar OkHttp, Retrofit, etc.
```

```swift
// iOS - Swift
let url = URL(string: "http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/temperatura/celsius-to-fahrenheit?celsius=100")!
// Usar URLSession, Alamofire, etc.
```

### **🖥️ Aplicaciones Desktop (Java, C#, Python, etc.):**
```java
// Java Desktop
String url = "http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/temperatura/celsius-to-fahrenheit?celsius=100";
```

```csharp
// C# Desktop
string url = "http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/temperatura/celsius-to-fahrenheit?celsius=100";
```

## 🔧 **CONFIGURACIÓN DE PAYARA:**

### **1. Configurar Payara para acceso de red:**
1. Abre **Payara Admin Console**: `http://localhost:4848`
2. Ve a **Configurations** → **server-config** → **Network Config** → **Network Listeners**
3. Selecciona **http-listener-1**
4. En **Address**, cambia de `127.0.0.1` a `0.0.0.0`
5. **Save** y **Restart** Payara

### **2. Verificar que el puerto esté abierto:**
```bash
# Verificar que el puerto 8080 esté escuchando
netstat -an | findstr :8080
```

## 🛡️ **CONFIGURACIÓN DE FIREWALL:**

### **Windows Firewall:**
1. **Windows Defender Firewall** → **Configuración avanzada**
2. **Reglas de entrada** → **Nueva regla**
3. **Puerto** → **TCP** → **8080** → **Permitir conexión**
4. **Aplicar a**: Todas las redes

### **Router (si es necesario):**
- Configurar **Port Forwarding** del puerto 8080 a tu IP local

## 📋 **NOMBRES DEL SERVICIO:**

### **✅ El nombre del servicio SÍ importa:**
- **Nombre del proyecto**: `CONUNI_RESTFUL_JAVA_GR09`
- **URL completa**: `http://IP:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/...`
- **Los clientes DEBEN usar el nombre completo**

### **🔄 Si cambias el nombre del proyecto:**
1. **Redeploy** en Payara
2. **Actualizar URLs** en todos los clientes
3. **Actualizar variables** en Postman

## 🚀 **EJEMPLOS DE CONSUMO:**

### **📱 App Móvil Android:**
```java
public class ConversionService {
    private static final String BASE_URL = "http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion";
    
    public void convertCelsiusToFahrenheit(double celsius, Callback callback) {
        String url = BASE_URL + "/temperatura/celsius-to-fahrenheit?celsius=" + celsius;
        // Hacer request HTTP...
    }
}
```

### **💻 App Web (JavaScript):**
```javascript
class MonsterConverter {
    constructor(baseUrl) {
        this.baseUrl = baseUrl || 'http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion';
    }
    
    async celsiusToFahrenheit(celsius) {
        const response = await fetch(`${this.baseUrl}/temperatura/celsius-to-fahrenheit?celsius=${celsius}`);
        return await response.json();
    }
}
```

### **🖥️ App Desktop (Java Swing):**
```java
public class ConversionClient {
    private static final String BASE_URL = "http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion";
    
    public ConversionResponse convertTemperature(double value, String from, String to) {
        String url = BASE_URL + "/temperatura/" + from + "-to-" + to + "?" + from + "=" + value;
        // Usar HttpURLConnection o HttpClient...
    }
}
```

## 🔍 **VERIFICACIÓN:**

### **1. Desde otra computadora:**
```bash
# Probar conectividad
ping 192.168.1.100

# Probar el servicio
curl http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/info
```

### **2. Desde móvil (misma red WiFi):**
- Abrir navegador
- Ir a: `http://192.168.1.100:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/info`

## ⚠️ **CONSIDERACIONES IMPORTANTES:**

### **🔒 Seguridad:**
- **Solo para red local** (recomendado para desarrollo)
- **No exponer a Internet** sin autenticación
- **Usar HTTPS** en producción

### **📊 Rendimiento:**
- **Una instancia** puede manejar múltiples clientes
- **Payara** maneja conexiones concurrentes automáticamente
- **Escalable** horizontalmente

### **🔄 Mantenimiento:**
- **Mismo nombre** del servicio en todos los clientes
- **URLs consistentes** en toda la aplicación
- **Versionado** del API si es necesario

---

**¡Tu servicio puede ser consumido por cualquier dispositivo en la red!** 🦕🌐

¿Quieres que te ayude a configurar algún tipo específico de cliente o necesitas ayuda con la configuración de red?
