plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":script-definition"))
    api(kotlin("stdlib-jdk8"))

    api(Dep.spigot)
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            shadow.component(this)
        }
    }
}