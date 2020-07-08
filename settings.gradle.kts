rootProject.name = "Bukkript"

include("intellij", "script-definition", "script-host", "plugin", "core")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
