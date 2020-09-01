dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(Dep.spigot)

    api(project(":script-definition"))

    api(kotlin("scripting-common"))
    api(kotlin("scripting-jvm-host"))
    api(kotlin("scripting-compiler-embeddable"))

    api("org.ow2.asm:asm:8.0.1")
    api("org.ow2.asm:asm-commons:8.0.1")
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