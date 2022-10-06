plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "kmm.jacky.utilitylibrary.android"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":monospacedTableFormatter"))
    implementation("com.google.android.material:material:1.6.1")

    implementation("androidx.compose.ui:ui:1.1.1")
    implementation("androidx.compose.ui:ui-tooling:1.1.1")
    implementation("androidx.compose.material:material:1.1.1")
    implementation("androidx.compose.material:material-icons-core:1.1.1")
    implementation("androidx.compose.material:material-icons-extended:1.1.1")
    implementation("androidx.activity:activity-compose:1.1.1")
}
