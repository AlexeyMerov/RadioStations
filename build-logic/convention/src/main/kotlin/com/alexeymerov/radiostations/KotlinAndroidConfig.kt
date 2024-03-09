package com.alexeymerov.radiostations

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *>) {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.android")
    }

    commonExtension.apply {
        compileSdk = libs.getIntVersion("compileSdk")

        defaultConfig.minSdk = libs.getIntVersion("minSdk")

        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(libs.getStringVersion("javaVersion"))
            targetCompatibility = JavaVersion.toVersion(libs.getStringVersion("javaVersion"))
        }

        dependencies {
            implementation(libs.getLibrary("timber"))
            implementation(libs.getLibrary("androidx-core-ktx"))
            implementation(libs.getLibrary("coroutines-core"))
            implementation(libs.getLibrary("coroutines-android"))
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
                merges += "META-INF/LICENSE.md"
                merges += "META-INF/LICENSE-notice.md"
            }
        }
    }

    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = libs.getStringVersion("javaVersion")
            languageVersion = libs.getStringVersion("kotlinLanguage")

            // Enable experimental APIs
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
        }
    }
}
