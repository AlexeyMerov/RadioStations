plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.core.domain"

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.data)
    implementation(projects.core.filestore)
    implementation(projects.core.analytics)
    implementation(projects.core.connectivity)

    api(projects.core.dto)

    testImplementation(projects.core.test)
    testImplementation(libs.robolectric)
}