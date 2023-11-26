@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
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

    api(libs.compose.viewmodel)
    api(libs.compose.runtime)

    api(libs.coil.compose)
    api(libs.lottie)
}
