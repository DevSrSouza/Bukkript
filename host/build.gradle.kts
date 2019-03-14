repositories {  }
dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":script"))

    compileOnly(kotlin("scripting-jvm-host"))
    shadow(kotlin("reflect"))
    //compile("br.com.devsrsouza.bukkript:script:0.0.1-SNAPSHOT")

    // embeded compiler
    shadow(kotlin("compiler-embeddable"))
    shadow(kotlin("scripting-jvm-host-embeddable"))
}