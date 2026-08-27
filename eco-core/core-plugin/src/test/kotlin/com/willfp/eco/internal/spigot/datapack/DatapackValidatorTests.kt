package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.internal.spigot.proxies.DatapackCodecProxy
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DatapackValidatorTests {
    /**
     * Stands in for the server's own codec. Rejects the exact content that killed a live 1.21.11
     * test server: a damage_type with an enum value that does not exist.
     */
    private object FakeCodec : DatapackCodecProxy {
        override fun validate(registryDirPath: String, json: String): String? =
            if (json.contains("NOT_A_VALID_SCALING")) {
                "Unknown element name: NOT_A_VALID_SCALING; No key message_id in MapLike[...]"
            } else {
                null
            }

        override fun validatableRegistries() = setOf("damage_type", "worldgen/biome")
    }

    private fun entry(registry: String, key: String, content: String, namespace: String = "test") =
        DatapackEntry(registry, NamespacedKey(namespace, key), content.toByteArray(), true)

    @Test
    fun `valid content passes`() {
        val validator = DatapackValidator(FakeCodec)

        Assertions.assertNull(
            validator.validate(entry("damage_type", "custom", """{"scaling":"never","exhaustion":0.0}"""))
        )
    }

    @Test
    fun `the entry that killed a live server is rejected`() {
        val validator = DatapackValidator(FakeCodec)

        val error = validator.validate(
            entry("damage_type", "broken", """{"scaling":"NOT_A_VALID_SCALING","exhaustion":0.0}""", "badpack")
        )

        Assertions.assertNotNull(error)
        Assertions.assertTrue(error!!.contains("NOT_A_VALID_SCALING"), error)
    }

    @Test
    fun `malformed json is rejected before the codec sees it`() {
        val validator = DatapackValidator(FakeCodec)

        Assertions.assertNotNull(validator.validate(entry("damage_type", "broken", """{"scaling":}""")))
        Assertions.assertNotNull(validator.validate(entry("damage_type", "broken", "{a: 1}")))
    }

    @Test
    fun `a non-object top level is rejected`() {
        val validator = DatapackValidator(FakeCodec)

        Assertions.assertNotNull(validator.validate(entry("damage_type", "broken", "[1, 2]")))
    }

    @Test
    fun `an invalid registry directory is rejected`() {
        val validator = DatapackValidator(FakeCodec)

        Assertions.assertNotNull(validator.validate(entry("Worldgen Biome", "thing", "{}")))
        Assertions.assertNotNull(validator.validate(entry("", "thing", "{}")))
    }

    @Test
    fun `path traversal in the id is rejected`() {
        val validator = DatapackValidator(FakeCodec)

        Assertions.assertNotNull(validator.validate(entry("worldgen/biome", "../escape", "{}")))
    }

    @Test
    fun `registries with no reachable codec get syntax checks only`() {
        val validator = DatapackValidator(FakeCodec)

        // recipe is not in validatableRegistries, so the codec tier is skipped.
        Assertions.assertNull(validator.validate(entry("recipe", "thing", """{"scaling":"NOT_A_VALID_SCALING"}""")))
        Assertions.assertNotNull(validator.validate(entry("recipe", "thing", "{")))
    }

    @Test
    fun `a missing proxy degrades to syntax checks`() {
        val validator = DatapackValidator(null)

        Assertions.assertNull(
            validator.validate(entry("damage_type", "broken", """{"scaling":"NOT_A_VALID_SCALING"}"""))
        )
        Assertions.assertNotNull(validator.validate(entry("damage_type", "broken", "{")))
    }

    @Test
    fun `a throwing proxy fails closed`() {
        val throwing = object : DatapackCodecProxy {
            override fun validate(registryDirPath: String, json: String): String? = throw IllegalStateException("boom")
            override fun validatableRegistries() = setOf("damage_type")
        }

        Assertions.assertNotNull(DatapackValidator(throwing).validate(entry("damage_type", "thing", "{}")))
    }

    @Test
    fun `binary entries skip json checks`() {
        val validator = DatapackValidator(FakeCodec)
        val binary = DatapackEntry("structure", NamespacedKey("test", "a"), byteArrayOf(0x0A, 0x00), false)

        Assertions.assertNull(validator.validate(binary))
    }
}
