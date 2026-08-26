package com.willfp.eco.internal.spigot.datapack

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class PackMcmetaTests {
    @Test
    fun `the pre-1_21_9 version json shape is read`() {
        Assertions.assertEquals(
            PackFormat(81, 0),
            PackMcmeta.parseVersionJson("""{"id":"1.21.8","pack_version":{"resource":64,"data":81}}""")
        )
    }

    @Test
    fun `the post-1_21_9 version json shape is read`() {
        // The shape changed at 1.21.9, from {"data": 81} to {"data_major": 88, "data_minor": 0}.
        val cases = mapOf(
            88 to 0,
            94 to 1,
            101 to 1,
            107 to 1
        )

        for ((major, minor) in cases) {
            Assertions.assertEquals(
                PackFormat(major, minor),
                PackMcmeta.parseVersionJson(
                    """{"pack_version":{"resource_major":1,"data_major":$major,"data_minor":$minor}}"""
                )
            )
        }
    }

    @Test
    fun `a version json with no pack version yields null`() {
        Assertions.assertNull(PackMcmeta.parseVersionJson("""{"id":"1.21.8"}"""))
        Assertions.assertNull(PackMcmeta.parseVersionJson("""{"pack_version":{"resource":64}}"""))
        Assertions.assertNull(PackMcmeta.parseVersionJson("not json"))
    }

    @Test
    fun `the supported range spans from the minimum to the running server`() {
        for (format in listOf(81, 88, 94, 101, 107)) {
            val mcmeta = PackMcmeta.generate("TestPlugin (eco)", PackFormat(format))
            val pack = JsonCanonicaliser.parseStrict(mcmeta).asJsonObject.getAsJsonObject("pack")

            Assertions.assertEquals(PackMcmeta.MIN_FORMAT, pack.get("pack_format").asInt)
            Assertions.assertEquals(PackMcmeta.MIN_FORMAT, pack.get("min_format").asInt)
            Assertions.assertEquals(format, pack.get("max_format").asInt)

            val supported = pack.getAsJsonObject("supported_formats")
            Assertions.assertEquals(PackMcmeta.MIN_FORMAT, supported.get("min_inclusive").asInt)
            Assertions.assertEquals(format, supported.get("max_inclusive").asInt)
        }
    }

    @Test
    fun `a server older than the minimum does not produce an inverted range`() {
        val mcmeta = PackMcmeta.generate("TestPlugin (eco)", PackFormat(10))
        val pack = JsonCanonicaliser.parseStrict(mcmeta).asJsonObject.getAsJsonObject("pack")

        Assertions.assertEquals(PackMcmeta.MIN_FORMAT, pack.get("max_format").asInt)
    }

    @Test
    fun `generation is deterministic`() {
        Assertions.assertEquals(
            PackMcmeta.generate("A (eco)", PackFormat(94, 1)),
            PackMcmeta.generate("A (eco)", PackFormat(94, 1))
        )
    }
}
