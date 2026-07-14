import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.gms.google-services")
}

val ktor_version = "2.3.7"
val coil_version = "3.0.0-rc01"
val firebase_version = "1.8.1"
val coroutines_version = "1.7.3"
val okio_version = "3.7.0"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Network and JSON
            implementation("com.google.code.gson:gson:2.10.1")
            implementation("io.ktor:ktor-client-core:$ktor_version")
            implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
            implementation("io.ktor:ktor-client-logging:$ktor_version")

            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")

            // ViewModel and Lifecycles
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.navigation.compose)

            // Image Loading
            implementation("io.coil-kt.coil3:coil-compose:$coil_version")
            implementation("io.coil-kt.coil3:coil-network-ktor2:$coil_version")
            implementation("io.coil-kt.coil3:coil-network-cache-control:$coil_version")

            // Firebase
            implementation("dev.gitlive:firebase-common:$firebase_version")
            implementation("dev.gitlive:firebase-firestore:$firebase_version")
            implementation("dev.gitlive:firebase-auth:$firebase_version")

            // DateTime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

            // Okio
            implementation("com.squareup.okio:okio:$okio_version")
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.osmdroid.android)
            implementation(libs.osm.android.compose)

            // Debug tooling
            implementation(compose.ui)
            implementation(compose.uiTooling)
            implementation(compose.preview)

            // OkHttp
            implementation("com.squareup.okhttp3:okhttp:4.12.0")
            implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
            implementation("io.ktor:ktor-client-okhttp:$ktor_version")
            implementation("io.ktor:ktor-client-cio:$ktor_version")

            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

            // Firebase
            implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
            implementation("com.google.firebase:firebase-analytics")
            implementation("com.google.firebase:firebase-firestore")
            implementation("com.google.android.gms:play-services-location:21.1.0")

            // AndroidX Fragment
            implementation(libs.androidx.fragment)
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktor_version")
            implementation("dev.gitlive:firebase-firestore:$firebase_version")
        }
    }
}

android {
    namespace = "org.bridgwatercarnival.companion"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.bridgwatercarnival.companion"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 21
        versionName = "Updated App Info"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}
dependencies {
    implementation(libs.material3.android)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.runtime.saveable.android)
    implementation(libs.androidx.runtime)
}
