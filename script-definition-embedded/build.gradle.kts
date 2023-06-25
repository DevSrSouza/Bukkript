plugins {
    id(libs.plugins.bukkript.build.get().pluginId)
    //alias(libs.plugins.maven)
}

dependencies {
    implementation(libs.spigot.api)
    
    implementation(projects.scriptDefinition)
}