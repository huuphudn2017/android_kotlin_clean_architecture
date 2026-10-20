plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

// KSP processor that generates typed constants for `assets/` and `res/raw/`.
// Runs at build time only; it is never packaged into the APK.
dependencies {
    implementation(libs.ksp.symbol.processing.api)
    testImplementation(libs.junit)
}
