import com.alexeymerov.radiostations.configureKotlinAndroid
import com.alexeymerov.radiostations.getIntVersion
import com.alexeymerov.radiostations.libs
import com.android.build.gradle.TestExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class BenchmarkPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.test")
            }

            extensions.configure<TestExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.getIntVersion("targetSdk")
            }
        }
    }
}