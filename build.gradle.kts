plugins {
    alias(libs.plugins.ktlint) apply false
    id(libs.plugins.bukkript.build.get().pluginId) apply false
    alias(libs.plugins.dependencyGraph)
    //alias(libs.plugins.maven) apply false
    
}
