plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.core.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.remote)
    implementation(projects.core.database)

    testImplementation(projects.core.test)
    testImplementation(libs.paging.testing)
}