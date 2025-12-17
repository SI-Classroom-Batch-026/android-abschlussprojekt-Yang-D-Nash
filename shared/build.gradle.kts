
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
        }
    }

    sourceSets {
        // Fix für Compose Extension Reference
        val composeDeps = project.extensions.getByType<org.jetbrains.compose.ComposeExtension>().dependencies

        commonMain.dependencies {
            implementation(composeDeps.runtime)
            implementation(composeDeps.foundation)
            implementation(composeDeps.material3)
            implementation(composeDeps.ui)
            implementation(composeDeps.components.resources)
            implementation(libs.kotlinx.datetime)

            // Namen exakt nach deiner TOML (Punkte statt Bindestriche)
            implementation(libs.koin.compose)
            implementation(libs.stately.common)
            implementation(libs.kotlinx.serialization)
            implementation(libs.koin.core)
            implementation(libs.androidx.room.runtime)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.room.ktx)
            implementation(composeDeps.uiTooling)
        }

        val iosMain by getting {
            dependencies {
                // iOS spezifische deps falls nötig
            }
        }
    }
}

android {
    namespace = "com.example.yangdnashabschlussprojekt.shared"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
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