# 🎨 Abrir Proyecto en Android Studio

## 📋 Pasos para abrir y ejecutar en Android Studio

### 1. Abrir Android Studio

1. Inicia **Android Studio**
2. Si se abre con otro proyecto, ciérralo: `File` → `Close Project`

### 2. Abrir el Proyecto

**Opción A: Desde la pantalla de bienvenida**
1. Clic en `Open`
2. Navega a:
   ```
   C:\Users\david\Documents\36884 ARQUITECTURA DE SOFTWARE\Arquitectura_G2\TI.1.4. PAO202651_RESTFUL_DOTNET_SINBDD_NRC36884_GR02\04. CLIMOV\CONUNI_CLIENTE_MOVIL
   ```
3. Selecciona la carpeta y haz clic en `OK`

**Opción B: Desde el menú**
1. `File` → `Open...`
2. Selecciona la carpeta del proyecto
3. Clic en `OK`

### 3. Esperar la Sincronización de Gradle

- Android Studio automáticamente sincronizará Gradle
- Verás una barra de progreso en la parte inferior
- **IMPORTANTE:** Espera a que termine completamente (puede tomar 2-5 minutos la primera vez)
- Si aparece "Gradle sync failed", haz clic en `Sync Now`

### 4. Configurar el Dispositivo

**Opción A: Dispositivo Físico (Recomendado)**

1. **Preparar el celular:**
   - Ve a `Ajustes` → `Acerca del teléfono`
   - Toca 7 veces en "Número de compilación"
   - Regresa y entra a `Opciones de desarrollador`
   - Activa `Depuración USB`

2. **Conectar:**
   - Conecta tu celular con cable USB
   - Acepta el mensaje de "Permitir depuración USB" en el celular
   - En Android Studio, verás tu dispositivo en la barra superior

3. **Verificar conexión:**
   - Abre PowerShell:
   ```powershell
   adb devices
   ```
   - Deberías ver tu dispositivo listado

**Opción B: Emulador Android**

1. Clic en `Device Manager` (icono de teléfono en la barra lateral)
2. Si no tienes emulador, clic en `Create Device`
3. Selecciona un dispositivo (ej: Pixel 6)
4. Selecciona una imagen del sistema (ej: Android 13/Tiramisu)
5. Descarga si es necesario
6. Clic en `Finish`

### 5. Ejecutar la Aplicación

1. **Selecciona el dispositivo** en la barra superior
2. Haz clic en el **botón verde de Play** (▶) o presiona `Shift + F10`
3. Android Studio compilará e instalará la app
4. La app se abrirá automáticamente en tu dispositivo

### 6. Ver los Logs (Logcat)

1. En la parte inferior, clic en la pestaña `Logcat`
2. Aquí verás todos los logs de la aplicación
3. Puedes filtrar por:
   - `Tag`: ec.edu.monster
   - `Level`: Error, Warning, Info, etc.

---

## 🎯 Estructura del Proyecto en Android Studio

```
📁 CONUNI_CLIENTE_MOVIL
├── 📁 app
│   ├── 📁 src
│   │   ├── 📁 main
│   │   │   ├── 📁 java/ec/edu/monster
│   │   │   │   ├── 📁 controlador
│   │   │   │   │   └── ApiService.java          ← Conexión API
│   │   │   │   ├── 📁 modelo
│   │   │   │   │   ├── ConversionRequest.java   ← Request
│   │   │   │   │   └── ConversionResponse.java  ← Response
│   │   │   │   └── 📁 vista
│   │   │   │       ├── LoginActivity.java       ← Pantalla login
│   │   │   │       ├── MainActivity.java        ← Menú principal
│   │   │   │       └── TransformacionesActivity.java ← Conversiones
│   │   │   ├── 📁 res
│   │   │   │   ├── 📁 drawable                  ← Iconos y fondos
│   │   │   │   ├── 📁 layout                    ← Diseños XML
│   │   │   │   ├── 📁 values                    ← Colores, strings
│   │   │   │   └── 📁 xml                       ← Configuraciones
│   │   │   └── AndroidManifest.xml              ← Configuración app
│   │   └── 📁 build                            ← APKs generados
│   └── build.gradle.kts                         ← Dependencias
├── build.gradle.kts                             ← Config proyecto
└── settings.gradle.kts                          ← Módulos
```

---

## 🔧 Atajos Útiles en Android Studio

| Acción | Atajo |
|--------|-------|
| Ejecutar app | `Shift + F10` |
| Buscar archivo | `Ctrl + Shift + N` |
| Buscar en archivos | `Ctrl + Shift + F` |
| Formatear código | `Ctrl + Alt + L` |
| Importar automático | `Alt + Enter` |
| Comentar línea | `Ctrl + /` |
| Duplicar línea | `Ctrl + D` |
| Eliminar línea | `Ctrl + Y` |
| Ir a definición | `Ctrl + B` |
| Buscar referencias | `Alt + F7` |

---

## 🐛 Depuración (Debug)

