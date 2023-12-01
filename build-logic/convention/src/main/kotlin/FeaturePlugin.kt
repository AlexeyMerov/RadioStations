import com.alexeymerov.radiostations.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("radiostations.android.library")
            }

            dependencies {
                implementation(project(":core:domain"))
            }
        }
    }
}