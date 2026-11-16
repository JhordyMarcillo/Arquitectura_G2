plugins {
    id("com.android.application") version "8.7.3" apply false
}

// Android (aapt2) no soporta rutas de mas de 260 caracteres y "SOA BDD_LOCAL" es muy profunda:
// la carpeta build se genera en una ruta corta (%LOCALAPPDATA%\EurekaBankGR02 o el temporal del sistema).
val carpetaCorta = (System.getenv("LOCALAPPDATA") ?: System.getProperty("java.io.tmpdir")) + "/EurekaBankGR02/" + rootProject.name
allprojects {
    layout.buildDirectory.set(file("$carpetaCorta/${project.name}"))
}
