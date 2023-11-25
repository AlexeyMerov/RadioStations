import com.alexeymerov.radiostations.androidTestImplementation
import com.alexeymerov.radiostations.getLibrary
import com.alexeymerov.radiostations.implementation
import com.alexeymerov.radiostations.ksp
import com.alexeymerov.radiostations.kspAndroidTest
import com.alexeymerov.radiostations.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply("com.google.devtools.ksp")
            }

            dependencies {
                implementation(libs.getLibrary("hilt-android"))
                ksp(libs.getLibrary("hilt-compiler"))
                androidTestImplementation(libs.getLibrary("hilt-testing"))
                kspAndroidTest(libs.getLibrary("hilt-compiler"))
            }
        }
    }
}
