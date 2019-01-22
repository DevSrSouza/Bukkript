import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20-eap-100"
}

group = "br.com.devsrsouza"
version = "1.0-SNAPSHOT"

subprojects {
    plugins.apply("org.jetbrains.kotlin.jvm")
}

allprojects {
    repositories {
        jcenter()
        mavenLocal()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }

        maven {
            name = "spigot"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
    }

    dependencies {
        compile(kotlin("stdlib-jdk8"))

        // spigot
        compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

        // kotlinbukkitapi
        compileOnly("br.com.devsrsouza.kotlinbukkitapi:core:0.1.0-SNAPSHOT")
        compileOnly("br.com.devsrsouza.kotlinbukkitapi:attributestorage:0.1.0-SNAPSHOT")
        compileOnly("br.com.devsrsouza.kotlinbukkitapi:plugins:0.1.0-SNAPSHOT")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
