import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency

val changing = Action<ExternalModuleDependency> { isChanging = true }