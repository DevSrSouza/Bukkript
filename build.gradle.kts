allprojects {
    group = "br.com.devsrsouza.bukkript"
    version = "0.1.0-SNAPSHOT"

    repositories {
        jcenter()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
