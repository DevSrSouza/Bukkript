plugins {
    `kotlin-dsl`
}
repositories {
    jcenter()
    google()
    gradlePluginPortal()
    mavenLocal()
}
dependencies {
    compileOnly(gradleApi())
    implementation("com.github.jengelman.gradle.plugins:shadow:6.0.0")
}