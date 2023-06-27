enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// plugin-yml plugin does not support this way of repositories
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        mavenCentral()
//        maven("https://repo.papermc.io/repository/maven-public/")
//        maven("https://raw.githubusercontent.com/KotlinMinecraft/KotlinBukkitAPI-Repository/main")
//    }
//}

rootProject.name = "Bukkript"

include(
    "script-definition",
    "script-definition-embedded",
    "script-host",
    "plugin",
)

plugins {
    id("com.gradle.enterprise") version("3.13")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}