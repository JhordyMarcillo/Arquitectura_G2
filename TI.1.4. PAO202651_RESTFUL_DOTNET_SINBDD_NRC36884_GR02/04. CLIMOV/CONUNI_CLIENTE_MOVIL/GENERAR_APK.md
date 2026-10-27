# 📦 Guía para Generar el APK

## 🎯 Dos Métodos: Debug y Release

---

## Método 1: APK Debug (Rápido y Fácil) ⚡

### Opción A: Desde Android Studio (GUI)

1. **Abrir el proyecto en Android Studio**
   - Abre Android Studio
   - `File` → `Open`
   - Selecciona la carpeta del proyecto

2. **Esperar sincronización de Gradle**
   - Espera a que Gradle termine de sincronizar
   - Si hay errores, haz clic en `Sync Now`

3. **Generar APK Debug**
   - En el menú: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - Espera a que termine (verás una notificación)
   - Haz clic en `locate` en la notificación

4. **Ubicación del APK**
   ```
   app\build\outputs\apk\debug\app-debug.apk
   ```

### Opción B: Desde Terminal/PowerShell

```powershell
# Navega a la carpeta del proyecto
cd "C:\Users\david\Documents\36884 ARQUITECTURA DE SOFTWARE\Arquitectura_G2\TI.1.4. PAO202651_RESTFUL_DOTNET_SINBDD_NRC36884_GR02\04. CLIMOV\CONUNI_CLIENTE_MOVIL"

# Genera el APK
.\gradlew assembleDebug
```

**El APK estará en:**
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## Método 2: APK Release (Firmado) 🔐

### Paso 1: Crear Keystore (Solo la primera vez)

```powershell
# Navega a la carpeta del proyecto
cd "C:\Users\david\Documents\36884 ARQUITECTURA DE SOFTWARE\Arquitectura_G2\TI.1.4. PAO202651_RESTFUL_DOTNET_SINBDD_NRC36884_GR02\04. CLIMOV\CONUNI_CLIENTE_MOVIL"

# Crea el keystore
keytool -genkey -v -keystore mi-aplicacion.keystore -alias mi-app-alias -keyalg RSA -keysize 2048 -validity 10000
```

**Te pedirá:**
- Password del keystore (ej: `Monster123`)
- Tu nombre y apellido
- Organización (ej: `MONSTER`)
- Ciudad, estado, país
- Confirmar datos

**IMPORTANTE:** ⚠️ Guarda el password y el alias, los necesitarás después.

### Paso 2: Configurar el Signing en build.gradle.kts

Abre `app/build.gradle.kts` y agrega antes de `buildTypes`:

```kotlin
android {
    // ... código existente ...
    
    signingConfigs {
        create("release") {
            storeFile = file("../mi-aplicacion.keystore")
            storePassword = "Monster123"  // Tu password
            keyAlias = "mi-app-alias"
            keyPassword = "Monster123"    // Tu password
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### Paso 3: Generar APK Release

**Desde Android Studio:**
1. `Build` → `Generate Signed Bundle / APK`
2. Selecciona `APK` → `Next`
3. Selecciona tu keystore o crea uno nuevo
4. Ingresa los passwords
5. Selecciona `release` build variant
6. `Finish`

**Desde Terminal:**
```powershell
.\gradlew assembleRelease
```

**El APK estará en:**
```
app\build\outputs\apk\release\app-release.apk
```

---

## 📲 Instalar el APK en tu Celular

### Método 1: Por USB (ADB)

```powershell
# Instalar directamente
adb install app\build\outputs\apk\debug\app-debug.apk

# O si ya está instalado (reinstalar)
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Método 2: Transferir el archivo

1. **Copia el APK a tu celular:**
   - Conecta por USB y copia el archivo
   - O envíalo por WhatsApp/Email
   - O súbelo a Google Drive

2. **En el celular:**
   - Abre el archivo APK
   - Android pedirá permiso para instalar apps desconocidas
   - Activa "Permitir desde esta fuente"
   - Instala la aplicación

---

## 🎨 Personalizar el APK

### Cambiar el nombre de la aplicación

Edita `app/src/main/res/values/strings.xml`:
```xml
<resources>
    <string name="app_name">Conversor MONSTER</string>
</resources>
```

### Cambiar el icono de la app

