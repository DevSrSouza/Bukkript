rootProject.name = "Bukkript"

include(
    "script-definition", "script-definition-embedded",
    "script-host", "script-host-embedded",
    "plugin"
)

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
