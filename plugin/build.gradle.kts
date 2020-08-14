plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

repositories {
    maven("https://kotlin.bintray.com/kotlin-dependencies")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation(project(":script-host"))
    implementation(project(":script-definition"))

    implementation(kotlin("scripting-dependencies-maven"))

    val KOTLINBUKKITAPI_VERSION = "0.1.0-SNAPSHOT"
    val changing = Action<ExternalModuleDependency> { isChanging = true }
    listOf(
        "br.com.devsrsouza.kotlinbukkitapi:core:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:serialization:$KOTLINBUKKITAPI_VERSION"
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
        archiveBaseName.set("Bukkript")
        archiveClassifier.set("")

        dependencies {
            this.exclude { dep ->
                listOf(
                    "kotlin-stdlib",
                    "kotlin-stdlib",
                    "kotlinx-coroutines-core",
                    "kotlinx-coroutines-core",

                    //https://github.com/JetBrains/kotlin/blob/master/libraries/scripting/dependencies-maven/build.gradle.kts#L9
                    "aether",
                    "maven-", // `-` for not remove the `dependencies-maven`
                    "wagon-provider"
                )
                    .any { it.contains(dep.moduleName, ignoreCase = true) }
            }
        }
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

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(120, "seconds")
}