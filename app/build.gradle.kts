import com.android.build.api.dsl.Packaging

plugins {
    // ------------------------
    // Core Plugins
    // ------------------------
    alias(libs.plugins.android.application)          // Android Application Plugin
    alias(libs.plugins.kotlin.android)               // Kotlin Android Plugin
    alias(libs.plugins.kotlin.compose)               // Kotlin Compose Plugin
    alias(libs.plugins.jetbrains.kotlin.serialization) // Kotlin Serialization Plugin
    alias(libs.plugins.kotlin.ksp)                   // Kotlin Symbol Processing (Room Compiler)

    // ------------------------
    // Firebase Services
    // ------------------------
    id("com.google.gms.google-services")            // Firebase plugin
}

android {
    namespace = "com.example.yangdnashabschlussprojekt"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.yangdnashabschlussprojekt"
        minSdk = 24
        targetSdk = 36
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
        debug {
            isMinifyEnabled = false
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

    @Suppress("UnstableApiUsage")
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeBom.get()
    }

    fun Packaging.() {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    // ------------------------
    // Core & Lifecycle
    // ------------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ------------------------
    // Compose / UI / Material
    // ------------------------
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // ------------------------
    // Koin Dependency Injection
    // ------------------------
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // ------------------------
    // Room / DataStore / WorkManager
    // ------------------------
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)

    // ------------------------
    // CameraX
    // ------------------------
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ------------------------
    // ML Kit
    // ------------------------
    implementation(libs.mlkit.text)
    implementation(libs.mlkit.translate)
    // ------------------------
    // ARCore
    // ------------------------
    implementation(libs.arcore)

    // ------------------------
    // Firebase
    // ------------------------
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // ------------------------
    // Networking / Serialization
    // ------------------------
    implementation(libs.retrofit)
    implementation(libs.converterMoshi)
    implementation(libs.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization)

    // ------------------------
    // Image Loading / Coil
    // ------------------------
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // ------------------------
    // Location / Maps
    // ------------------------
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.google.maps.compose)

    // ------------------------
    // Testing
    // ------------------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


}
