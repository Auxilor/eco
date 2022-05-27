package com.mongodb.diagnostics.logging

object Loggers {
    private const val PREFIX = "org.mongodb.driver"

    @JvmStatic
    fun getLogger(suffix: String): Logger = NoOpLogger("$PREFIX.$suffix")
}
