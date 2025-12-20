plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.library")
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            binaryOption("bundleId", "com.example.yangdnash.shared")
            export(libs.koin.core)
        }
    }

    sourceSets {
        val composeDeps = project.extensions.getByType<org.jetbrains.compose.ComposeExtension>().dependencies

        commonMain.dependencies {
            implementation("dev.gitlive:firebase-auth:1.13.0")
            implementation("dev.gitlive:firebase-firestore:1.13.0")
            implementation("dev.gitlive:firebase-storage:1.13.0")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.2.0")
            implementation("com.russhwolf:multiplatform-settings-coroutines:1.2.0")
            implementation(composeDeps.runtime)
            implementation(composeDeps.foundation)
            implementation(composeDeps.material3)
            implementation(composeDeps.ui)
            implementation(composeDeps.components.resources)

            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.2")

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation("io.insert-koin:koin-compose-viewmodel:1.2.0-Beta4")

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.stately.common)

            implementation(libs.androidx.room.runtime)
        }

        androidMain.dependencies {
            implementation(libs.androidx.room.ktx)
            implementation(composeDeps.uiTooling)
            implementation("io.insert-koin:koin-android:3.5.0") // oder deine Koin Version
        }

        val iosMain by getting {
            dependencies {
                api(libs.koin.core)
            }
        }
    }
}

android {
    namespace = "com.example.yangdnashabschlussprojekt.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    }
}