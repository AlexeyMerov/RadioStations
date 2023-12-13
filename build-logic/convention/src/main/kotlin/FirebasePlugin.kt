import com.alexeymerov.radiostations.getLibrary
import com.alexeymerov.radiostations.implementation
import com.alexeymerov.radiostations.libs
import com.android.build.api.dsl.ApplicationExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class FirebasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
            }

            dependencies {
                implementation(platform(libs.getLibrary("firebase-bom")))
                implementation(libs.getLibrary("firebase-crashlytics"))
            }

            extensions.configure<ApplicationExtension> {
                // To prevent the Crashlytics Gradle plugin from uploading the mapping file for variants that use obfuscation
                // Use App Quality Insights or https://github.com/Guardsquare/proguard for ReTrace your logs
                // https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android#keep-obfuscated-build-variants
                buildTypes.configureEach {
                    configure<CrashlyticsExtension> {
                        mappingFileUploadEnabled = false
                    }
                }
            }
        }
    }
}
