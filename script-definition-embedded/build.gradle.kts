plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

dependencies {
    api(project(":script-definition"))
    api(kotlin("stdlib-jdk8"))

    api("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    /*val KOTLINBUKKITAPI_VERSION = "0.1.0-SNAPSHOT"
    val changing = Action<ExternalModuleDependency> { isChanging = true }
    listOf(
        "br.com.devsrsouza.kotlinbukkitapi:core:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:serialization:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:exposed:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:plugins:$KOTLINBUKKITAPI_VERSION"
    ).forEach {
        implementation(it, changing)
    }*/
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(120, "seconds")
}