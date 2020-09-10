plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
    jcenter()
    google()
    gradlePluginPortal()
}
dependencies {
    compileOnly(gradleApi())
    implementation("com.github.jengelman.gradle.plugins:shadow:6.0.0")
}