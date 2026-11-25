plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.telfquito_soap_java"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.telfquito_soap_java"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // IP del servidor TelefQuito: gradlew installDebug -PservidorIp=192.168.x.x
        // Sin el parametro se usa 10.0.2.2 (el PC visto desde el emulador)
        val servidorIp = (project.findProperty("servidorIp") as String?) ?: "10.0.2.2"
        buildConfigField("String", "SERVIDOR_IP", "\"$servidorIp\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.ksoap2)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}