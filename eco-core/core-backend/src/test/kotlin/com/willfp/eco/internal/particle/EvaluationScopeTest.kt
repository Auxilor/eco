package com.willfp.eco.internal.particle

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.E
import kotlin.math.PI

class EvaluationScopeTest {

    private val empty = EvaluationScope.empty(PlaceholderContext.EMPTY)

    @Test
    fun `pi resolves to Math PI`() {
        assertEquals(PI, empty.lookup("pi"))
    }

    @Test
    fun `e resolves to Math E`() {
        assertEquals(E, empty.lookup("e"))
    }

    @Test
    fun `unknown name resolves to zero`() {
        assertEquals(0.0, empty.lookup("unknownVariable"))
    }

    @Test
    fun `reserved variable is found`() {
        val scope = empty.withReserved(mapOf("t" to 3.0))
        assertEquals(3.0, scope.lookup("t"))
    }

    @Test
    fun `vars variable is found`() {
        val scope = empty.withVars(mapOf("myVar" to 42.0))
        assertEquals(42.0, scope.lookup("myVar"))
    }

    @Test
    fun `reserved shadows var with same name`() {
        val scope = empty
            .withVars(mapOf("t" to 1.0))
            .withReserved(mapOf("t" to 99.0))
        assertEquals(99.0, scope.lookup("t"))
    }

    @Test
    fun `inner reserved shadows outer reserved`() {
        val scope = empty
            .withReserved(mapOf("t" to 1.0))
            .withReserved(mapOf("t" to 2.0))
        assertEquals(2.0, scope.lookup("t"))
    }

    @Test
    fun `inner vars shadows outer vars`() {
        val scope = empty
            .withVars(mapOf("x" to 10.0))
            .withVars(mapOf("x" to 20.0))
        assertEquals(20.0, scope.lookup("x"))
    }

    @Test
    fun `withReserved does not mutate original scope`() {
        val original = empty.withReserved(mapOf("t" to 5.0))
        original.withReserved(mapOf("t" to 99.0))
        assertEquals(5.0, original.lookup("t"))
    }

    @Test
    fun `withVars does not mutate original scope`() {
        val original = empty.withVars(mapOf("x" to 5.0))
        original.withVars(mapOf("x" to 99.0))
        assertEquals(5.0, original.lookup("x"))
    }
}
