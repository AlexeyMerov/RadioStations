import com.alexeymerov.radiostations.androidTestImplementation
import com.alexeymerov.radiostations.getLibrary
import com.alexeymerov.radiostations.implementation
import com.alexeymerov.radiostations.ksp
import com.alexeymerov.radiostations.libs
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.process.CommandLineArgumentProvider
import java.io.File

@Suppress("unused")
class RoomPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }

            extensions.configure<KspExtension> {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable Room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
            }

            dependencies {
                implementation(libs.getLibrary("room-runtime"))
                implementation(libs.getLibrary("room-ktx"))
                ksp(libs.getLibrary("room-compiler"))

                androidTestImplementation(libs.getLibrary("room-testing"))
            }

        }
    }
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> = listOf("room.schemaLocation=${schemaDir.path}")
}