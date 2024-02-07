plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.core.connectivity"
}

dependencies {
    testImplementation(projects.core.test)
    testImplementation(libs.robolectric)
}