import com.alexeymerov.radiostations.configureKotlinAndroid
import com.alexeymerov.radiostations.getIntVersion
import com.alexeymerov.radiostations.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.getIntVersion("targetSdk")
            }
        }
    }
}