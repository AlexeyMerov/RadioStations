@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.remote)
    implementation(projects.core.database)

    testImplementation(projects.core.test)
}