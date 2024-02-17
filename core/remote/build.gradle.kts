plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.testing)
}

android {
    namespace = "com.alexeymerov.radiostations.core.remote"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.retrofit.base)
    implementation(libs.retrofit.converter.moshi)

    implementation(libs.moshi.kotlin.base)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.okhttp.base)
    implementation(libs.okhttp.logging)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    testImplementation(libs.okhttp.test)
    testImplementation(libs.retrofit.test)
}