package com.willfp.eco.internal.config.handler

import com.willfp.eco.core.EcoPlugin
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner

class ReflectiveConfigHandler(
    private val plugin: EcoPlugin
) : SimpleConfigHandler() {
    private val reflections: Reflections = Reflections(
        this.plugin::class.java.classLoader,
        MethodAnnotationsScanner()
    )

    override fun callUpdate() {
        @Suppress("DEPRECATION", "REMOVAL")
        for (method in reflections.getMethodsAnnotatedWith(com.willfp.eco.core.config.updating.ConfigUpdater::class.java)) {
            runCatching {
                when (method.parameterCount) {
                    0 -> method.invoke(null)
                    1 -> method.invoke(null, this.plugin)
                    else -> throw InvalidUpdateMethodException("Update method must have 0 parameters or a plugin parameter.")
                }
            }.onFailure {
                it.printStackTrace()
                plugin.logger.severe("Update method ${method.toGenericString()} generated an exception")
            }
        }
    }
}

class InvalidUpdateMethodException(message: String) : RuntimeException(message)
