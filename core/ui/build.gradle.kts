plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.library.compose)
    kotlin("plugin.parcelize")
}

android {
    namespace = "com.alexeymerov.radiostations.core.ui"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.dto)
    implementation(projects.core.analytics)

    api(libs.androidx.appcompat)
}