Reemplaza los iconos en:
```
app/src/main/res/mipmap-hdpi/
app/src/main/res/mipmap-mdpi/
app/src/main/res/mipmap-xhdpi/
app/src/main/res/mipmap-xxhdpi/
app/src/main/res/mipmap-xxxhdpi/
```

O usa **Image Asset Studio**:
1. Click derecho en `res` → `New` → `Image Asset`
2. Selecciona tu imagen
3. Ajusta y genera

### Cambiar versión y número de versión

Edita `app/build.gradle.kts`:
```kotlin
defaultConfig {
    applicationId = "ec.edu.monster"
    minSdk = 24
    targetSdk = 34
    versionCode = 2        // Incrementa este número
    versionName = "1.1"    // Versión visible para usuarios
    // ...
}
```

---

## 🔍 Verificar el APK

### Ver información del APK

```powershell
# Ver información básica
aapt dump badging app\build\outputs\apk\debug\app-debug.apk

# Ver permisos
aapt dump permissions app\build\outputs\apk\debug\app-debug.apk
```

### Probar antes de distribuir

1. **Desinstala la versión anterior** (si existe)
2. **Instala el APK** en varios dispositivos
3. **Prueba todas las funcionalidades:**
   - Login
   - Navegación
   - Conversiones
   - Botones de volver y salir

---

## ⚠️ Solución de Problemas

### Error: "Cannot resolve symbol R"

```powershell
# Limpia el proyecto
.\gradlew clean

# O en Android Studio
Build → Clean Project → Build → Rebuild Project
```

### Error: "Gradle sync failed"

1. `File` → `Invalidate Caches` → `Invalidate and Restart`
2. Espera que sincronice de nuevo

### Error: "Java heap space"

Edita `gradle.properties` y agrega:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
```

### Error al instalar APK: "App not installed"

1. Desinstala la versión anterior primero
2. O usa: `adb install -r app-debug.apk`

### APK muy grande

En `build.gradle.kts`, activa minify:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true  // Cambia a true
        isShrinkResources = true // Agrega esto
        // ...
    }
}
```

---

## 📊 Tamaños Esperados

- **Debug APK:** ~5-15 MB
- **Release APK (sin minify):** ~5-12 MB
- **Release APK (con minify):** ~3-8 MB

---

## 📤 Distribuir tu APK

### Opción 1: Compartir directamente
- Envía el APK por WhatsApp, Email, etc.
- Usuarios deben activar "Instalar apps desconocidas"

### Opción 2: Google Play Store (Producción)
1. Crea una cuenta de desarrollador ($25 único pago)
2. Genera un **Bundle** en lugar de APK:
   ```powershell
   .\gradlew bundleRelease
   ```
3. Sube el `.aab` a Play Console

### Opción 3: Plataformas alternativas
- **APKPure**
- **Aptoide**
- **Amazon AppStore**

---

## ✅ Checklist Final

Antes de distribuir:

- [ ] Probado en al menos 2 dispositivos diferentes
- [ ] Login funciona correctamente
- [ ] Todas las conversiones funcionan
- [ ] Servidor REST está disponible (o cambiar IP)
- [ ] Icono de la app se ve bien
- [ ] Nombre de la app es correcto
- [ ] Versión incrementada
- [ ] Sin errores en Logcat
- [ ] APK firmado (para release)
- [ ] Tamaño razonable

---

## 🚀 Comando Rápido (Todo en Uno)

```powershell
# Limpia, compila y genera APK debug
cd "C:\Users\david\Documents\36884 ARQUITECTURA DE SOFTWARE\Arquitectura_G2\TI.1.4. PAO202651_RESTFUL_DOTNET_SINBDD_NRC36884_GR02\04. CLIMOV\CONUNI_CLIENTE_MOVIL"
.\gradlew clean assembleDebug
explorer app\build\outputs\apk\debug
```

---

## 📝 Resumen de Comandos Útiles

```powershell
# Limpiar proyecto
.\gradlew clean

# Ver todas las tareas disponibles
.\gradlew tasks

# Generar APK debug
.\gradlew assembleDebug

# Generar APK release
.\gradlew assembleRelease

# Generar Bundle (para Play Store)
.\gradlew bundleRelease

# Instalar directamente en dispositivo conectado
.\gradlew installDebug

# Desinstalar de dispositivo
.\gradlew uninstallDebug

# Ver dependencias
.\gradlew dependencies
```

---

¡Tu APK está listo para ser compartido! 🎉
