import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    dependencies {
        classpath(libs.plugin.gradle)
        classpath(libs.plugin.kotlin)
        classpath(libs.plugin.hilt)
        classpath(libs.plugin.versionsChecker)
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versionsChecker)
}

allprojects {
    project.configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.contains("kotlin-(reflect|stdlib)".toRegex())) {
                useVersion(libs.versions.kotlin.get())
            }
        }
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        candidate.version.lowercase().contains("alpha|beta|rc".toRegex())
    }
}