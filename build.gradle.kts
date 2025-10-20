import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.configure

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.crashlytics) apply false

}

allprojects.onEach { project ->
    project.afterEvaluate {
        with(project.plugins) {
            if (hasPlugin(libs.plugins.kotlin.android.get().pluginId)
                 || hasPlugin(libs.plugins.jetbrains.kotlin.jvm.get().pluginId)
            ) {
                project.plugins.apply(libs.plugins.detekt.get().pluginId)

                project.extensions.configure<DetektExtension> {

                    config.setFrom(rootProject.files("default-detekt-config.yml"))
                }
            }
        }
    }
}
