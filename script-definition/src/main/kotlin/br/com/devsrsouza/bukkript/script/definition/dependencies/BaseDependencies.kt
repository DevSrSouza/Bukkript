package br.com.devsrsouza.bukkript.script.definition.dependencies

data class Dependency(
    val fqnPackage: String,
    val repositoriesUrl: List<String>,
    val artifacts: List<String>
)

private const val KOTLINBUKKITAPI_VERSION = "0.1.0-SNAPSHOT"

val KOTLINBUKKITAPI_DEPENDENCY = Dependency(
    "br.com.devsrsouza.kotlinbukkitapi",
    listOf("http://nexus.devsrsouza.com.br/repository/maven-public/"),
    listOf(
        "br.com.devsrsouza.kotlinbukkitapi:core:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:architecture:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:serialization:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:exposed:$KOTLINBUKKITAPI_VERSION",
        "br.com.devsrsouza.kotlinbukkitapi:plugins:$KOTLINBUKKITAPI_VERSION"
    )
)

val SPIGOT_DEPENDENCY = Dependency(
    "org.spigotmc",
    listOf(
        "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
        "https://oss.sonatype.org/content/repositories/snapshots/"
    ),
    listOf(
        "org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT"
    )
)

val baseDependencies = listOf(
    KOTLINBUKKITAPI_DEPENDENCY,
    SPIGOT_DEPENDENCY
)