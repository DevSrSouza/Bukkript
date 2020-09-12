plugins {
    id("com.github.johnrengelman.shadow")
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

dependencies {
    implementation(Dep.bstats)
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(Dep.spigot)

    api(project(":script-host"))

    compileOnly(Dep.kotlinBukkitAPI.core, changing)
    compileOnly(Dep.kotlinBukkitAPI.serialization, changing)

    compileOnly(Dep.skedule)
    compileOnly(Dep.coroutinesCore)
}

tasks {
    shadowJar {
        dependsOn(pdm)
        archiveBaseName.set("Bukkript")
        archiveClassifier.set("")

        relocate("org.bstats", "br.com.devsrsouza.bukkript.bstats")
    }
}

pdm {
    projectRepository = "http://nexus.devsrsouza.com.br/repository/maven-public/"
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