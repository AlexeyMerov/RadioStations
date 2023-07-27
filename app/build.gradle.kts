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
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtension.get()
    }

    defaultConfig {
        applicationId = "com.alexeymerov.radiostations"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"https://opml.radiotime.com/\"")
    }

    buildTypes {
        named("debug") {
            isDebuggable = true
            isMinifyEnabled = false
        }
        named("release") {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("$project.rootDir/tools/proguard-rules.pro"))
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
        xmlReport = false
//        disable.add("UnsafeExperimentalUsageError")
//        disable.add("UnsafeExperimentalUsageWarning")
    }
}

dependencies {
    testImplementation(libs.junit.base)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)

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

    implementation(libs.coil.compose)
    implementation(libs.accompanist.navigation)
    implementation(libs.accompanist.systemUiController)
}

kapt {
    correctErrorTypes = true
}