import org.gradle.kotlin.dsl.implementation
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
        minSdk = 26
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
            excludes += "google/api/logging.proto"

            excludes += "google/api/annotations.proto"
            excludes += "google/api/http.proto"
            excludes += "google/protobuf/**"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/*"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.object1.detection)
    implementation(libs.androidx.compose.foundation.layout)

    implementation(libs.firebase.firestore)
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.ui)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.sceneform.base)
    implementation(libs.genai.common)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.runtime)

    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    implementation(libs.mlkit.text)
    implementation(libs.mlkit.translate)

    implementation(libs.arcore)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.retrofit)
    implementation(libs.converterMoshi)
    implementation(libs.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.google.maps.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.retrofit.converter.gson)

    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.guava)

    implementation(libs.okio)

    implementation(libs.mediapipe.vision.detector)

    implementation(libs.mlkit.barcode)       // Barcode Scanning
    implementation(libs.mlkit.face)          // Face Detection
    implementation(libs.mlkit.label)         // Image Labeling
    implementation(libs.mlkit.pose)          // Pose Detection

    implementation(libs.mlkit.custom)        // Custom Model

    implementation(libs.mlkit.objectDetection)
    implementation(libs.mlkit.objectDetection.common)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.camerax.core)

    implementation(libs.androidx.lifecycle.runtime.compose)
}
