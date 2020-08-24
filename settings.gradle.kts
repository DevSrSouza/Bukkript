rootProject.name = "Bukkript"

include("script-definition", "script-definition-embedded", "script-host", "plugin", "script-host-embedded", "cli")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
