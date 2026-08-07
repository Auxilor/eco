package com.willfp.eco.util

import com.willfp.eco.core.Eco
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for [StringUtils.createProgressBar].
 *
 * The three format strings are run through [StringUtils.format], so most tests use plain
 * markers (`C`, `P`, `I`) that survive formatting untouched, which keeps the structural
 * assertions exact. The formatting-specific tests use real colour codes instead.
 */
internal class ProgressBarTests {
    private companion object {
        const val COMPLETE = "C"
        const val IN_PROGRESS = "P"
        const val INCOMPLETE = "I"
    }

    private lateinit var eco: Eco

    @BeforeEach
    fun setup() {
        mockkStatic(Eco::class)
        eco = mockk()
        every { Eco.get() } returns eco

        // Placeholders and MiniMessage are identity by default; individual tests override.
        every { eco.translatePlaceholders(any(), any()) } answers { firstArg() }
        every { eco.formatMiniMessage(any()) } answers { firstArg() }
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(Eco::class)
    }

    private fun bar(
        bars: Int,
        progress: Double,
        character: Char = '=',
        complete: String = COMPLETE,
        inProgress: String = IN_PROGRESS,
        incomplete: String = INCOMPLETE
    ) = StringUtils.createProgressBar(character, bars, progress, complete, inProgress, incomplete)

    // Structure

    @Test
    fun `full bar is entirely complete`() {
        assertEquals("C==========", bar(10, 1.0))
    }

    @Test
    fun `empty bar has no complete section`() {
        assertEquals("P=I=========", bar(10, 0.0))
    }

    @Test
    fun `half bar splits around the in-progress character`() {
        assertEquals("C=====P=I====", bar(10, 0.5))
    }

    @Test
    fun `almost full bar has no incomplete section`() {
        // floor(0.99 * 10) = 9 complete, 1 in-progress, 0 incomplete.
        assertEquals("C=========P=", bar(10, 0.99))
    }

    @Test
    fun `two bars is the minimum and puts the in-progress character first`() {
        assertEquals("P=I=", bar(2, 0.0))
        assertEquals("P=I=", bar(2, 0.4))
        assertEquals("C=P=", bar(2, 0.5))
        assertEquals("C=P=", bar(2, 0.99))
        assertEquals("C==", bar(2, 1.0))
    }

    @Test
    fun `progress of exactly one bar completes exactly one bar`() {
        assertEquals("C=P=I==", bar(4, 0.25))
    }

    @Test
    fun `progress just under a bar boundary does not complete that bar`() {
        assertEquals("P=I===", bar(4, 0.2499))
    }

    @Test
    fun `progress derived from a fraction of the bars completes that many bars`() {
        for (num in 1 until 10) {
            val incompleteBars = 10 - num - 1
            val incompleteSection = if (incompleteBars > 0) INCOMPLETE + "=".repeat(incompleteBars) else ""
            val expected = COMPLETE + "=".repeat(num) + IN_PROGRESS + "=" + incompleteSection

            assertEquals(expected, bar(10, num / 10.0), "num=$num")
        }
    }

    @Test
    fun `floating point progress can truncate down`() {
        // 15.0 / 22 * 22 is 14.999999999999998 as a double, so only 14 bars complete.
        assertEquals(
            COMPLETE + "=".repeat(14) + IN_PROGRESS + "=" + INCOMPLETE + "=".repeat(7),
            bar(22, 15.0 / 22)
        )
    }

    @Test
    fun `large bar counts are handled`() {
        val result = bar(1000, 0.5)

        assertEquals(COMPLETE + "=".repeat(500) + IN_PROGRESS + "=" + INCOMPLETE + "=".repeat(499), result)
    }

    // Invariants

    @Test
    fun `bar is always exactly the requested number of characters long`() {
        for (bars in 2..25) {
            for (step in 0..20) {
                val progress = step / 20.0
                val result = StringUtils.createProgressBar('=', bars, progress, "", "", "")

                assertEquals(bars, result.length, "bars=$bars, progress=$progress")
                assertTrue(result.all { it == '=' }, "bars=$bars, progress=$progress")
            }
        }
    }

    @Test
    fun `exactly one character is in progress unless the bar is full`() {
        for (bars in 2..25) {
            for (step in 0..20) {
                val progress = step / 20.0
                val result = bar(bars, progress)
                val expectedInProgress = if (progress == 1.0) 0 else 1

                assertEquals(
                    expectedInProgress,
                    result.count { it == 'P' },
                    "bars=$bars, progress=$progress"
                )
            }
        }
    }

    @Test
    fun `sections appear in order and only when non-empty`() {
        for (bars in 2..25) {
            for (step in 0..20) {
                val progress = step / 20.0
                val result = bar(bars, progress)
                val markers = result.filter { it in "CPI" }
                val completeBars = Math.floor(progress * bars).toInt()

                val expected = when {
                    progress == 1.0 -> "C"
                    completeBars == 0 -> "PI"
                    completeBars == bars - 1 -> "CP"
                    else -> "CPI"
                }

                assertEquals(expected, markers, "bars=$bars, progress=$progress")
            }
        }
    }

    // Characters

