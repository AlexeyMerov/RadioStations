plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.androidTesting)
}

android {
    namespace = "com.alexeymerov.radiostations.core.datastore"
}

dependencies {
    implementation(libs.dataStore.base)
}