rootProject.name = "Bukkript"

include("host", "script", "plugin")

pluginManagement {
    repositories {
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        gradlePluginPortal()
    }
}
