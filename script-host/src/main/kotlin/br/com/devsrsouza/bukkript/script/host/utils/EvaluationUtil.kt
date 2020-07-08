package br.com.devsrsouza.bukkript.script.host.utils

import kotlin.script.experimental.jvm.JvmScriptEvaluationConfigurationKeys
import kotlin.script.experimental.util.PropertiesCollection

val JvmScriptEvaluationConfigurationKeys.actualClassLoader by PropertiesCollection.key<ClassLoader?>(isTransient = true)