import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versionsChecker) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gradleSecrets) apply false
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
