import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    alias(libs.plugins.radiostations.android.application)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.app.compose)
    alias(libs.plugins.radiostations.android.room)

    kotlin("plugin.parcelize")
}

android {
    namespace = "com.alexeymerov.radiostations"

    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.alexeymerov.radiostations"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        vectorDrawables.useSupportLibrary = true
        resourceConfigurations.addAll(listOf("en", "uk", "ru"))
        buildConfigField("String", "BASE_URL", "\"https://opml.radiotime.com/\"")

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

    // Adds exported schema location as test app assets.
    sourceSets.getByName("androidTest").assets.srcDir("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.work.runtime) // to avoid crash on Android 12 API 31
    implementation(libs.timber)
    implementation(libs.dataStore.base)
    implementation(libs.kotlin.guava)

    implementation(libs.retrofit.base)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin.base)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.okhttp.base)
    implementation(libs.okhttp.logging)

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

    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.coroutines.test)

    testImplementation(libs.okhttp.test)
    testImplementation(libs.retrofit.test)

    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
}