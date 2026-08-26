package com.willfp.eco.internal.spigot.datapack

import com.google.gson.JsonSyntaxException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class JsonCanonicaliserTests {
    @Test
    fun `key order does not change the output`() {
        Assertions.assertEquals(
            JsonCanonicaliser.canonicalise("""{"a": 1, "b": 2}"""),
            JsonCanonicaliser.canonicalise("""{"b": 2, "a": 1}""")
        )
    }

    @Test
    fun `whitespace does not change the output`() {
        Assertions.assertEquals(
            JsonCanonicaliser.canonicalise("""{"a":1}"""),
            JsonCanonicaliser.canonicalise("{\n   \"a\" :    1\n}\n")
        )
    }

    @Test
    fun `nested objects are sorted too`() {
        Assertions.assertEquals(
            JsonCanonicaliser.canonicalise("""{"o": {"x": 1, "y": 2}}"""),
            JsonCanonicaliser.canonicalise("""{"o": {"y": 2, "x": 1}}""")
        )
    }

    @Test
    fun `array order is preserved`() {
        Assertions.assertNotEquals(
            JsonCanonicaliser.canonicalise("""{"a": [1, 2]}"""),
            JsonCanonicaliser.canonicalise("""{"a": [2, 1]}""")
        )
    }

    @Test
    fun `canonicalising is idempotent`() {
        val once = JsonCanonicaliser.canonicalise("""{"b": [3, {"z": 1, "a": 2}], "a": null}""")
        Assertions.assertEquals(once, JsonCanonicaliser.canonicalise(once))
    }

    @Test
    fun `malformed json is rejected`() {
        for (bad in listOf("{", """{"a": }""", """{"a": 1,}""", "")) {
            Assertions.assertThrows(JsonSyntaxException::class.java, { JsonCanonicaliser.parseStrict(bad) }, bad)
        }
    }

    @Test
    fun `lenient json is rejected`() {
        // Gson accepts these by default. Minecraft's loader does not, so neither do we.
        for (bad in listOf("{a: 1}", "{'a': 1}", """{"a": 1} {"b": 2}""")) {
            Assertions.assertThrows(JsonSyntaxException::class.java, { JsonCanonicaliser.parseStrict(bad) }, bad)
        }
    }

    @Test
    fun `well-formed json is accepted`() {
        Assertions.assertTrue(JsonCanonicaliser.parseStrict("""{"a": 1}""").isJsonObject)
    }
}
