package com.willfp.eco.core.sound

import com.willfp.eco.core.Eco
import com.willfp.eco.core.config.interfaces.Config
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PlayableSoundNumberTests {
    private lateinit var config: Config

    @BeforeEach
    fun setup() {
        config = mockk()

        mockkStatic(Eco::class)
        val eco = mockk<Eco>()
        every { Eco.get() } returns eco
        every { eco.evaluate("0.5 * 4", any()) } returns 2.0
        every { eco.evaluate("3 - 1", any()) } returns 2.0
        every { eco.evaluate("not a number", any()) } returns null
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(Eco::class)
    }

    @Test
    fun `plain number is read directly`() {
        every { config.getDoubleOrNull("pitch") } returns 1.5

        assertEquals(1.5, AbstractPlayableSound.readNumber(config, "pitch", 1.0))
    }

    @Test
    fun `expression string is evaluated`() {
        every { config.getDoubleOrNull("pitch") } returns null
        every { config.getStringOrNull("pitch") } returns "0.5 * 4"

        assertEquals(2.0, AbstractPlayableSound.readNumber(config, "pitch", 1.0))
    }

    @Test
    fun `missing path uses the fallback`() {
        every { config.getDoubleOrNull("pitch") } returns null
        every { config.getStringOrNull("pitch") } returns null

        assertEquals(1.0, AbstractPlayableSound.readNumber(config, "pitch", 1.0))
    }

    @Test
    fun `unevaluable expression uses the fallback`() {
        every { config.getDoubleOrNull("pitch") } returns null
        every { config.getStringOrNull("pitch") } returns "not a number"

        assertEquals(1.0, AbstractPlayableSound.readNumber(config, "pitch", 1.0))
    }

    @Test
    fun `range part reads a plain number`() {
        assertEquals(0.5, AbstractPlayableSound.readRangePart("0.5", 1.0))
    }

    @Test
    fun `range part evaluates an expression`() {
        assertEquals(2.0, AbstractPlayableSound.readRangePart("3 - 1", 1.0))
    }

    @Test
    fun `range part falls back when unevaluable`() {
        assertEquals(1.0, AbstractPlayableSound.readRangePart("not a number", 1.0))
    }
}
