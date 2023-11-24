import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.alexeymerov.radiostations.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kspGradlePlugin)
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

    }
}