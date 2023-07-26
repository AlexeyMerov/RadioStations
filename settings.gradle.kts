pluginManagement {
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

    versionCatalogs {
        create("libs") {
            val gradle = version("gradle", "8.1.0")
            val kotlin = version("kotlin", "1.9.0")
            val versionsChecker = version("versionsChecker", "0.47.0")

            plugin("androidApplication", "com.android.application").versionRef(gradle)
            plugin("androidLibrary", "com.android.library").versionRef(gradle)
            plugin("kotlinAndroid", "org.jetbrains.kotlin.android").versionRef(kotlin)
            plugin("ksp", "com.google.devtools.ksp").version("1.9.0-1.0.11")
            plugin("ktlint", "org.jlleitschuh.gradle.ktlint").version("11.5.0")
            plugin("versionsChecker", "com.github.ben-manes.versions").versionRef(versionsChecker)

            library("plugin-gradle", "com.android.tools.build", "gradle").versionRef(gradle)
            library("plugin-kotlin", "org.jetbrains.kotlin", "kotlin-gradle-plugin").versionRef(kotlin)
            library("plugin-versionsChecker", "com.github.ben-manes", "gradle-versions-plugin").versionRef(versionsChecker)

            library("timber", "com.jakewharton.timber:timber:5.0.1")
            library("androidx-core-ktx", "androidx.core:core-ktx:1.10.1")
            library("work-runtime", "androidx.work:work-runtime-ktx:2.8.1")

            val hilt = "2.47"
            library("plugin-hilt", "com.google.dagger:hilt-android-gradle-plugin:$hilt")
            library("hilt-android", "com.google.dagger:hilt-android:$hilt")
            library("hilt-compiler", "com.google.dagger:hilt-compiler:$hilt")

            val room = "2.5.2"
            library("room-runtime", "androidx.room:room-runtime:$room")
            library("room-ktx", "androidx.room:room-ktx:$room")
            library("room-compiler", "androidx.room:room-compiler:$room")

            val retrofit = "2.9.0"
            library("retrofit-base", "com.squareup.retrofit2:retrofit:$retrofit")
            library("retrofit-converter-moshi", "com.squareup.retrofit2:converter-moshi:$retrofit")

            val moshi = "1.15.0"
            library("moshi-kotlin-base", "com.squareup.moshi:moshi-kotlin:$moshi")
            library("moshi-kotlin-codegen", "com.squareup.moshi:moshi-kotlin-codegen:$moshi")

            val okhttp = "4.11.0"
            library("okhttp-base", "com.squareup.okhttp3:okhttp:$okhttp")
            library("okhttp-logging", "com.squareup.okhttp3:logging-interceptor:$okhttp")

            val exoplayer = "1.1.0"
            library("media3-exoplayer-base", "androidx.media3:media3-exoplayer:$exoplayer")
            library("media3-exoplayer-dash", "androidx.media3:media3-exoplayer-dash:$exoplayer")
            library("media3-ui", "androidx.media3:media3-ui:$exoplayer")

            library("junit-base", "junit:junit:4.13.2")
            library("junit-ext", "androidx.test.ext:junit:1.1.5")
            library("espresso", "androidx.test.espresso:espresso-core:3.5.1")

            library("compose-bom", "androidx.compose:compose-bom:2023.06.01")
            library("compose-material3", "androidx.compose.material3", "material3").withoutVersion()
            library("compose-ui-tooling", "androidx.compose.ui", "ui-tooling").withoutVersion()
            library("compose-ui-toolingPreview", "androidx.compose.ui", "ui-tooling-preview").withoutVersion()

            val composeLifecycle = "2.6.1"
            library("compose-viewmodel", "androidx.lifecycle:lifecycle-viewmodel-compose:$composeLifecycle")
            library("compose-runtime", "androidx.lifecycle:lifecycle-runtime-compose:$composeLifecycle")

            library("compose-activity", "androidx.activity:activity-compose:1.7.2")
            library("compose-navigation-base", "androidx.navigation:navigation-compose:2.6.0")
            library("compose-navigation-hilt", "androidx.hilt:hilt-navigation-compose:1.0.0")
            library("coil-compose", "io.coil-kt:coil-compose:2.4.0")

            val accompanist = "0.30.1"
            library("accompanist-navigation", "com.google.accompanist:accompanist-navigation-animation:$accompanist")
            library("accompanist-systemUiController", "com.google.accompanist:accompanist-systemuicontroller:$accompanist")
        }
    }
}

rootProject.name = "RadioStations"
include(":app")