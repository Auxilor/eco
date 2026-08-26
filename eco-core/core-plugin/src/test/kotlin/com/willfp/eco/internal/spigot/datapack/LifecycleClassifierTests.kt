package com.willfp.eco.internal.spigot.datapack

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class LifecycleClassifierTests {
    @Test
    fun `the reloadable set is exactly the seven known registries`() {
        Assertions.assertEquals(
            setOf("recipe", "advancement", "function", "tags", "loot_table", "predicate", "item_modifier"),
            LifecycleClassifier.RELOADABLE
        )
    }

    @Test
    fun `reloadable registries are reloadable`() {
        for (registry in LifecycleClassifier.RELOADABLE) {
            Assertions.assertTrue(LifecycleClassifier.isReloadable(registry), registry)
        }
    }

    @Test
    fun `tag subdirectories are reloadable`() {
        Assertions.assertTrue(LifecycleClassifier.isReloadable("tags/block"))
        Assertions.assertTrue(LifecycleClassifier.isReloadable("tags/worldgen/biome"))
    }

    @Test
    fun `bootstrap registries are not reloadable`() {
        for (registry in listOf("worldgen/biome", "dimension", "dimension_type", "enchantment", "damage_type")) {
            Assertions.assertFalse(LifecycleClassifier.isReloadable(registry), registry)
        }
    }

    @Test
    fun `unknown registries default to bootstrap`() {
        // The default must fail towards a needless restart prompt, never towards silently
        // reporting that content is live when it is not.
        Assertions.assertFalse(LifecycleClassifier.isReloadable("worldgen/feature_type"))
        Assertions.assertFalse(LifecycleClassifier.isReloadable("something_mojang_adds_in_26_3"))
    }

    @Test
    fun `leading and trailing slashes and casing are ignored`() {
        Assertions.assertTrue(LifecycleClassifier.isReloadable("/recipe/"))
        Assertions.assertTrue(LifecycleClassifier.isReloadable("Recipe"))
    }

    @Test
    fun `a restart is required if any registry is bootstrap-only`() {
        Assertions.assertFalse(LifecycleClassifier.requiresRestart(listOf("recipe", "advancement")))
        Assertions.assertTrue(LifecycleClassifier.requiresRestart(listOf("recipe", "worldgen/biome")))
        Assertions.assertFalse(LifecycleClassifier.requiresRestart(emptyList()))
    }

    @Test
    fun `registries that merely start with a reloadable name are not reloadable`() {
        Assertions.assertFalse(LifecycleClassifier.isReloadable("recipe_type"))
        Assertions.assertFalse(LifecycleClassifier.isReloadable("functions"))
    }
}
