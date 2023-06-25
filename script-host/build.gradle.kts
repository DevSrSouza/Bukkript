plugins {
    id(libs.plugins.bukkript.build.get().pluginId)
    //alias(libs.plugins.maven)
}

dependencies {
    compileOnly(libs.spigot.api)
    
    api(projects.scriptDefinition)
    
    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-compiler-embeddable"))
    
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-dependencies"))
    implementation(kotlin("scripting-dependencies-maven"))
}