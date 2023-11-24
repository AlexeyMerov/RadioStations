package com.alexeymerov.radiostations

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.getLibrary(alias: String) = findLibrary(alias).get()

fun VersionCatalog.getStringVersion(alias: String) = findVersion(alias).get().toString()

fun VersionCatalog.getIntVersion(alias: String) = getStringVersion(alias).toInt()

fun DependencyHandlerScope.implementation(alias: Provider<MinimalExternalModuleDependency>) {
    add("implementation", alias)
}

fun DependencyHandlerScope.testImplementation(alias: Provider<MinimalExternalModuleDependency>) {
    add("testImplementation", alias)
}

fun DependencyHandlerScope.androidTestImplementation(alias: Provider<MinimalExternalModuleDependency>) {
    add("androidTestImplementation", alias)
}

fun DependencyHandlerScope.debugImplementation(alias: Provider<MinimalExternalModuleDependency>) {
    add("debugImplementation", alias)
}

fun DependencyHandlerScope.ksp(alias: Provider<MinimalExternalModuleDependency>) {
    add("ksp", alias)
}

fun DependencyHandlerScope.kspAndroidTest(alias: Provider<MinimalExternalModuleDependency>) {
    add("kspAndroidTest", alias)
}

fun DependencyHandlerScope.androidTestUtil(alias: Provider<MinimalExternalModuleDependency>) {
    add("androidTestUtil", alias)
}