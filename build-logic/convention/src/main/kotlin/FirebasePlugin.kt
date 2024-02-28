import com.alexeymerov.radiostations.getLibrary
import com.alexeymerov.radiostations.implementation
import com.alexeymerov.radiostations.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FirebasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
                apply("com.google.firebase.firebase-perf")
            }

            dependencies {
                implementation(platform(libs.getLibrary("firebase-bom")))
                implementation(libs.getLibrary("firebase-crashlytics"))
                implementation(libs.getLibrary("firebase-perf"))
            }
        }
    }
}
