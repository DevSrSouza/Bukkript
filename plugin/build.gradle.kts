import java.util.Properties

plugins {
    id(libs.plugins.bukkript.build.get().pluginId)
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginYml.bukkit)
    alias(libs.plugins.pluginYml.paper)
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.paper.api)
    compileOnly(libs.maven.resolver.transportHttp)
    compileOnly(libs.maven.resolver.connectorBasic)

    implementation(libs.bstats)
    
    implementation(projects.scriptHost)

    bukkitLibrary(kotlin("stdlib"))
    implementation(libs.kotlinbukkitapi.architecture) {
        // ignore this, this will be downloaded by spigot
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx") // TODO: remove when KBAPI Architecture no longer depend on Coroutines for no reason
    }

    // this is Called paper libraries but will also be used in Spigot
    // because spigot does not support non maven central repository
    // so the logic here is: we shadow kotlinbukkitapi-architecture only without Kotlin
    // we let kotlin stdlib be downloaded by Spigot and then with Kotlin + Architecture
    // the plugin loaded correctly, and we can the Maven Aether Resolver to fully resolve
    // dependencies from paper dependency file.
    // On Paper side, we just download Kotlin Stdlib because the rest will be avaiable at the
    // final paper dependencies files.
    paperLibrary(libs.coroutines)
    paperLibrary(libs.kotlinbukkitapi.coroutines)
    paperLibrary(libs.kotlinbukkitapi.utility)
    paperLibrary(libs.kotlinbukkitapi.extensions)
    paperLibrary(libs.kotlinbukkitapi.exposed)
    paperLibrary(libs.kotlinbukkitapi.commandLegacy)
    paperLibrary(libs.kotlinbukkitapi.menu)
    paperLibrary(libs.kotlinbukkitapi.scoreboardLegacy)

    // scripting dependencies
    paperLibrary(kotlin("scripting-jvm"))
    paperLibrary(kotlin("scripting-dependencies"))
    paperLibrary(kotlin("scripting-dependencies-maven"))
    paperLibrary(kotlin("scripting-jvm-host-unshaded"))
    //paperLibrary(kotlin("scripting-compiler-embeddable"))
}

tasks {
    shadowJar {
        archiveBaseName.set("Bukkript")
        val commitId = "git rev-parse --short=8 HEAD".runCommand(workingDir = rootDir)
        version = "$version-b$commitId"
        archiveClassifier.set("")
        
        relocate("org.bstats", "br.com.devsrsouza.bukkript.bstats")
    }
}

val pluginMain = "br.com.devsrsouza.bukkript.plugin.BukkriptPlugin"

bukkit {
    name = "Bukkript"
    main = pluginMain
    author = "DevSrSouza"
    website = "github.com.br/DevSrSouza"
    apiVersion = "1.19"
    
    description = "Bukkript Scripting."
}

paper {
    loader = "br.com.devsrsouza.bukkript.libraryresolver.PluginLibrariesLoader"
    generateLibrariesJson = true
    main = pluginMain
    description = "Bukkript Scripting."
    apiVersion = "1.19"
}

val localProperties = Properties()
    .apply {
        load(File(rootDir, "local.properties").inputStream())
    }

val serverPluginFolder = localProperties["serverPluginsFolder"]?.toString()
if(serverPluginFolder != null) {
    tasks.register<Copy>("copyToServer") {
        dependsOn(tasks.shadowJar)
        doFirst {
            File(serverPluginFolder).listFiles()?.filter { it.startsWith("Bukkript") && it.extension == "jar" }
                ?.forEach { it.delete() }
        }
        this.from(tasks.shadowJar.get().archiveFile.get().asFile)
        this.into(File(serverPluginFolder))
    }
}
