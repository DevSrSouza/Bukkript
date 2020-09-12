plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
    jcenter()
    google()
    gradlePluginPortal()
    mavenLocal()
}
dependencies {
    compileOnly(gradleApi())
    implementation("com.github.jengelman.gradle.plugins:shadow:6.0.0")
    implementation("me.bristermitten:pdm-gradle:0.0.28")
}