plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":script-definition")) {
        exclude("module" to "pdm")
    }
    api(kotlin("stdlib-jdk8"))

    api(kotlin("scripting-jvm"))
    api(kotlin("scripting-dependencies"))
    api(kotlin("scripting-dependencies-maven"))

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