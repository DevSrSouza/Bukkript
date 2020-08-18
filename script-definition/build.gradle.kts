plugins {
    kotlin("jvm") version "1.4.0"
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    // script
    api(kotlin("scripting-jvm"))
    api(kotlin("scripting-dependencies"))
    api("org.apache.ivy:ivy:2.5.0")

    compileOnly("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    val KOTLINBUKKITAPI_VERSION = "0.1.0-SNAPSHOT"
    val changing = Action<ExternalModuleDependency> { isChanging = true }
    listOf(
        "br.com.devsrsouza.kotlinbukkitapi:core:$KOTLINBUKKITAPI_VERSION",
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