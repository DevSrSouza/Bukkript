plugins {
    kotlin("jvm") version "1.4.0"
    id("maven-publish")
}

subprojects {
    plugins.apply("org.jetbrains.kotlin.jvm")
    plugins.apply("maven-publish")

    group = "br.com.devsrsouza.bukkript"
    version = "0.1.2-dev-SNAPSHOT"

    repositories {
        jcenter()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    tasks {
        compileKotlin {
            kotlinOptions{
                jvmTarget = "1.8"
                freeCompilerArgs += "-Xno-optimized-callable-references"
            }
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(120, "seconds")
    }

    afterEvaluate {
        publishing.publications.forEach {
            (it as? MavenPublication)?.pom {
                name.set("Bukkript")
                description.set("Bukkript is a Bukkit plugin that allows server admins to customize their server easily with the power of **Kotlin** language and **KotlinBukkitAPI**.")
                url.set("https://github.com/DevSrSouza/Bukkript")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/DevSrSouza/Bukkript/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("DevSrSouza")
                        name.set("Gabriel Souza")
                        email.set("devsrsouza@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/DevSrSouza/Bukkript/tree/master/" +
                            project.path.removePrefix(":").replace(":", "/"))
                }
            }
        }
    }
}
