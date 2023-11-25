import com.alexeymerov.radiostations.configureKotlinAndroid
import com.alexeymerov.radiostations.getIntVersion
import com.alexeymerov.radiostations.libs
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
class LibPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.getIntVersion("targetSdk")
            }
        }
    }
}