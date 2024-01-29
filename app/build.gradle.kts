import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
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
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
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
    implementation(projects.feature.player.widget)

    implementation(libs.work.runtime) // to avoid crash on Android 12 API 31

    implementation(libs.compose.activity)
    implementation(libs.compose.navigation.base)
    implementation(libs.compose.navigation.hilt)

    implementation(libs.accompanist.systemUiController)
    implementation(libs.splashScreen)

    debugImplementation(libs.leakcanary)
}