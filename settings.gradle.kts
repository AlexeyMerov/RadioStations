pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "RadioStations"
include(":app")

include(":core:common")
include(":core:test")
include(":core:remote")
include(":core:datastore")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:dto")
include(":core:filestore")
include(":core:ui")
include(":core:analytics")
include(":core:connectivity")

include(":feature:category")
include(":feature:favorite")
include(":feature:settings")
include(":feature:profile")
include(":feature:player:screen")
include(":feature:player:service")
include(":feature:player:widget")
