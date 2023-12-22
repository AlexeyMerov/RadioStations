plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.library.compose)
}

android {
    namespace = "com.alexeymerov.radiostations.core.analytics"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    api(libs.firebase.analytics)
}