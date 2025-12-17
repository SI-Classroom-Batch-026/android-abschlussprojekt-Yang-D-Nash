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
        }
    }

    sourceSets {
        val composeDeps = project.extensions.getByType<org.jetbrains.compose.ComposeExtension>().dependencies

        commonMain.dependencies {
            implementation(composeDeps.runtime)
            implementation(composeDeps.foundation)
            implementation(composeDeps.material3)
            implementation(composeDeps.ui)
            implementation(composeDeps.components.resources)
            implementation("io.insert-koin:koin-compose:1.1.0")
            implementation("co.touchlab:stately-common:2.1.0")
            implementation(libs.kotlinx.serialization)
            implementation(libs.koin.core)
            implementation(libs.androidx.room.runtime)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }

        androidMain.dependencies {
            implementation(libs.androidx.room.ktx)
            implementation(composeDeps.uiTooling)
        }

        val iosMain by getting {
            dependencies {
                implementation(composeDeps.ui)
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
    // Room KSP Setup
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}