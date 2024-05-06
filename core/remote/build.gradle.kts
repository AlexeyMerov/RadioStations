plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.testing)
    id("kotlinx-serialization")
    alias(libs.plugins.apolloGraphql)
}

android {
    namespace = "com.alexeymerov.radiostations.core.remote"
}

apollo {
    service("countries") {
        srcDir("src/main/graphql/countries")
        packageName.set("com.alexeymerov.radiostations.core.remote")
    }
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization)

    implementation(platform(libs.retrofit.bom))
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinxSerialization)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.base)
    implementation(libs.okhttp.logging)

    implementation(libs.apollo.runtime)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    testImplementation(libs.okhttp.test)
    testImplementation(libs.retrofit.test)
}