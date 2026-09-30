package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.scheduling.Scheduler
import com.willfp.eco.core.scheduling.TaskContext
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLitePersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.stubEcoConfigFactory
import com.willfp.eco.internal.spigot.data.handlers.unstubEcoConfigFactory
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoPlayerProfile
import com.willfp.eco.util.namespacedKeyOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Cross-server sync drops a cached value when another server changes it, and leaves it alone while
 * this server has a newer one waiting to be saved. These are the two hooks it does that through.
 */
class ProfileSyncHookTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() = stubEcoConfigFactory()

        @JvmStatic
        @AfterAll
        fun unstubEco() = unstubEcoConfigFactory()
    }

    private val sharedKey =
        PersistentDataKey(namespacedKeyOf("synchook", "shared"), PersistentDataKeyType.INT, 0)
    private val localKey =
        PersistentDataKey(namespacedKeyOf("synchook", "local"), PersistentDataKeyType.INT, 0, true)

    private val defaultHandler = mockk<PersistentDataHandler>(relaxed = true)
    private val localHandler = mockk<SQLitePersistentDataHandler>(relaxed = true)
    private val handler = mockk<ProfileHandler>(relaxed = true)

    init {
        every { handler.defaultHandler } returns defaultHandler
        every { handler.localHandler } returns localHandler
        every { handler.liveMigration } returns null
    }

    @Test
    fun `an invalidated key is read again from the handler`() {
        val uuid = UUID.randomUUID()
        every { defaultHandler.read(uuid, sharedKey) } returnsMany listOf(1, 2)
        val profile = EcoPlayerProfile(uuid, handler)

        assertEquals(1, profile.read(sharedKey))
        assertEquals(1, profile.read(sharedKey))

        profile.invalidate(sharedKey)

        assertEquals(2, profile.read(sharedKey))
    }

    @Test
    fun `invalidating shared keys keeps local keys cached`() {
        val uuid = UUID.randomUUID()
        every { defaultHandler.read(uuid, sharedKey) } returnsMany listOf(1, 2)
        every { localHandler.read(uuid, localKey) } returnsMany listOf(10, 20)
        val profile = EcoPlayerProfile(uuid, handler)

        profile.read(sharedKey)
        profile.read(localKey)

        profile.invalidateShared()

        assertEquals(2, profile.read(sharedKey))
        assertEquals(10, profile.read(localKey))
    }

    @Test
    fun `a write is pending until the tick saves it`() {
        val uuid = UUID.randomUUID()
        val config = mockk<ConfigYml>(relaxed = true)
        val scheduler = mockk<Scheduler>()
        val global = mockk<TaskContext>()
        val plugin = mockk<EcoSpigotPlugin>()
        val task = slot<Runnable>()

        every { plugin.configYml } returns config
        every { plugin.scheduler } returns scheduler
        every { scheduler.global() } returns global
        every { config.getInt("save-interval") } returns 1
        every { global.runTimer(any(), any(), capture(task)) } returns mockk()

        val writer = ProfileWriter(plugin, handler)
        writer.startTickingSaves()

        assertFalse(writer.isPending(uuid, sharedKey))

        writer.write(uuid, sharedKey, 5)
        assertTrue(writer.isPending(uuid, sharedKey))

        task.captured.run()
        assertFalse(writer.isPending(uuid, sharedKey))
        verify { defaultHandler.write(uuid, sharedKey, 5) }
    }

    @Test
    fun `a registered key is found by its id`() {
        assertSame(sharedKey, KeyRegistry.getKey(sharedKey.key))
        assertNull(KeyRegistry.getKey(namespacedKeyOf("synchook", "never_registered")))
    }
}
