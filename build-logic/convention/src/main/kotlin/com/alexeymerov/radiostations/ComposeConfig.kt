package com.alexeymerov.radiostations

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
            androidTestImplementation(libs.getLibrary("compose-test-junit"))
            debugImplementation(libs.getLibrary("compose-test-manifest"))

            implementation(libs.getLibrary("compose-animation"))
            implementation(libs.getLibrary("compose-foundation"))
            implementation(libs.getLibrary("compose-material3"))
            implementation(libs.getLibrary("compose-materialIcons"))

            implementation(libs.getLibrary("compose-ui-util"))
            implementation(libs.getLibrary("compose-ui-tooling"))
            debugImplementation(libs.getLibrary("compose-ui-toolingPreview"))

            implementation(libs.getLibrary("compose-runtime"))
            implementation(libs.getLibrary("compose-lifecycleRuntime"))
            implementation(libs.getLibrary("compose-activity"))
            implementation(libs.getLibrary("compose-viewmodel"))

            implementation(libs.getLibrary("coil-compose"))
            implementation(libs.getLibrary("lottie"))
        }
    }

    tasks.withType(KotlinCompile::class.java) {
        compilerOptions.freeCompilerArgs.addAll(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true",
        )
    }
}