plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0-RC3"}

android {
    namespace = "com.example.greenhousetemp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.greenhousetemp"
        minSdk = 24
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // ... (existing dependencies) ...

// Ktor HTTP Client (for network requests)
    implementation(libs.ktor.client.core) // Check for the latest stable version
    implementation(libs.ktor.client.android) // Android-specific engine
    implementation(libs.ktor.client.content.negotiation) // For JSON serialization/deserialization
    implementation(libs.ktor.serialization.kotlinx.json) // For Kotlinx Serialization with Ktor

// Kotlinx Serialization (for JSON parsing)
    implementation(libs.kotlinx.serialization.json) // Check for the latest stable version

// Coroutine Lifecycle Scopes (to manage coroutines that tie to the UI lifecycle)
    implementation(libs.androidx.lifecycle.runtime.ktx.v281) // Already have this
    implementation(libs.androidx.lifecycle.runtime.compose) // Important for Composables
}