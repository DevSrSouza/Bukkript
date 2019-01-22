group = "br.com.devsrsouza"
version = "1.0-SNAPSHOT"

repositories {  }
dependencies {
    compileOnly(project(":plugin"))

    compile(kotlin("scripting-jvm"))
    compile(kotlin("script-util"))

    compile("com.jcabi:jcabi-aether:0.10.1")
    compile("org.sonatype.aether:aether-api:1.13.1")
    compile("org.apache.maven:maven-core:3.0.3")
}