package com.willfp.eco.internal.spigot.anvil

import org.bukkit.Material
import org.bukkit.Tag

/** Vanilla-style "unit repair" material table (e.g. iron ingot repairs iron tools/armor). */
object AnvilRepair {
    private val repair: MutableMap<Collection<Material>, Collection<Material>> = buildBaseMap()

    init {
        if (hasSpears() && !repair[Tag.PLANKS.values].orEmpty().any { it.name == "WOODEN_SPEAR" }) {
            addSpearAndCopperMaterials()
        }
    }

    /** Whether [other] (the right item's material) can unit-repair [this] (left item). */
    fun Material.canUnitRepair(other: Material): Boolean {
        for ((units, repairable) in repair) {
            if (this in units) {
                return other in repairable
            }
        }
        return false
    }

    /** Builds the base repair-unit map for materials present on all supported versions. */
    private fun buildBaseMap(): MutableMap<Collection<Material>, Collection<Material>> =
        mutableMapOf(
            Tag.PLANKS.values to listOf(
                Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_AXE,
                Material.WOODEN_SHOVEL, Material.WOODEN_HOE, Material.SHIELD
            ),
            listOf(Material.LEATHER) to listOf(
                Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS
            ),
            listOf(Material.COBBLESTONE, Material.COBBLED_DEEPSLATE, Material.BLACKSTONE) to listOf(
                Material.STONE_SWORD, Material.STONE_PICKAXE, Material.STONE_AXE,
                Material.STONE_SHOVEL, Material.STONE_HOE
            ),
            listOf(Material.IRON_INGOT) to listOf(
                Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
                Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
                Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_HOE
            ),
            listOf(Material.GOLD_INGOT) to listOf(
                Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
                Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE
            ),
            listOf(Material.DIAMOND) to listOf(
                Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
                Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE
            ),
            listOf(Material.NETHERITE_INGOT) to listOf(
                Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS,
                Material.NETHERITE_SWORD, Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE
            ),
            listOf(Material.TURTLE_SCUTE) to listOf(Material.TURTLE_HELMET),
            listOf(Material.PHANTOM_MEMBRANE) to listOf(Material.ELYTRA)
        )

    /** Adds spear (post-1.21.9) and copper tool/armor repair units when those materials exist. */
    private fun addSpearAndCopperMaterials() {
        repair[Tag.PLANKS.values] = repair[Tag.PLANKS.values]!! + getMaterialIfExists("WOODEN_SPEAR").toList()

        val stoneKey = listOf(Material.COBBLESTONE, Material.COBBLED_DEEPSLATE, Material.BLACKSTONE)
        repair[stoneKey] = repair[stoneKey]!! + getMaterialIfExists("STONE_SPEAR").toList()

        val copperKey = listOf(getMaterialIfExists("COPPER_INGOT")).flatten()
        if (copperKey.isNotEmpty()) {
            repair[copperKey] = listOf(
                getMaterialIfExists("COPPER_HELMET"),
                getMaterialIfExists("COPPER_CHESTPLATE"),
                getMaterialIfExists("COPPER_LEGGINGS"),
                getMaterialIfExists("COPPER_BOOTS"),
                getMaterialIfExists("COPPER_SWORD"),
                getMaterialIfExists("COPPER_PICKAXE"),
                getMaterialIfExists("COPPER_AXE"),
                getMaterialIfExists("COPPER_SHOVEL"),
                getMaterialIfExists("COPPER_HOE"),
                getMaterialIfExists("COPPER_SPEAR")
            ).flatten()
        }

        repair[listOf(Material.IRON_INGOT)] = repair[listOf(Material.IRON_INGOT)]!! + getMaterialIfExists("IRON_SPEAR").toList()
        repair[listOf(Material.GOLD_INGOT)] = repair[listOf(Material.GOLD_INGOT)]!! + getMaterialIfExists("GOLDEN_SPEAR").toList()
        repair[listOf(Material.DIAMOND)] = repair[listOf(Material.DIAMOND)]!! + getMaterialIfExists("DIAMOND_SPEAR").toList()
        repair[listOf(Material.NETHERITE_INGOT)] = repair[listOf(Material.NETHERITE_INGOT)]!! + getMaterialIfExists("NETHERITE_SPEAR").toList()
    }

    /** [name] as a single-element list if the [Material] exists on this server version, else empty. */
    private fun getMaterialIfExists(name: String): List<Material> = try {
        listOf(Material.valueOf(name))
    } catch (_: IllegalArgumentException) {
        emptyList()
    }

    /** Whether this server version has spear items (added in 1.21.9). */
    private fun hasSpears(): Boolean = try {
        Material.valueOf("IRON_SPEAR")
        true
    } catch (_: IllegalArgumentException) {
        false
    }
}
