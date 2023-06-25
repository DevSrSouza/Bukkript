plugins {
    id(libs.plugins.bukkript.build.get().pluginId)
    //alias(libs.plugins.maven)
}

dependencies {
    compileOnly(libs.spigot.api)
    
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-dependencies"))
    implementation(kotlin("scripting-dependencies-maven"))
    
    implementation(libs.coroutines)
    
    implementation(libs.kotlinbukkitapi.coroutines)
    implementation(libs.kotlinbukkitapi.utility)
    implementation(libs.kotlinbukkitapi.architecture)
    implementation(libs.kotlinbukkitapi.extensions)
    implementation(libs.kotlinbukkitapi.exposed)
    implementation(libs.kotlinbukkitapi.commandLegacy)
    implementation(libs.kotlinbukkitapi.menu)
    implementation(libs.kotlinbukkitapi.scoreboardLegacy)
    //implementation(libs.kotlinbukkitapi.serialization) TODO: support Ktx Serialization at scripts
}