    @Test
    fun `any character can be used`() {
        assertEquals("C||P|I||", bar(5, 0.5, character = '|'))
        assertEquals("C██P█I██", bar(5, 0.5, character = '█'))
        assertEquals("C  P I  ", bar(5, 0.5, character = ' '))
        assertEquals("C%%P%I%%", bar(5, 0.5, character = '%'))
    }

    // Formats

    @Test
    fun `empty formats produce a bare bar`() {
        assertEquals("=====", StringUtils.createProgressBar('=', 5, 1.0, "", "", ""))
        assertEquals("=====", StringUtils.createProgressBar('=', 5, 0.5, "", "", ""))
    }

    @Test
    fun `legacy ampersand codes are formatted`() {
        assertEquals("§a==§e=§7==", StringUtils.createProgressBar('=', 5, 0.5, "&a", "&e", "&7"))
    }

    @Test
    fun `section sign codes pass through unchanged`() {
        assertEquals("§a==§e=§7==", StringUtils.createProgressBar('=', 5, 0.5, "§a", "§e", "§7"))
    }

    @Test
    fun `decoration codes are formatted`() {
        assertEquals("§a§l==§e=§7==", StringUtils.createProgressBar('=', 5, 0.5, "&a&l", "&e", "&7"))
    }

    @Test
    fun `ampersand hex codes are formatted`() {
        assertEquals(
            "§x§F§F§0§0§0§0==§x§0§0§F§F§0§0=§x§8§0§8§0§8§0==",
            StringUtils.createProgressBar('=', 5, 0.5, "&#FF0000", "&#00FF00", "&#808080")
        )
    }

    @Test
    fun `braced hex codes are formatted`() {
        assertEquals(
            "§x§F§F§0§0§0§0==§x§0§0§F§F§0§0=§x§8§0§8§0§8§0==",
            StringUtils.createProgressBar('=', 5, 0.5, "{#FF0000}", "{#00FF00}", "{#808080}")
        )
    }

    @Test
    fun `angled hex codes are formatted`() {
        assertEquals(
            "§x§F§F§0§0§0§0==§x§0§0§F§F§0§0=§x§8§0§8§0§8§0==",
            StringUtils.createProgressBar('=', 5, 0.5, "<#FF0000>", "<#00FF00>", "<#808080>")
        )
    }

    @Test
    fun `mini message tags are formatted`() {
        every { eco.formatMiniMessage("<green>") } returns "§a"
        every { eco.formatMiniMessage("<yellow>") } returns "§e"
        every { eco.formatMiniMessage("<gray>") } returns "§7"

        assertEquals("§a==§e=§7==", StringUtils.createProgressBar('=', 5, 0.5, "<green>", "<yellow>", "<gray>"))
    }

    @Test
    fun `placeholders in formats are translated`() {
        every { eco.translatePlaceholders("%complete_color%", any()) } returns "&a"
        every { eco.translatePlaceholders("%in_progress_color%", any()) } returns "&e"
        every { eco.translatePlaceholders("%incomplete_color%", any()) } returns "&7"

        assertEquals(
            "§a==§e=§7==",
            StringUtils.createProgressBar('=', 5, 0.5, "%complete_color%", "%in_progress_color%", "%incomplete_color%")
        )
    }

    @Test
    fun `unused formats do not appear in the output`() {
        // Full bar: neither the in-progress nor the incomplete format is used.
        assertEquals("§a=====", StringUtils.createProgressBar('=', 5, 1.0, "&a", "&e", "&7"))

        // Empty bar: the complete format is not used.
        assertEquals("§e=§7====", StringUtils.createProgressBar('=', 5, 0.0, "&a", "&e", "&7"))

        // Last bar in progress: the incomplete format is not used.
        assertEquals("§a====§e=", StringUtils.createProgressBar('=', 5, 0.99, "&a", "&e", "&7"))
    }

    // Preconditions

    @Test
    fun `negative progress is rejected`() {
        val exception = assertThrows(IllegalArgumentException::class.java) { bar(10, -0.01) }

        assertEquals("Progress must be between 0 and 1!", exception.message)
    }

    @Test
    fun `progress above one is rejected`() {
        val exception = assertThrows(IllegalArgumentException::class.java) { bar(10, 1.01) }

        assertEquals("Progress must be between 0 and 1!", exception.message)
    }

    @Test
    fun `non-finite progress is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { bar(10, Double.NaN) }
        assertThrows(IllegalArgumentException::class.java) { bar(10, Double.POSITIVE_INFINITY) }
        assertThrows(IllegalArgumentException::class.java) { bar(10, Double.NEGATIVE_INFINITY) }
    }

    @Test
    fun `fewer than two bars is rejected`() {
        for (bars in intArrayOf(1, 0, -1, Int.MIN_VALUE)) {
            val exception = assertThrows(IllegalArgumentException::class.java) { bar(bars, 0.5) }

            assertEquals("Must have at least 2 bars!", exception.message)
        }
    }

    @Test
    fun `progress is validated before the bar count`() {
        val exception = assertThrows(IllegalArgumentException::class.java) { bar(1, 2.0) }

        assertEquals("Progress must be between 0 and 1!", exception.message)
    }
}
