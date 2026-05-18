// Top-level build.gradle.kts (Trimly)
plugins {
    alias(libs.plugins.android.application) apply false

    // Swapped the broken alias chain for the bulletproof direct ID
   //id("org.jetbrains.kotlin.android") version "2.0.0" apply false

   // id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}