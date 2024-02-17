import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.alexeymerov.radiostations.buildlogic"

// Configure the build-logic plugins to target project JDK version
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = libs.versions.javaVersion.get()
    }
}

dependencies {
    compileOnly(libs.gradlePlugin.android)
    compileOnly(libs.gradlePlugin.kotlin)
    compileOnly(libs.gradlePlugin.ksp)
    compileOnly(libs.gradlePlugin.firebaseCrashlytics)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "radiostations.android.application"
            implementationClass = "AppPlugin"
        }
        register("androidLibrary") {
            id = "radiostations.android.library"
            implementationClass = "LibPlugin"
        }
        register("androidFeature") {
            id = "radiostations.android.feature"
            implementationClass = "FeaturePlugin"
        }
        register("androidHilt") {
            id = "radiostations.android.hilt"
            implementationClass = "HiltPlugin"
        }
        register("androidAppCompose") {
            id = "radiostations.android.application.compose"
            implementationClass = "AppComposePlugin"
        }
        register("androidLibCompose") {
            id = "radiostations.android.library.compose"
            implementationClass = "LibComposePlugin"
        }
        register("androidRoom") {
            id = "radiostations.android.room"
            implementationClass = "RoomPlugin"
        }
        register("androidFirebase") {
            id = "radiostations.android.firebase"
            implementationClass = "FirebasePlugin"
        }
        register("androidTesting") {
            id = "radiostations.android.testing"
            implementationClass = "TestPlugin"
        }

    }
}