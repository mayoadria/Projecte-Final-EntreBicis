plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt")
}

android {
    namespace = "cat.copernic.amayo.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "cat.copernic.amayo.frontend"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.foundation.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.coil.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)



    // Dependencias de Jetpack Compose (ajusta las versiones a las que uses)
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.androidx.activity.compose.v161)


    // Dependencias para OSMDroid y ubicación
    // OSM
    implementation (libs.osmdroid.android)
    implementation (libs.play.services.location)
    implementation (libs.play.services.location.v2101)


    // Room core
    implementation (libs.androidx.room.runtime)
// Para usar corrutinas / Kotlin extensions
    implementation (libs.androidx.room.ktx)
// Procesador de anotaciones de Room
    kapt (libs.androidx.room.compiler)

    implementation (libs.converter.scalars)



    
    implementation(libs.logging.interceptor)

    implementation (libs.androidx.material.icons.extended)

    implementation (libs.androidx.material3.vltimaversin)



}