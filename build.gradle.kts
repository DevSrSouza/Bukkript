import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20-eap-100"
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "4.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    subprojects.forEach {
        compile(project(":${it.name}", configuration = "shadow"))
    }
}

bukkit {
    name = project.name
    version = project.version.toString()
    main = "br.com.devsrsouza.bukkript.Bukkript"

    website = "https://github.com/DevSrSouza/Bukkript"
    authors = listOf("DevSrSouza")

    depend = listOf("KotlinBukkitAPI")
}

allprojects {
    plugins.apply("org.jetbrains.kotlin.jvm")
    plugins.apply("com.github.johnrengelman.shadow")

    group = "br.com.devsrsouza.bukkript"
    version = "0.0.1-SNAPSHOT"

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
        // spigot
        compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

        // kotlinbukkitapi
        compileOnly("br.com.devsrsouza.kotlinbukkitapi:core:0.1.0-SNAPSHOT")
        compileOnly("br.com.devsrsouza.kotlinbukkitapi:attributestorage:0.1.0-SNAPSHOT")
        compileOnly("br.com.devsrsouza.kotlinbukkitapi:plugins:0.1.0-SNAPSHOT")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
        withType<ShadowJar> {
            baseName = project.name
            classifier = null
        }
    }
}

subprojects {
    plugins.apply("maven-publish")

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
    }

    val sources by tasks.registering(Jar::class) {
        baseName = "Bukkript-${project.name}"
        classifier = "sources"
        version = null
        from(sourceSets.getByName("main").allSource)
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
                artifact(sources.get())
                groupId = project.group.toString()
                artifactId = project.name.toLowerCase()
                version = project.version.toString()
                pom.withXml {
                    asNode().apply {
                        appendNode(
                            "description",
                            "Bukkript is a Bukkit plugin that allows server admins to customize their" +
                                    " server easily with the power of Kotlin language and KotlinBukkitAPI."
                        )
                        appendNode("name", "Bukkript-${project.name}")
                        appendNode("url", "https://github.com/DevSrSouza/Bukkript")

                        appendNode("licenses").appendNode("license").apply {
                            appendNode("name", "MIT License")
                            appendNode("url", "https://github.com/DevSrSouza/Bukkript/blob/master/LICENSE")
                            appendNode("distribution", "repo")
                        }
                        appendNode("developers").apply {
                            appendNode("developer").apply {
                                appendNode("id", "DevSrSouza")
                                appendNode("name", "Gabriel Souza")
                                appendNode("email", "devsrsouza@gmail.com")
                            }
                        }
                        appendNode("scm").appendNode("url", "https://github.com/DevSrSouza/Bukkript/tree/master/${project.name}")
                    }
                    asElement().apply {
                        getElementsByTagName("dependencies")?.item(0)?.also { removeChild(it) }
                    }
                }
            }
        }
    }
}