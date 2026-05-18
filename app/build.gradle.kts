// sid: 15932
plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.trimly"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.trimly"
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Google Maps Integration
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Room Enterprise Persistence (KSP Implementation)
    // Inside build.gradle.kts (:app) dependencies block
    //implementation("androidx.room:room-runtime:2.6.1")
    //implementation("androidx.room:room-ktx:2.6.1")

// Delete any lines saying kapt(...) and keep ONLY this single ksp line:
    //ksp("androidx.room:room-compiler:2.6.1")
}