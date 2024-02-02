plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.room)
}

android {
    namespace = "com.alexeymerov.radiostations.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.alexeymerov.radiostations.core.test.HiltTestRunner"
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

    api(libs.paging.runtime)
    implementation(libs.room.paging)

    androidTestImplementation(projects.core.test)
    androidTestUtil(libs.test.orchestrator)

    androidTestImplementation(libs.paging.testing)
}