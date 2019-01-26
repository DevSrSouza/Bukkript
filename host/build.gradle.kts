repositories {  }
dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":script"))

    compile(kotlin("scripting-jvm-host"))
    compile(kotlin("reflect"))
}