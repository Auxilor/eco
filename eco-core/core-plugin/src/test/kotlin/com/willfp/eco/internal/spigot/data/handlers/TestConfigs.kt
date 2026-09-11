package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.Eco
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.internal.config.EcoConfigSection
import com.willfp.eco.internal.config.toMap
import com.willfp.eco.internal.spigot.data.KeyRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.bukkit.NamespacedKey

/**
 * The CONFIG key type serializes through [com.willfp.eco.core.config.readConfig], which reaches the
 * eco singleton, so a config round trip cannot be tested against a relaxed mock -- it has to answer
 * with a real config implementation.
 */
fun stubEcoConfigFactory() {
    val eco = mockk<Eco>(relaxed = true)

    every { eco.createConfig(any<String>(), any<ConfigType>()) } answers {
        val type = secondArg<ConfigType>()
        EcoConfigSection(type, type.toMap(firstArg<String>()))
    }

    every { eco.createConfig(any<Map<String, Any>>(), any<ConfigType>()) } answers {
        EcoConfigSection(secondArg(), firstArg<Map<String, Any>>())
    }

    // A value's stored path and column are built from the key's string form, so a mocked key
    // would read and write at a path no fixture can be written against.
    every { eco.createNamespacedKey(any(), any()) } answers {
        NamespacedKey(firstArg<String>(), secondArg<String>())
    }

    // getSavedUUIDs decides which tables to scan from the key registry, which is where the real
    // implementation routes key registration.
    every { eco.registerPersistentKey(any()) } answers { KeyRegistry.registerKey(firstArg()) }

    mockkStatic(Eco::class)
    every { Eco.get() } returns eco
}

fun unstubEcoConfigFactory() {
    unmockkStatic(Eco::class)
}

fun configOf(vararg values: Pair<String, Any?>): Config =
    EcoConfigSection(ConfigType.YAML, mapOf(*values))
