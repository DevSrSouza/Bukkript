plugins {
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    maven("https://kotlin.bintray.com/kotlin-dependencies")
}

dependencies {
    implementation(project(":script-host"))
    implementation(project(":script-definition"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        dependencies {
            this.exclude { dep ->
                listOf(
                    "kotlin-stdlib",
                    "kotlinx-coroutines-core"
                ).any {
                    dep.moduleName.contains(it, ignoreCase = true)
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            shadow.component(this)
        }
    }
}