plugins {
    id(libs.plugins.bukkript.build.get().pluginId)
    //alias(libs.plugins.maven)
}

dependencies {
    compileOnly(libs.spigot.api)

    compileOnly(kotlin("scripting-jvm"))
    compileOnly(kotlin("scripting-dependencies"))
    compileOnly(kotlin("scripting-dependencies-maven"))

    compileOnly(libs.coroutines)

    compileOnly(libs.kotlinbukkitapi.coroutines)
    compileOnly(libs.kotlinbukkitapi.utility)
    compileOnly(libs.kotlinbukkitapi.architecture)
    compileOnly(libs.kotlinbukkitapi.extensions)
    compileOnly(libs.kotlinbukkitapi.exposed)
    compileOnly(libs.kotlinbukkitapi.commandLegacy)
    compileOnly(libs.kotlinbukkitapi.menu)
    compileOnly(libs.kotlinbukkitapi.scoreboardLegacy)
    //implementation(libs.kotlinbukkitapi.serialization) TODO: support Ktx Serialization at scripts
}