plugins {
    kotlin("jvm") version "1.3.72"
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    api(project(":script-definition"))

    // will provided by downloading at the plugin (scripting-jvm-host-embeddable)
    compileOnly(kotlin("scripting-common"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler-embeddable"))

    compileOnly("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}