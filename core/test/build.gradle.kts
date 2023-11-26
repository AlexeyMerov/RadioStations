@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.core.test"
}

dependencies {
    api(libs.junit)
    api(libs.junitExt)

    api(libs.test.core)
    api(libs.test.coreKtx)
    api(libs.test.runner)

    api(libs.mockk.android)
    api(libs.mockk.agent)

    api(libs.coroutines.test)

    api(libs.hilt.testing)
}