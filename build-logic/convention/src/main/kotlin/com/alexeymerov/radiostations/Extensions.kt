package com.alexeymerov.radiostations

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.getLibrary(alias: String) = findLibrary(alias).get()

fun VersionCatalog.getStringVersion(alias: String) = findVersion(alias).get().toString()

fun VersionCatalog.getIntVersion(alias: String) = getStringVersion(alias).toInt()

fun DependencyHandlerScope.implementation(dependency: Any) {
    add("implementation", dependency)
}

fun DependencyHandlerScope.testImplementation(dependency: Any) {
    add("testImplementation", dependency)
}

fun DependencyHandlerScope.androidTestImplementation(dependency: Any) {
    add("androidTestImplementation", dependency)
}

fun DependencyHandlerScope.debugImplementation(dependency: Any) {
    add("debugImplementation", dependency)
}

fun DependencyHandlerScope.ksp(dependency: Any) {
    add("ksp", dependency)
}

fun DependencyHandlerScope.kspAndroidTest(dependency: Any) {
    add("kspAndroidTest", dependency)
}

fun DependencyHandlerScope.androidTestUtil(dependency: Any) {
    add("androidTestUtil", dependency)
}