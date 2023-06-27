plugins {
    alias(libs.plugins.ktlint) apply false
    id(libs.plugins.bukkript.build.get().pluginId) apply false
    alias(libs.plugins.dependencyGraph)
    //alias(libs.plugins.maven) apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://raw.githubusercontent.com/KotlinMinecraft/KotlinBukkitAPI-Repository/main")
    }
}