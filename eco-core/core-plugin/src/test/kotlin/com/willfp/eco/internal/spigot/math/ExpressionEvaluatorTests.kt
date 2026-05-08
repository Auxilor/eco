package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.placeholder.PlaceholderParser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ExpressionEvaluatorTests {

    private lateinit var parser: PlaceholderParser
    private lateinit var evaluator: ExpressionEvaluator
    private val context = PlaceholderContext.EMPTY

    @BeforeEach
    fun setup() {
        parser = mockk()
        evaluator = ExpressionEvaluator(parser, 0L)
    }

    private fun stubPlaceholder(name: String, value: String) {
        every { parser.parseIndividualPlaceholders(match { name in it }, context) } answers {
            firstArg<Collection<String>>().map { if (it == name) value else "0" }
        }
    }

    @Test
    fun `plain number bypasses placeholder lookup`() {
        assertEquals(42.0, evaluator.evaluate("42", context))
    }

    @Test
    fun `single placeholder resolves correctly`() {
        stubPlaceholder("%eco_level%", "5")
        assertEquals(10.0, evaluator.evaluate("%eco_level% * 2", context))
    }

    @Test
    fun `multiple placeholders resolve correctly`() {
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map {
                when (it) {
                    "%eco_level%" -> "10"
                    "%eco_exp%" -> "50"
                    else -> "0"
                }
            }
        }
        assertEquals(500.0, evaluator.evaluate("%eco_level% * %eco_exp%", context))
    }

    @Test
    fun `non-numeric placeholder value is treated as zero`() {
        stubPlaceholder("%eco_name%", "Steve")
        assertEquals(5.0, evaluator.evaluate("%eco_name% + 5", context))
    }

    @Test
    fun `division by zero returns null`() {
        stubPlaceholder("%eco_x%", "0")
        assertNull(evaluator.evaluate("1 / %eco_x%", context))
    }

    @Test
    fun `expression with no placeholders evaluates directly`() {
        assertEquals(9.0, evaluator.evaluate("3 * 3", context))
    }

    @Test
    fun `placeholder value used in complex expression`() {
        stubPlaceholder("%eco_health%", "20")
        assertEquals(410.0, evaluator.evaluate("%eco_health% ^ 2 + 10", context))
    }

    @Test
    fun `invalid expression returns null`() {
        assertNull(evaluator.evaluate("* 5", context))
    }

}
