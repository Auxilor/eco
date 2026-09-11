package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.scheduling.Scheduler
import com.willfp.eco.core.scheduling.TaskContext
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.DataYml
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test

/**
 * data.yml is written to for exactly as long as a migration is carrying profiles out of it: the
 * dual-write is what a half-finished copy resumes from on the next boot. Once the migration is
 * done the file is gone, and saving it on a timer would delete and rewrite -- or recreate -- a file
 * eco has just retired.
 */
class AutosaveTests {
    private val config = mockk<ConfigYml>()
    private val scheduler = mockk<Scheduler>()
    private val global = mockk<TaskContext>()
    private val dataYml = mockk<DataYml>(relaxed = true)
    private val plugin = mockk<EcoSpigotPlugin>()
    private val handler = mockk<ProfileHandler>()

    init {
        every { plugin.configYml } returns config
        every { plugin.scheduler } returns scheduler
        every { plugin.dataYml } returns dataYml
        every { scheduler.global() } returns global

        every { config.getInt("save-interval") } returns 1
        // config.yml no longer ships the key, so the writer's own default is what servers get.
        every { config.getIntOrNull("autosave-interval") } returns null
    }

    private fun tick(): Runnable {
        val task = slot<Runnable>()
        every { global.runTimer(36000L, 36000L, capture(task)) } returns mockk()

        ProfileWriter(plugin, handler).startTickingAutosave()

        return task.captured
    }

    @Test
    fun `the autosave timer saves data yml while a migration is reading it`() {
        every { handler.liveMigration } returns mockk()

        tick().run()

        verify(exactly = 1) { dataYml.save() }
    }

    @Test
    fun `the autosave timer leaves a retired data yml alone`() {
        every { handler.liveMigration } returns null

        tick().run()

        verify(exactly = 0) { dataYml.save() }
    }
}
