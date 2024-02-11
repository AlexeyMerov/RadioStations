import com.alexeymerov.radiostations.androidTestImplementation
import com.alexeymerov.radiostations.androidTestUtil
import com.alexeymerov.radiostations.getLibrary
import com.alexeymerov.radiostations.libs
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidTestPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("radiostations.android.library")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "com.alexeymerov.radiostations.core.test.HiltTestRunner"
                    testInstrumentationRunnerArguments["clearPackageData"] = "true"
                }

                @Suppress("UnstableApiUsage")
                testOptions {
                    execution = "ANDROIDX_TEST_ORCHESTRATOR"
                    unitTests.isIncludeAndroidResources = true
                }
            }

            dependencies {
                androidTestImplementation(project(":core:test"))
                androidTestUtil(libs.getLibrary("test-orchestrator"))
            }
        }
    }
}