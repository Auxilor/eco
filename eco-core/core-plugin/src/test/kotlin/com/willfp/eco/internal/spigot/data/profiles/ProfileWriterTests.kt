package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLitePersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.stubEcoConfigFactory
import com.willfp.eco.internal.spigot.data.handlers.unstubEcoConfigFactory
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoPlayerProfile
import com.willfp.eco.util.namespacedKeyOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * The writer hands every value on exactly once: a value replaced while the queue drains waits for
 * the next drain rather than being dropped, and a profile loaded again before its values are
 * saved reads the waiting value rather than the stored one.
 */
class ProfileWriterTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            stubEcoConfigFactory()
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unstubEcoConfigFactory()
        }
    }

    private val key = PersistentDataKey(namespacedKeyOf("writer", "int"), PersistentDataKeyType.INT, 0)

    private val defaultHandler = mockk<PersistentDataHandler>(relaxed = true)
    private val localHandler = mockk<SQLitePersistentDataHandler>(relaxed = true)
    private val handler = mockk<ProfileHandler>(relaxed = true)
    private val plugin = mockk<EcoSpigotPlugin>()
    private val config = mockk<ConfigYml>(relaxed = true)

    init {
        every { handler.defaultHandler } returns defaultHandler
        every { handler.localHandler } returns localHandler
        every { handler.liveMigration } returns null
        every { plugin.configYml } returns config
        every { config.getInt("save-interval") } returns 1
    }

    @Test
    fun `a mid-drain write is kept`() {
        val uuid = UUID.randomUUID()
        val writer = ProfileWriter(plugin, handler)

        every { defaultHandler.write(uuid, key, 1) } answers { writer.write(uuid, key, 2) }

        writer.write(uuid, key, 1)
        writer.flush()

        assertTrue(writer.isPending(uuid, key))
        assertEquals(2, writer.pendingValue(uuid, key))

        writer.flush()

        assertFalse(writer.isPending(uuid, key))
        verify(exactly = 1) { defaultHandler.write(uuid, key, 2) }
    }

    @Test
    fun `a reload reads pending values`() {
        val uuid = UUID.randomUUID()
        val writer = ProfileWriter(plugin, handler)

        every { handler.profileWriter } returns writer
        every { defaultHandler.read(uuid, key) } returns 1

        writer.write(uuid, key, 5)

        assertEquals(5, EcoPlayerProfile(uuid, handler).read(key))
        verify(exactly = 0) { defaultHandler.read(uuid, key) }
    }
}
