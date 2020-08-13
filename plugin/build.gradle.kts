plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(project(":script-host"))

    // script
    api(kotlin("scripting-jvm"))
    api(kotlin("scripting-dependencies"))
    api(kotlin("scripting-compiler-embeddable"))
    api(kotlin("compiler-embeddable"))
    api("org.apache.ivy:ivy:2.5.0")

    api("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    val KOTLINBUKKITAPI_VERSION = "0.1.0-SNAPSHOT"

    val changing = Action<ExternalModuleDependency> { isChanging = true }

    listOf(
        "br.com.devsrsouza.kotlinbukkitapi:core:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:architecture:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:serialization:$KOTLINBUKKITAPI_VERSION"
    ).forEach {
        api(it, changing)
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

bukkit {
    name = "Bukkript"
    main = "br.com.devsrsouza.bukkript.plugin.BukkriptPlugin"
    author = "DevSrSouza"
    website = "devsrsouza.com.br"
    depend = listOf("KotlinBukkitAPI")

    description = "Bukkript Scripting."
}