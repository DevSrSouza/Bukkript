dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    // script
    api(kotlin("scripting-jvm"))
    api(kotlin("scripting-dependencies"))
    api(Dep.ivy)

    compileOnly(Dep.spigot)
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    compileOnly(Dep.kotlinBukkitAPI.core, changing)
    compileOnly(Dep.kotlinBukkitAPI.exposed, changing)
    compileOnly(Dep.kotlinBukkitAPI.plugins, changing)
    compileOnly(Dep.kotlinBukkitAPI.serialization, changing)
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(120, "seconds")
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
            from(components["java"])
            artifact(sources.get())
        }
    }
}