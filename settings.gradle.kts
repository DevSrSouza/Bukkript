rootProject.name = "Bukkript"

include("intellij", "script-definition", "script-definition-embedded", "script-host", "plugin")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
