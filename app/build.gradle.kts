import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    id("com.google.gms.google-services")
}

val props = Properties()
val localPropsFile = rootProject.file("local.properties")

if (localPropsFile.exists()) {
    props.load(localPropsFile.inputStream())
}

android {
    namespace = "com.example.yangdnashabschlussprojekt"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.yangdnashabschlussprojekt"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "CLOUD_VISION_API_KEY",
            "\"AIzaSyCeptnqf5FVyWnYkMzb4tRaXI8L8RY9ZcY\""
        )
        buildConfigField(
            "String",
            "CLOUD_TRANSLATE_API_KEY",
            "\"AIzaSyCeptnqf5FVyWnYkMzb4tRaXI8L8RY9ZcY\""
        )
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

    packaging {
        resources {
            excludes += "META-INF/*"
        }
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
    implementation(libs.object1.detection)
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

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}
