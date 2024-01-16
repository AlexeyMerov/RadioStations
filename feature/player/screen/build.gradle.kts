plugins {
    alias(libs.plugins.radiostations.android.feature)
    alias(libs.plugins.radiostations.android.library.compose)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.feature.player.screen"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.domain)

    implementation(libs.palette)
}