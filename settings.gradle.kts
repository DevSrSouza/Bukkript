rootProject.name = "Bukkript"

include("api", "plugin", "host", "script")

pluginManagement {
    repositories {
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        gradlePluginPortal()
    }
}
