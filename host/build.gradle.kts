repositories {  }
dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":script"))

    compileOnly(kotlin("scripting-jvm-host"))
    shadow(kotlin("reflect"))

    // embeded compiler
    shadow(kotlin("compiler-embeddable"))
    shadow(kotlin("scripting-jvm-host-embeddable"))
}