plugins {
    alias(libs.plugins.radiostations.android.library)
    alias(libs.plugins.radiostations.android.hilt)
    alias(libs.plugins.radiostations.android.testing)
}

android {
    namespace = "com.alexeymerov.radiostations.core.common"

    buildFeatures.buildConfig = true
    defaultConfig.buildConfigField("String", "BASE_URL", "\"https://opml.radiotime.com/\"")
}