### Poner un Breakpoint

1. Haz clic en el margen izquierdo de una línea de código
2. Aparecerá un círculo rojo
3. Cuando ejecutes en modo debug, el código se detendrá ahí

### Ejecutar en Modo Debug

1. Clic en el botón de **Debug** (🐞) o presiona `Shift + F9`
2. La app se detendrá en tus breakpoints
3. Puedes ver valores de variables
4. Usar los controles:
   - **Step Over** (F8): Ejecuta línea actual
   - **Step Into** (F7): Entra en métodos
   - **Step Out** (Shift + F8): Sale del método
   - **Resume** (F9): Continúa ejecución

---

## 📝 Editar Código

### Modificar la IP del Servidor

1. Abre `ApiService.java`
2. Busca la línea:
   ```java
   private static final String BASE_URL = "http://192.168.137.1:5014/api/conversor";
   ```
3. Cambia la IP si es necesario
4. Guarda (`Ctrl + S`)
5. Ejecuta de nuevo

### Cambiar colores del tema

1. Abre `res/values/colors.xml`
2. Modifica los valores hexadecimales
3. Los cambios se reflejan al ejecutar

---

## ⚡ Sincronizar Cambios

Después de editar archivos:

1. **Si editaste Java:**
   - Guarda (`Ctrl + S`)
   - Build → Make Project (`Ctrl + F9`)

2. **Si editaste XML:**
   - Guarda
   - Los cambios se aplican automáticamente

3. **Si editaste build.gradle.kts:**
   - Aparecerá banner "Gradle files have changed"
   - Clic en `Sync Now`

---

## 🎨 Diseño Visual (Layout Editor)

1. Abre cualquier archivo XML de `layout/`
2. Verás 3 pestañas arriba:
   - **Code**: Código XML
   - **Split**: Código + Vista previa
   - **Design**: Vista visual

3. En modo **Design**:
   - Arrastra componentes desde la paleta
   - Edita propiedades en el panel derecho
   - Vista previa en diferentes dispositivos

---

## 📦 Generar APK desde Android Studio

### APK Debug (Rápido)

1. `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. Espera a que termine
3. Clic en `locate` en la notificación
4. El APK está en: `app/build/outputs/apk/debug/`

### APK Release (Firmado)

1. `Build` → `Generate Signed Bundle / APK...`
2. Selecciona `APK` → `Next`
3. Completa los datos del keystore
4. Selecciona `release` → `Finish`
5. El APK está en: `app/build/outputs/apk/release/`

---

## 🔍 Ver Estructura del Proyecto

**Panel izquierdo (Project)** - Cambia la vista:

1. **Android**: Vista simplificada (por defecto)
2. **Project**: Estructura real de archivos
3. **Packages**: Organizado por paquetes Java

Para cambiar: Clic en el dropdown arriba del árbol de archivos

---

## ⚠️ Solución de Problemas Comunes

### Error: "Cannot resolve symbol R"

```
File → Invalidate Caches → Invalidate and Restart
```

### Error: "Gradle sync failed"

1. Verifica tu conexión a internet
2. `File` → `Sync Project with Gradle Files`
3. Si persiste: Borra `.gradle` y `.idea` del proyecto

### Error: "Unable to find SDK"

1. `File` → `Project Structure`
2. En `SDK Location`, selecciona tu Android SDK
3. Usualmente: `C:\Users\[TU_USUARIO]\AppData\Local\Android\Sdk`

### App no se instala en el dispositivo

1. Desinstala la versión anterior manualmente
2. O usa: `Build` → `Clean Project`
3. Luego: `Build` → `Rebuild Project`

### Logcat vacío

1. Verifica que el dispositivo esté seleccionado en el dropdown
2. Cambia el filtro de "No Filters" a "Show only selected application"

---

## 🚀 Consejos Pro

1. **Habilita autoimport:**
   - `File` → `Settings` → `Editor` → `General` → `Auto Import`
   - Marca "Add unambiguous imports on the fly"

2. **Formatea automáticamente al guardar:**
   - `File` → `Settings` → `Tools` → `Actions on Save`
   - Marca "Reformat code"

3. **Aumenta memoria de Android Studio:**
   - `Help` → `Edit Custom VM Options`
   - Cambia `-Xmx` a `-Xmx4096m` (4GB)

4. **Modo Zen (sin distracciones):**
   - `View` → `Appearance` → `Enter Distraction Free Mode`

---

## 📱 Probar en Dispositivo Real vs Emulador

### ✅ Ventajas Dispositivo Real:
- Más rápido
- Pruebas más realistas
- Sensores reales (GPS, cámara, etc.)
- Pruebas de red más precisas

### ✅ Ventajas Emulador:
- No necesitas dispositivo físico
- Puedes probar diferentes versiones de Android
- Fácil cambiar resoluciones

**Recomendación:** Usa dispositivo real siempre que sea posible.

---

¡Ya estás listo para desarrollar en Android Studio! 🎉
