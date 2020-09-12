dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(Dep.spigot)

    api(project(":script-definition"))

    pdm(kotlin("scripting-common").toString(), excluding)
    pdm(kotlin("scripting-jvm-host").toString(), excluding)
    pdm(kotlin("scripting-compiler-embeddable").toString(), excluding)

    compileOnly(kotlin("scripting-dependencies-maven").toString(), excluding)
    compileOnly(kotlin("scripting-dependencies").toString(), excluding)
    compileOnly(kotlin("scripting-jvm").toString(), excluding)
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