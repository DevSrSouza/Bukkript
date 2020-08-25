dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(project(":plugin")) { isTransitive = true }
    compileOnly(Dep.spigot)

    compileOnly(project(":script-host"))
    compileOnly(project(":script-definition"))

    compileOnly(Dep.kotlinBukkitAPI.core, changing)
    compileOnly(Dep.kotlinBukkitAPI.serialization, changing)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}