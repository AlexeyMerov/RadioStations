import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    alias(libs.plugins.radiostations.android.application)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.application.compose)

    kotlin("plugin.parcelize")
}

android {
    namespace = "com.alexeymerov.radiostations"

    defaultConfig {
        applicationId = "com.alexeymerov.radiostations"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        vectorDrawables.useSupportLibrary = true
        resourceConfigurations.addAll(listOf("en", "uk", "ru"))

        testInstrumentationRunner = "com.alexeymerov.radiostations.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
        }
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.remote)
    implementation(projects.core.datastore)
    implementation(projects.core.database)

    implementation(libs.work.runtime) // to avoid crash on Android 12 API 31
    implementation(libs.kotlin.guava)

    implementation(libs.media3.exoplayer.base)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)
    implementation(libs.media3.session)

    implementation(libs.compose.activity)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.runtime)
    implementation(libs.compose.navigation.base)
    implementation(libs.compose.navigation.hilt)

    implementation(libs.coil.compose)
    implementation(libs.accompanist.systemUiController)

    implementation(libs.lottie)
    implementation(libs.splashScreen)

    /* --- TESTS --- */

    testImplementation(libs.junit)
    testImplementation(libs.junitExt)
    androidTestImplementation(libs.junitExt)

    testImplementation(libs.test.core)
    androidTestImplementation(libs.test.core)

    testImplementation(libs.test.coreKtx)
    androidTestImplementation(libs.test.coreKtx)

    testImplementation(libs.test.runner)
    androidTestImplementation(libs.test.runner)

    androidTestUtil(libs.test.orchestrator)

    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
}