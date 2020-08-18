plugins {
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

repositories {
    maven("https://kotlin.bintray.com/kotlin-dependencies")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation(project(":script-host"))
    implementation(project(":script-definition"))

    implementation(kotlin("scripting-dependencies-maven") as String) {
        isTransitive = true
    }

    val KOTLINBUKKITAPI_VERSION = "0.1.0-SNAPSHOT"
    val changing = Action<ExternalModuleDependency> { isChanging = true }
    listOf(
        "br.com.devsrsouza.kotlinbukkitapi:core:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:serialization:$KOTLINBUKKITAPI_VERSION"
    ).forEach {
        compileOnly(it, changing)
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("Bukkript")
        archiveClassifier.set("")

        dependencies {
            this.exclude { dep ->
                listOf(
                    "kotlin-stdlib",
                    "kotlinx-coroutines-core"
                ).any {
                    dep.moduleName.contains(it, ignoreCase = true)
                }
            }
        }
    }
}

bukkit {
    name = "Bukkript"
    main = "br.com.devsrsouza.bukkript.plugin.BukkriptPlugin"
    author = "DevSrSouza"
    website = "devsrsouza.com.br"
    depend = listOf("KotlinBukkitAPI")

    description = "Bukkript Scripting."
}

val sources by tasks.registering(Jar::class) {
    baseName = project.name
    classifier = "sources"
    version = null
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(sources.get())
        }
    }
}