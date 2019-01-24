repositories {  }
dependencies {
    compileOnly(project(":plugin"))
    compileOnly(project(":script"))

    compile(kotlin("scripting-jvm-host"))
    compile(kotlin("reflect"))
}