plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(project(":core"))

    // script
    api(kotlin("scripting-jvm"))
    api(kotlin("scripting-dependencies"))
    api("org.apache.ivy:ivy:2.5.0")

    api("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    val KOTLINBUKKITAPI_VERSION = "0.1.0-SNAPSHOT"

    val changing = Action<ExternalModuleDependency> { isChanging = true }

    listOf(
        "br.com.devsrsouza.kotlinbukkitapi:core:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:architecture:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:serialization:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:exposed:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:plugins:$KOTLINBUKKITAPI_VERSION"
    ).forEach {
        compileOnly(it, changing)
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    shadowJar {
        archiveClassifier.set("embedded")
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(120, "seconds")
}