import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency

val changing = Action<ExternalModuleDependency> { isChanging = true }

val excluding: ExternalModuleDependency.() -> Unit = {
    excludeModule("kotlin-stdlib")
    excludeModule("kotlin-reflect")
    excludeModule("kotlinx-coroutines-core")
}

fun ExternalModuleDependency.excludeModule(module: String) = exclude(mutableMapOf("module" to module))