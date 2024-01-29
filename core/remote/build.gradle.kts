plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.alexeymerov.radiostations.core.remote"

    defaultConfig {
        testInstrumentationRunner = "com.alexeymerov.radiostations.core.test.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }
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

    testImplementation(projects.core.test)
}