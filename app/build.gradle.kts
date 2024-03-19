import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.io.FileInputStream
import java.util.Properties

var keystorePropertiesFile: File = rootProject.file("keystore.properties")
if (!keystorePropertiesFile.exists()) keystorePropertiesFile = rootProject.file("keystore.defaults.properties")

val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))


plugins {
    alias(libs.plugins.radiostations.android.application)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.application.compose)
    alias(libs.plugins.radiostations.android.firebase)
    id(libs.plugins.gradleSecrets.get().pluginId)
}

android {
    namespace = "com.alexeymerov.radiostations"

    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.alexeymerov.radiostations"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        vectorDrawables.useSupportLibrary = true
    }

    androidResources {
        // https://developer.android.com/guide/topics/resources/app-languages
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["keyPassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".dev"

            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")

            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }

    secrets {
        propertiesFileName = "secrets.properties"
        defaultPropertiesFileName = "secrets.defaults.properties"
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.core.analytics)
    implementation(projects.core.connectivity)

    implementation(projects.feature.category)
    implementation(projects.feature.favorite)
    implementation(projects.feature.settings)
    implementation(projects.feature.profile)
    implementation(projects.feature.player.screen)
    implementation(projects.feature.player.service)

    // to avoid crash on Android 12 API 31
    // https://stackoverflow.com/questions/68473542/mediasessioncompattargeting-s-version-31-and-above-requires-that-one-of-flag/69152986#69152986
    implementation(libs.androidx.work.runtime)

    implementation(libs.compose.activity)
    implementation(libs.compose.navigation.base)
    implementation(libs.compose.navigation.hilt)

    implementation(libs.accompanist.systemUiController)
    implementation(libs.splashScreen)

    debugImplementation(libs.leakcanary)
}