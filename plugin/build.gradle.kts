plugins {
    id(libs.plugins.bukkript.build.get().pluginId)
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginYml)
}

dependencies {
    compileOnly(libs.spigot.api)
    
    implementation(libs.bstats)
    
    implementation(libs.coroutines)
    
    implementation(projects.scriptHost) {
        // Excluding aether already available at Spigot latest versions
        exclude("org.apache.maven.resolver")
        exclude("org.apache.maven.wagon")
        exclude("org.apache.maven.shared")
        exclude("org.apache.maven")
    }
    
    implementation(libs.kotlinbukkitapi.coroutines)
    implementation(libs.kotlinbukkitapi.utility)
    implementation(libs.kotlinbukkitapi.architecture)
    implementation(libs.kotlinbukkitapi.extensions)
    implementation(libs.kotlinbukkitapi.exposed)
    implementation(libs.kotlinbukkitapi.commandLegacy)
    implementation(libs.kotlinbukkitapi.menu)
    implementation(libs.kotlinbukkitapi.scoreboardLegacy)
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

bukkit {
    name = "Bukkript"
    main = "br.com.devsrsouza.bukkript.plugin.BukkriptPlugin"
    author = "DevSrSouza"
    website = "github.com.br/DevSrSouza"
    
    description = "Bukkript Scripting."
}

