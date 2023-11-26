package com.alexeymerov.radiostations

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.getStringVersion("composeCompiler")
        }

        dependencies {
            val composeBom = platform(libs.getLibrary("compose-bom"))
            implementation(composeBom)
            androidTestImplementation(composeBom)

            implementation(libs.getLibrary("compose-material3"))
            implementation(libs.getLibrary("compose-materialIcons"))
            implementation(libs.getLibrary("compose-ui-toolingPreview"))
            debugImplementation(libs.getLibrary("compose-ui-tooling"))
        }
    }
}