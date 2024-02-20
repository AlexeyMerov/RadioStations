plugins {
    alias(libs.plugins.radiostations.android.feature)
    alias(libs.plugins.radiostations.android.library.compose)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.testing)
}

android {
    namespace = "com.alexeymerov.radiostations.feature.profile"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.domain)

    implementation(libs.accompanist.permissions)
    implementation(libs.coil.svg)
    implementation(libs.paging.compose)
    implementation(libs.imageCropper)

    androidTestImplementation(libs.test.compose.junit)
    debugImplementation(libs.test.compose.manifest)
}