import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
}

android {
    namespace = "com.alexeymerov.radiostations"
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        languageVersion = libs.versions.kotlinLanguage.get()
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    defaultConfig {
        applicationId = "com.alexeymerov.radiostations"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true

        resourceConfigurations.addAll(listOf("en", "uk", "ru"))

        testInstrumentationRunner = "com.alexeymerov.radiostations.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        buildConfigField("String", "BASE_URL", "\"https://opml.radiotime.com/\"")
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
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("$project.rootDir/tools/proguard-rules.pro"))

            signingConfig = signingConfigs.getByName("release")
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
        xmlReport = false
//        disable.add("UnsafeExperimentalUsageError")
//        disable.add("UnsafeExperimentalUsageWarning")
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests.isIncludeAndroidResources = true
    }

    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // https://issuetracker.google.com/issues/179057202

    implementation(libs.androidx.core.ktx)
    implementation(libs.work.runtime) // to avoid crash on Android 12 API 31
    implementation(libs.timber)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.retrofit.base)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin.base)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.okhttp.base)
    implementation(libs.okhttp.logging)

    implementation(libs.media3.exoplayer.base)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.compose.material3)
    implementation(libs.compose.ui.toolingPreview)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.compose.activity)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.runtime)
    implementation(libs.compose.navigation.base)
    implementation(libs.compose.navigation.hilt)
    implementation(libs.compose.materialIcons)

    implementation(libs.coil.compose)
    implementation(libs.accompanist.systemUiController)

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

    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)

    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.coroutines.test)

    androidTestImplementation(libs.room.testing)

    testImplementation(libs.okhttp.test)
    testImplementation(libs.retrofit.test)

    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
}

kapt {
    correctErrorTypes = true
}

ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> = listOf("room.schemaLocation=${schemaDir.path}")
}