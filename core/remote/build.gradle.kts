@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.alexeymerov.radiostations.remote"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.retrofit.base)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin.base)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.okhttp.base)
    implementation(libs.okhttp.logging)

    testImplementation(libs.okhttp.test)
    testImplementation(libs.retrofit.test)

    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.coroutines.test)

}