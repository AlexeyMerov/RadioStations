@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.room)
}

android {
    namespace = "com.alexeymerov.radiostations.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.alexeymerov.radiostations.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests.isIncludeAndroidResources = true
    }

    // Adds exported schema location as test app assets.
    sourceSets.getByName("androidTest").assets.srcDir("$projectDir/schemas")
}

dependencies {
    implementation(projects.core.common)

    androidTestImplementation(projects.core.test)
    androidTestUtil(libs.test.orchestrator)
}