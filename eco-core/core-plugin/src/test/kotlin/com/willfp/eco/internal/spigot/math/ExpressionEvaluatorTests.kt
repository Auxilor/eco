package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.placeholder.PlaceholderParser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTimeoutPreemptively
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.math.PI

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

    @Test
    fun `formula value is evaluated`() {
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            firstArg<Collection<String>>().map { placeholder ->
                when (placeholder) {
                    "%amount%" -> "120-(120*(%eco_reduction%*0.01))"
                    "%eco_reduction%" -> "20"
                    else -> "0"
                }
            }
        }
        assertEquals(96.0, evaluator.evaluate("%amount%", context))
        assertEquals(-96.0, evaluator.evaluate("- %amount%", context))
        assertEquals(97.0, evaluator.evaluate("%amount% + 1", context))
    }

    @Test
    fun `self reference stops`() {
        stubPlaceholder("%eco_loop%", "%eco_loop% + 1")
        assertEquals(8.0, evaluator.evaluate("%eco_loop%", context))
    }

    @Test
    fun `constant formula evaluates`() {
        stubPlaceholder("%eco_angle%", "pi + pi")
        assertEquals(2 * PI, evaluator.evaluate("%eco_angle%", context))
    }

    @Test
    fun `single word value stays zero`() {
        stubPlaceholder("%eco_flag%", "true")
        assertEquals(5.0, evaluator.evaluate("%eco_flag% + 5", context))
    }

    @Test
    fun `branching references stop`() {
        val names = (0 until 10).map { "%eco_branch_$it%" }
        val formula = names.joinToString(" + ")
        var resolutions = 0
        every { parser.parseIndividualPlaceholders(any(), context) } answers {
            resolutions++
            firstArg<Collection<String>>().map { formula }
        }
        assertTimeoutPreemptively(Duration.ofSeconds(5)) {
            evaluator.evaluate(formula, context)
        }
        assertTrue(resolutions <= 65, "resolved $resolutions times")
    }

}
