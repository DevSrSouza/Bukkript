import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    java
    kotlin("jvm") version "1.3.72"
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")

    api("org.apache.ivy:ivy:2.5.0")
    api(project(":script-definition"))
    api("org.apache.ivy:ivy:2.5.0") // TODO: remove in the future
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1.2"

    setPlugins("java", "Kotlin")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    runIde {
        environment("project-dir", projectDir.parentFile.absolutePath)
    }

    runIde.get().dependsOn(":script-definition:shadowJar")
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}

listOf("compileKotlin", "compileTestKotlin").forEach {
    tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>(it) {
        kotlinOptions.jvmTarget = "1.8"
    }
}