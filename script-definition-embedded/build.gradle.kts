plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

dependencies {
    api(project(":script-definition"))
    api(kotlin("stdlib-jdk8"))

    api("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(120, "seconds")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            shadow.component(this)
        }
    }
}