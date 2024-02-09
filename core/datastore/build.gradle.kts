plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.core.datastore"

    defaultConfig {
        testInstrumentationRunner = "com.alexeymerov.radiostations.core.test.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(libs.dataStore.base)

    androidTestImplementation(projects.core.test)
    androidTestUtil(libs.test.orchestrator)
}