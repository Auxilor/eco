package com.willfp.eco.internal.config

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.util.StringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class EmptyListStringTests {
    private val config = EcoConfigSection(
        ConfigType.YAML,
        mapOf(
            "empty" to emptyList<String>(),
            "list" to listOf("a", "b"),
            "text" to "value"
        )
    )

    private fun read(path: String) = config.getStringOrNull(path, false, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)

    @Test
    fun `an empty list reads as an empty string`() {
        assertEquals("", read("empty"))
    }

    @Test
    fun `a non-empty list and a string read as before`() {
        assertEquals("[a, b]", read("list"))
        assertEquals("value", read("text"))
    }

    @Test
    fun `a missing key reads as null`() {
        assertNull(read("missing"))
    }
}
