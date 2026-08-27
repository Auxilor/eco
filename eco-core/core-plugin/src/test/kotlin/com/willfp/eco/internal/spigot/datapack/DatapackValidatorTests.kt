package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.internal.spigot.proxies.DatapackCodecProxy
import com.willfp.eco.internal.spigot.proxies.PendingContent
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DatapackValidatorTests {
    /**
     * Stands in for the server's own codec. Rejects the exact content that killed a live 1.21.11
     * test server: a damage_type with an enum value that does not exist.
     *
     * It also models the behaviour that makes [PendingContent] necessary: some reference fields
     * resolve eagerly during decode, against the live registries plus whatever the same publish is
     * writing.
     */
    private object FakeCodec : DatapackCodecProxy {
        /** What the running server already has, standing in for the live registries. */
        private val live = setOf("minecraft:flower_plain")

        private val reference = Regex("\"(feature|biomes)\"\\s*:\\s*\"([^\"]+)\"")

        private val targets = mapOf(
            "feature" to "worldgen/configured_feature",
            "biomes" to "worldgen/biome"
        )

        override fun validate(registryDirPath: String, json: String, pending: PendingContent): String? {
            if (json.contains("NOT_A_VALID_SCALING")) {
                return "Unknown element name: NOT_A_VALID_SCALING; No key message_id in MapLike[...]"
            }

            for (match in reference.findAll(json)) {
                val (field, raw) = match.destructured
                val target = targets[field] ?: continue
                val isTag = raw.startsWith("#")
                val id = raw.removePrefix("#")

                val known = if (isTag) pending.tagsIn(target) else live + pending.elementsIn(target)

                if (id !in known) {
                    return if (isTag) {
                        "Missing tag: '$id' in 'minecraft:$target'"
                    } else {
                        "Failed to get element ResourceKey[minecraft:$target / $id]"
                    }
                }
            }

            return null
        }

        override fun validatableRegistries() = setOf(
            "damage_type",
            "worldgen/biome",
            "worldgen/placed_feature",
            "worldgen/structure"
        )
    }

    private fun entry(registry: String, key: String, content: String, namespace: String = "test") =
        DatapackEntry(registry, NamespacedKey(namespace, key), content.toByteArray(), true)

    /** Mirrors how `EcoDatapackHandle` splits a draft into elements and tags. */
    private fun pendingFor(entries: List<DatapackEntry>): PendingContent {
        val elements = mutableMapOf<String, MutableSet<String>>()
        val tags = mutableMapOf<String, MutableSet<String>>()

        for (entry in entries) {
            val registry = entry.registry.trim('/').lowercase()
            val id = "${entry.id.namespace}:${entry.id.key.removeSuffix(".json")}"

            if (registry.startsWith("tags/")) {
                tags.getOrPut(registry.removePrefix("tags/")) { mutableSetOf() }.add(id)
            } else {
                elements.getOrPut(registry) { mutableSetOf() }.add(id)
            }
        }

        return PendingContent(elements, tags)
    }

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
            override fun validate(registryDirPath: String, json: String, pending: PendingContent): String? =
                throw IllegalStateException("boom")

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

    @Test
    fun `a reference to an entry the same publish writes is accepted`() {
        val validator = DatapackValidator(FakeCodec)

        val entries = listOf(
            entry("worldgen/configured_feature", "my_cf", """{"type":"minecraft:flower"}"""),
            entry("worldgen/placed_feature", "my_pf", """{"feature":"test:my_cf"}""")
        )

        Assertions.assertNull(validator.validate(entries[1], pendingFor(entries)))
    }

    @Test
    fun `a reference to an entry nobody writes is still rejected`() {
        val validator = DatapackValidator(FakeCodec)

        val entries = listOf(entry("worldgen/placed_feature", "my_pf", """{"feature":"test:absent"}"""))
        val error = validator.validate(entries[0], pendingFor(entries))

        Assertions.assertNotNull(error)
        Assertions.assertTrue(error!!.contains("Failed to get element"), error)
    }

    @Test
    fun `a tag reference to a tag the same publish writes is accepted`() {
        val validator = DatapackValidator(FakeCodec)

        val entries = listOf(
            entry("tags/worldgen/biome", "has_probe", """{"values":["test:my_biome"]}"""),
            entry("worldgen/structure", "my_structure", """{"biomes":"#test:has_probe"}""")
        )

        Assertions.assertNull(validator.validate(entries[1], pendingFor(entries)))
    }

    @Test
    fun `a tag reference to a tag nobody writes is still rejected`() {
        val validator = DatapackValidator(FakeCodec)

        val entries = listOf(entry("worldgen/structure", "my_structure", """{"biomes":"#test:absent"}"""))
        val error = validator.validate(entries[0], pendingFor(entries))

        Assertions.assertNotNull(error)
        Assertions.assertTrue(error!!.contains("Missing tag"), error)
    }

    @Test
    fun `a reference to live content needs no pending set`() {
        val validator = DatapackValidator(FakeCodec)

        Assertions.assertNull(
            validator.validate(
                entry("worldgen/placed_feature", "my_pf", """{"feature":"minecraft:flower_plain"}""")
            )
        )
    }
}
