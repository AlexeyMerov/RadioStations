plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.testing)
    id("kotlinx-serialization")
}

android {
    namespace = "com.alexeymerov.radiostations.core.remote"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.retrofit.base)

    implementation(libs.kotlinx.serialization)
    implementation(libs.retrofit.kotlinx.serialization)

    implementation(libs.okhttp.base)
    implementation(libs.okhttp.logging)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    testImplementation(libs.okhttp.test)
    testImplementation(libs.retrofit.test)
}