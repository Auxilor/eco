package com.willfp.eco.internal.spigot.datapack

import com.willfp.eco.core.datapack.DatapackDraft
import com.willfp.eco.core.datapack.InstallResult
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Logger

internal class EcoDatapackHandleTests {
    @TempDir
    lateinit var root: File

    private lateinit var ledger: CommitLedger
    private lateinit var coordinator: RestartCoordinator
    private lateinit var handle: EcoDatapackHandle
    private lateinit var published: MutableList<List<DatapackEntry>>

    private fun packDir() = File(File(root, "datapacks"), "eco_testplugin")

    private fun silentLogger() = Logger.getAnonymousLogger().apply {
        useParentHandlers = false
        level = Level.OFF
    }

    private fun handleFor(coordinator: RestartCoordinator, onPublished: (List<DatapackEntry>) -> Unit) =
        EcoDatapackHandle(
            pluginId = "testplugin",
            displayName = "TestPlugin",
            packDir = packDir(),
            validator = DatapackValidator(null),
            ledger = ledger,
            restartCoordinator = coordinator,
            logger = silentLogger(),
            packFormat = { PackFormat(94, 1) },
            onPublished = onPublished
        )

    @BeforeEach
    fun setUp() {
        ledger = CommitLedger(MemoryLedgerStorage())
        coordinator = RestartCoordinator(silentLogger())
        published = mutableListOf()
        handle = handleFor(coordinator) { published.add(it) }
    }

    private fun apply(builder: (DatapackDraft) -> Unit) = handle.apply(Consumer(builder))

    @Test
    fun `reloadable content is ready without a restart`() {
        val result = apply {
            it.put("recipe", NamespacedKey("test", "thing"), """{"type":"minecraft:crafting_shapeless"}""")
        }

        Assertions.assertEquals(InstallResult.Status.READY, result.status)
        Assertions.assertFalse(result.restartRequired())
        Assertions.assertFalse(handle.restartPending())
        Assertions.assertTrue(File(packDir(), "data/test/recipe/thing.json").isFile)
        Assertions.assertTrue(File(packDir(), "pack.mcmeta").isFile)
    }

    @Test
    fun `bootstrap content requires a restart`() {
        val result = apply { it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 1}""") }

        Assertions.assertEquals(InstallResult.Status.RESTART_REQUIRED, result.status)
        Assertions.assertTrue(result.restartRequired())
        Assertions.assertTrue(handle.restartPending())
        Assertions.assertTrue(coordinator.restartPending)
    }

    @Test
    fun `identical content on a second run is unchanged`() {
        apply { it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 1, "b": 2}""") }

        // Same content, different whitespace and key order: the consumer should not have to care.
        val second = apply {
            it.put("worldgen/biome", NamespacedKey("test", "swamp"), "{\n  \"b\":2,\n \"a\": 1}")
        }

        Assertions.assertEquals(InstallResult.Status.UNCHANGED, second.status)
        Assertions.assertFalse(second.changed())
    }

    @Test
    fun `an unchanged republish does not re-arm the restart prompt`() {
        apply { it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 1}""") }

        // Simulates the next boot: the pack is already on disk, so it is already live and the
        // operator must not be told to restart again.
        val nextBoot = RestartCoordinator(silentLogger())
        val result = handleFor(nextBoot) { }
            .apply(Consumer { it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 1}""") })

        Assertions.assertEquals(InstallResult.Status.UNCHANGED, result.status)
        Assertions.assertFalse(nextBoot.restartPending)
    }

    @Test
    fun `invalid content is refused and nothing is written`() {
        apply { it.put("worldgen/biome", NamespacedKey("test", "good"), """{"a": 1}""") }

        val result = apply {
            it.put("worldgen/biome", NamespacedKey("test", "good"), """{"a": 2}""")
            it.put("worldgen/biome", NamespacedKey("test", "bad"), "{not json")
        }

        Assertions.assertEquals(InstallResult.Status.FAILED, result.status)
        Assertions.assertFalse(result.succeeded())
        Assertions.assertTrue(result.messages().isNotEmpty())

        val live = JsonCanonicaliser.parseStrict(
            File(packDir(), "data/test/worldgen/biome/good.json").readText()
        ).asJsonObject

        Assertions.assertEquals(1, live.get("a").asInt, "The live pack must be untouched")
        Assertions.assertFalse(File(packDir(), "data/test/worldgen/biome/bad.json").exists())
    }

    @Test
    fun `duplicate entries are refused`() {
        val result = apply {
            it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 1}""")
            it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 2}""")
        }

        Assertions.assertEquals(InstallResult.Status.FAILED, result.status)
        Assertions.assertTrue(result.messages().any { it.contains("duplicate") })
    }

    @Test
    fun `the pack is rebuilt in full, so dropped entries disappear`() {
        apply {
            it.put("recipe", NamespacedKey("test", "a"), "{}")
            it.put("recipe", NamespacedKey("test", "b"), "{}")
        }

        apply { it.put("recipe", NamespacedKey("test", "a"), "{}") }

        Assertions.assertFalse(File(packDir(), "data/test/recipe/b.json").exists())
    }

    @Test
    fun `removal is refused once an entry is committed`() {
        apply { it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 1}""") }
        ledger.commit("testplugin", published.last())

        val result = handle.remove()

        Assertions.assertEquals(InstallResult.Status.FAILED, result.status)
        Assertions.assertTrue(packDir().isDirectory)
    }

    @Test
    fun `reloadable content can always be removed`() {
        apply { it.put("recipe", NamespacedKey("test", "a"), "{}") }
        ledger.commit("testplugin", published.last())

        Assertions.assertTrue(handle.remove().succeeded())
        Assertions.assertFalse(packDir().exists())
    }

    @Test
    fun `force removal overrides the ledger`() {
        apply { it.put("worldgen/biome", NamespacedKey("test", "swamp"), """{"a": 1}""") }
        ledger.commit("testplugin", published.last())

        Assertions.assertTrue(handle.forceRemove().succeeded())
        Assertions.assertFalse(packDir().exists())
        Assertions.assertTrue(ledger.committed("testplugin").isEmpty())
    }

    @Test
    fun `the pack description names the plugin`() {
        apply { it.put("recipe", NamespacedKey("test", "a"), "{}") }

        val mcmeta = JsonCanonicaliser.parseStrict(File(packDir(), "pack.mcmeta").readText()).asJsonObject

        Assertions.assertEquals(
            "TestPlugin (eco)",
            mcmeta.getAsJsonObject("pack").get("description").asString
        )
    }
}
