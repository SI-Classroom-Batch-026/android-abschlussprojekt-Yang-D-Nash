plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.library")
    alias(libs.plugins.kotlin.ksp)
}

kotlin {
    androidTarget {
        compilations.all {
            @Suppress("DEPRECATION")
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization)
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.ktx)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    namespace = "com.example.yangdnashabschlussprojekt.shared"

    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)

    // If you add iOS targets later, you will need to add them here too, e.g.:
    // add("kspIosX64", libs.androidx.room.compiler)
    // add("kspIosArm64", libs.androidx.room.compiler)
    // add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
