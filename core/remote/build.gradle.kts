plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.testing)
    id("kotlinx-serialization")
}

android {
    namespace = "com.alexeymerov.radiostations.core.remote"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization)

    implementation(platform(libs.ktor.bom))
    implementation(libs.bundles.ktor)
}