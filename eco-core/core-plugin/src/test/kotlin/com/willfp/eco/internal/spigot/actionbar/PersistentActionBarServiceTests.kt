package com.willfp.eco.internal.spigot.actionbar

import com.willfp.eco.core.EcoPlugin
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PersistentActionBarServiceTests {
    private var now = 1_000_000L

    private val sent = mutableListOf<Component>()

    private val logRecords = mutableListOf<LogRecord>()

    private val logger = Logger.getAnonymousLogger().apply {
        useParentHandlers = false
        addHandler(object : Handler() {
            override fun publish(record: LogRecord) {
                logRecords += record
            }

            override fun flush() = Unit

            override fun close() = Unit
        })
    }

    private val service = PersistentActionBarService(logger, { now }) { _, component -> sent += component }

    private val plugin = mockk<EcoPlugin> { every { id } returns "test" }

    private val otherPlugin = mockk<EcoPlugin> { every { id } returns "other" }

    private val player = mockk<Player> { every { uniqueId } returns UUID.randomUUID() }

    private val first = Component.text("first")

    private val second = Component.text("second")

    private fun tickAfter(millis: Long) {
        now += millis
        service.tick(player)
    }

    @Test
    fun `highest priority wins`() {
        service.register(plugin, "low", 10) { first }
        service.register(plugin, "high", 20) { second }

        service.tick(player)

        assertEquals(listOf(second), sent)
    }

    @Test
    fun `ties go to the bar registered first`() {
        service.register(plugin, "a", 10) { first }
        service.register(plugin, "b", 10) { second }

        service.tick(player)

        assertEquals(listOf(first), sent)
    }

    @Test
    fun `inactive bar falls through to lower priority`() {
        service.register(plugin, "low", 10) { first }
        service.register(plugin, "high", 20) { null }

        service.tick(player)

        assertEquals(listOf(first), sent)
    }

    @Test
    fun `throwing provider is skipped and logged once`() {
        service.register(plugin, "low", 10) { first }
        service.register(plugin, "broken", 20) { error("broken") }

        service.tick(player)
        tickAfter(2000)

        assertEquals(listOf(first, first), sent)
        assertEquals(1, logRecords.size)
    }

    @Test
    fun `unchanged bar is not resent`() {
        service.register(plugin, "bar", 10) { first }

        service.tick(player)
        tickAfter(250)
        tickAfter(250)

        assertEquals(listOf(first), sent)
    }

    @Test
    fun `changed bar is resent`() {
        var shown = first
        service.register(plugin, "bar", 10) { shown }

        service.tick(player)
        shown = second
        tickAfter(250)

        assertEquals(listOf(first, second), sent)
    }

    @Test
    fun `unchanged bar is kept alive every 2000 ms`() {
        service.register(plugin, "bar", 10) { first }

        service.tick(player)
        tickAfter(1999)
        tickAfter(1)

        assertEquals(listOf(first, first), sent)
    }

    @Test
    fun `refresh forces a send`() {
        service.register(plugin, "bar", 10) { first }

        service.tick(player)
        service.refresh(player)
        tickAfter(250)
        tickAfter(250)

        assertEquals(listOf(first, first), sent)
    }

    @Test
    fun `foreign bar pauses for 2700 ms then sends at once`() {
        service.register(plugin, "bar", 10) { first }

        service.tick(player)
        now += 500
        service.onForeignActionBar(player)
        tickAfter(2699)
        tickAfter(1)

        assertEquals(listOf(first, first), sent)
    }

    @Test
    fun `own send within 50 ms is not foreign`() {
        var shown = first
        service.register(plugin, "bar", 10) { shown }

        service.tick(player)
        now += 49
        service.onForeignActionBar(player)
        shown = second
        tickAfter(201)

        assertEquals(listOf(first, second), sent)
    }

    @Test
    fun `one empty send when every bar goes inactive`() {
        var active = true
        service.register(plugin, "bar", 10) { if (active) first else null }

        service.tick(player)
        active = false
        tickAfter(250)
        tickAfter(2500)

        assertEquals(listOf(first, Component.empty()), sent)
    }

    @Test
    fun `unregister clears shown bar once`() {
        val bar = service.register(plugin, "bar", 10) { first }

        service.tick(player)
        bar.unregister()
        tickAfter(250)
        tickAfter(250)

        assertEquals(listOf(first, Component.empty()), sent)
    }

    @Test
    fun `registering the same key replaces the bar`() {
        service.register(plugin, "bar", 10) { first }
        service.register(plugin, "bar", 10) { second }
        service.register(otherPlugin, "bar", 5) { first }

        service.tick(player)

        assertEquals(listOf(second), sent)
    }

    @Test
    fun `unregister of a replaced bar keeps the replacement`() {
        val old = service.register(plugin, "bar", 10) { first }
        service.register(plugin, "bar", 10) { second }

        old.unregister()
        service.tick(player)

        assertEquals(listOf(second), sent)
    }

    @Test
    fun `unregisterAll removes only that plugin's bars`() {
        service.register(plugin, "high", 20) { first }
        service.register(otherPlugin, "low", 10) { second }

        service.unregisterAll(plugin)
        service.tick(player)

        assertEquals(listOf(second), sent)
    }

    @Test
    fun `removing a player resets their state`() {
        service.register(plugin, "bar", 10) { first }

        service.tick(player)
        service.remove(player)
        tickAfter(250)

        assertEquals(listOf(first, first), sent)
    }

    @Test
    fun `foreign bar with no bars registered does nothing`() {
        service.onForeignActionBar(player)
        service.register(plugin, "bar", 10) { first }

        tickAfter(250)

        assertEquals(listOf(first), sent)
    }

    @Test
    fun `pause end with no active bar sends nothing`() {
        var active = true
        service.register(plugin, "bar", 10) { if (active) first else null }

        service.tick(player)
        now += 100
        service.onForeignActionBar(player)
        active = false
        tickAfter(2700)
        tickAfter(250)

        assertEquals(listOf(first), sent)
    }

    @Test
    fun `tick with no bars sends nothing`() {
        service.tick(player)
        tickAfter(2000)

        assertEquals(emptyList<Component>(), sent)
    }
}
