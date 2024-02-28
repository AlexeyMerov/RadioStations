import com.alexeymerov.radiostations.androidTestImplementation
import com.alexeymerov.radiostations.androidTestUtil
import com.alexeymerov.radiostations.getLibrary
import com.alexeymerov.radiostations.libs
import com.alexeymerov.radiostations.testImplementation
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class TestPlugin : Plugin<Project> {
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
                    unitTests.isReturnDefaultValues = true
                }
            }

            extensions.configure<LibraryAndroidComponentsExtension> {
                beforeVariants {
                    it.enableAndroidTest = it.enableAndroidTest && projectDir.resolve("src/androidTest").exists()
                }
            }

            dependencies {
                testImplementation(project(":core:test"))
                androidTestImplementation(project(":core:test"))

                androidTestUtil(libs.getLibrary("test-orchestrator"))
                testImplementation(libs.getLibrary("robolectric"))

                testImplementation(libs.getLibrary("turbine"))
                androidTestImplementation(libs.getLibrary("turbine"))
            }
        }
    }
}