// sid: 15932
plugins {
    alias(libs.plugins.android.application)
    //id("org.jetbrains.kotlin.android")
    //Firebase
    id("com.google.gms.google-services")
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


    // --- FIREBASE CLOUD BRIDGE ---
    // The BoM ensures all Firebase libraries use compatible versions
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // The exact Firebase features Trimly needs for our Master Plan:
    implementation("com.google.firebase:firebase-auth")       // Stage 2: Login & Roles
    implementation("com.google.firebase:firebase-firestore")  // Stage 3 & 4: NoSQL Database
    implementation("com.google.firebase:firebase-storage")    // Stage 5: Barber Portfolios Images

    // Firebase Storage for saving image files
    implementation("com.google.firebase:firebase-storage-ktx")

    // Glide for painting images onto the screen
    implementation("com.github.bumptech.glide:glide:4.16.0")
}