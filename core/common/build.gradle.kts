@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.radiostations.android.library)
}

android {
    namespace = "com.alexeymerov.radiostations.common"

    buildFeatures.buildConfig = true
    defaultConfig.buildConfigField("String", "BASE_URL", "\"https://opml.radiotime.com/\"")
}

dependencies {
    implementation(libs.androidx.core.ktx)
}