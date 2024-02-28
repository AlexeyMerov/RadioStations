plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.testing)
}

android {
    namespace = "com.alexeymerov.radiostations.core.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.remote)
    implementation(projects.core.database)

    testImplementation(libs.paging.testing)
}