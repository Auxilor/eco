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
 * data.yml no longer holds profile data, but it still holds eco's own bookkeeping -- and the local
 * handler that used to flush it is a database now, which reports that it does not autosave. Without
 * this the timer became a no-op and nothing ever committed the file.
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
        every { config.getInt("autosave-interval") } returns 36000
    }

    @Test
    fun `the autosave timer saves data yml`() {
        val task = slot<Runnable>()
        every { global.runTimer(36000L, 36000L, capture(task)) } returns mockk()

        ProfileWriter(plugin, handler).startTickingAutosave()
        task.captured.run()

        verify(exactly = 1) { dataYml.save() }
    }
}
