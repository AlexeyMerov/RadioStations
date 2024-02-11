plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.room)
    alias(libs.plugins.radiostations.android.androidTesting)
}

android {
    namespace = "com.alexeymerov.radiostations.core.database"

    // Adds exported schema location as test app assets.
    sourceSets.getByName("androidTest").assets.srcDir("$projectDir/schemas")
}

dependencies {
    implementation(projects.core.common)

    api(libs.paging.runtime)
    implementation(libs.room.paging)

    androidTestImplementation(libs.paging.testing)
}