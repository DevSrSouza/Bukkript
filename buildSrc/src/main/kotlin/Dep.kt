object Dep {
    val kotlinBukkitAPI = KotlinBukkitAPI

    val spigot = "org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT"
    val ivy = "org.apache.ivy:ivy:2.5.0"
    val skedule = "com.okkero.skedule:skedule:1.2.6"


    private val asmVersion = "7.1"
    val asm = "org.ow2.asm:asm:${asmVersion}"
    val asmCommons = "org.ow2.asm:asm-commons:${asmVersion}"

    object KotlinBukkitAPI {
        private val version = "0.2.0-SNAPSHOT"
        private val group = "br.com.devsrsouza.kotlinbukkitapi"

        val core = "$group:core:$version"
        val serialization = "$group:serialization:$version"
        val exposed = "$group:exposed:$version"
        val plugins = "$group:plugins:$version"
    }
}



