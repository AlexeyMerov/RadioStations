package com.alexeymerov.radiostations

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *>) {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.android")
    }

    commonExtension.apply {
        compileSdk = libs.getIntVersion("compileSdk")

        defaultConfig.minSdk = libs.getIntVersion("minSdk")

        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(libs.getStringVersion("java"))
            targetCompatibility = JavaVersion.toVersion(libs.getStringVersion("java"))
        }

        dependencies {
            implementation(libs.getLibrary("timber"))
            implementation(libs.getLibrary("androidx-core-ktx"))

            implementation(platform(libs.getLibrary("coroutines-bom")))
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

private fun Project.configureKotlin() = configure<KotlinAndroidProjectExtension> {
    with(compilerOptions) {
        jvmTarget.set(JvmTarget.fromTarget(libs.getStringVersion("java")))
        languageVersion.set(KotlinVersion.fromVersion(libs.getStringVersion("kotlin").take(3))) // 2.0.0 - > 2.0

        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
        )
    }
}
