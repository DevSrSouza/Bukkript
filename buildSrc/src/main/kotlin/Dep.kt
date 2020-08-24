object Dep {
    val kotlinBukkitAPI = KotlinBukkitAPI

    val spigot = "org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT"
    val ivy = "org.apache.ivy:ivy:2.5.0"

    object KotlinBukkitAPI {
        private val version = "0.1.0-SNAPSHOT"
        private val group = "br.com.devsrsouza.kotlinbukkitapi"

        val core = "$group:core:$version"
        val serialization = "$group:serialization:$version"
        val exposed = "$group:exposed:$version"
        val plugins = "$group:plugins:$version"
    }
}



