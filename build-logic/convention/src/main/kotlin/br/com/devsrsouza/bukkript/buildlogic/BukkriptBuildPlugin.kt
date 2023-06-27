package br.com.devsrsouza.bukkript.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class BukkriptBuildPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply(libs.findPlugin("kotlin-jvm").get().get().pluginId)
            pluginManager.apply(libs.findPlugin("ktlint").get().get().pluginId)

            plugins.withType<KotlinBasePlugin> {
                extensions.configure<KotlinJvmProjectExtension> {
                    jvmToolchain(17) // TODO: upgrade to 11?
                }
                tasks.withType<KotlinCompile> {
                    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
                }
            }
        }
    }

}