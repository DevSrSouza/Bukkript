group = "br.com.devsrsouza"
version = "1.0-SNAPSHOT"

repositories {  }
dependencies {
    compileOnly(project(":plugin"))
    compileOnly(project(":script"))

    compile(kotlin("scripting-jvm-host"))
}