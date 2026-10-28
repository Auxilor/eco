package com.willfp.eco.internal.placeholder

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.NumberUtils
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Each math expression in a string is evaluated on its own, rather than from the first opening
 * braces to the last closing ones.
 */
class PlaceholderMathTests {
    companion object {
        private val results = mapOf("1+1" to 2.0, "2+2" to 4.0, "3+3" to 6.0)

        @JvmStatic
        @BeforeAll
        fun stubMath() {
            mockkStatic(NumberUtils::class)
            every { NumberUtils.evaluateExpression(any(), any<PlaceholderContext>()) } answers {
                results.getValue(firstArg())
            }
        }

        @JvmStatic
        @AfterAll
        fun unstubMath() {
            unmockkStatic(NumberUtils::class)
        }
    }

    private val parser = PlaceholderParser()

    @Test
    fun `two expressions on one line`() {
        assertEquals("2.0 / 4.0", parser.translatePlacholders("{{1+1}} / {{2+2}}", PlaceholderContext.EMPTY))
    }

    @Test
    fun `pretty and plain together`() {
        assertEquals("4 and 6.0", parser.translatePlacholders("{^{2+2}} and {{3+3}}", PlaceholderContext.EMPTY))
    }

    @Test
    fun `repeats evaluate separately`() {
        every { NumberUtils.evaluateExpression("rand", any<PlaceholderContext>()) } returnsMany listOf(1.0, 2.0)

        assertEquals("1.0 / 2.0", parser.translatePlacholders("{{rand}} / {{rand}}", PlaceholderContext.EMPTY))
    }

    @Test
    fun `one expression is unchanged`() {
        assertEquals("Damage: 2.0", parser.translatePlacholders("Damage: {{1+1}}", PlaceholderContext.EMPTY))
    }
}
