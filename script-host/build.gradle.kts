plugins {
    id(libs.plugins.bukkript.build.get().pluginId)
    //alias(libs.plugins.maven)
}

dependencies {
    compileOnly(libs.spigot.api)
    
    api(projects.scriptDefinition)

    compileOnly(kotlin("scripting-jvm"))
    compileOnly(kotlin("scripting-jvm-host"))
    //compileOnly(kotlin("scripting-compiler-embeddable"))
}