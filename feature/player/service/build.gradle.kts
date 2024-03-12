plugins {
    alias(libs.plugins.radiostations.android.feature)
    alias(libs.plugins.radiostations.android.library.compose)
    alias(libs.plugins.radiostations.android.hilt)
}

android {
    namespace = "com.alexeymerov.radiostations.feature.player.service"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.ui)

    implementation(libs.coroutines.guava)

    implementation(libs.media3.exoplayer.base)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)
    api(libs.media3.session)

    api(libs.glance.widget)
    implementation(libs.glance.material3)
}