package com.willfp.eco.internal.spigot.data.handlers

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PersistentDataHandlersRegistryTests {
    @Test
    fun `sqlite is a registered handler`() {
        assertNotNull(PersistentDataHandlers["sqlite"])
    }

    @Test
    fun `yaml is no longer a registered handler`() {
        // yaml survives as a migration source only, reached through its own factory, so a config
        // asking for it goes through resolveHandlerId instead of finding a writable yaml handler.
        assertNull(PersistentDataHandlers["yaml"])
    }

    @Test
    fun `the database handlers are untouched`() {
        for (id in listOf("mysql", "mariadb", "mongodb", "mongo")) {
            assertNotNull(PersistentDataHandlers[id], "expected $id to stay registered")
        }
    }
}
