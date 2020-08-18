dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    api(project(":script-definition"))

    // will provided by downloading at the plugin (scripting-jvm-host-embeddable)
    compileOnly(kotlin("scripting-common"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler-embeddable"))

    compileOnly("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
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