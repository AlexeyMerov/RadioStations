plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.core.test"
}

dependencies {
    api(libs.test.core)
    api(libs.test.coreKtx)
    api(libs.test.runner)

    api(libs.test.ext.junit)
    api(libs.test.ext.junitExt)
    api(libs.test.ext.truth)

    api(libs.mockk.android)
    api(libs.mockk.agent)

    api(libs.coroutines.test)
    api(libs.paging.runtime)

    implementation(libs.hilt.testing)
}