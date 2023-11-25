import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("radiostations.android.library")
            }
        }
    }
}