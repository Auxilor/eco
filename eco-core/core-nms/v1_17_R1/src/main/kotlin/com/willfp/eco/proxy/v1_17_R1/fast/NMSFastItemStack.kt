package com.willfp.eco.proxy.v1_17_R1.fast

import com.willfp.eco.internal.fast.EcoFastItemStack
import com.willfp.eco.util.StringUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey
import org.bukkit.enchantments.Enchantment
import kotlin.experimental.and

class NMSFastItemStack(itemStack: org.bukkit.inventory.ItemStack) : EcoFastItemStack<ItemStack>(
    FastItemStackUtils.getNMSStack(itemStack), itemStack
) {
    private var loreCache: List<String>? = null

    override fun getEnchantmentsOnItem(checkStored: Boolean): Map<Enchantment, Int> {
        val enchantmentNBT =
            if (checkStored && handle.item === Items.ENCHANTED_BOOK) EnchantedBookItem.getEnchantments(
                handle
            ) else handle.enchantmentTags
        val foundEnchantments: MutableMap<Enchantment, Int> = HashMap()
        for (base in enchantmentNBT) {
            val compound = base as CompoundTag
            val key = compound.getString("id")
            val level = ('\uffff'.code.toShort() and compound.getShort("lvl")).toInt()
            val found = Enchantment.getByKey(CraftNamespacedKey.fromStringOrNull(key))
            if (found != null) {
                foundEnchantments[found] = level
            }
        }
        return foundEnchantments
    }

    override fun getLevelOnItem(
        enchantment: Enchantment,
        checkStored: Boolean
    ): Int {
        val enchantmentNBT =
            if (checkStored && handle.item === Items.ENCHANTED_BOOK) EnchantedBookItem.getEnchantments(
                handle
            ) else handle.enchantmentTags
        for (base in enchantmentNBT) {
            val compound = base as CompoundTag
            val key = compound.getString("id")
            if (key != enchantment.key.toString()) {
                continue
            }
            return ('\uffff'.code.toShort() and compound.getShort("lvl")).toInt()
        }
        return 0
    }

    override fun setLore(lore: List<String>?) {
        loreCache = null
        val jsonLore: MutableList<String> = ArrayList()

        if (lore != null) {
            for (s in lore) {
                jsonLore.add(StringUtils.legacyToJson(s))
            }
        }

        val displayTag = handle.getOrCreateTagElement("display")

        if (!displayTag.contains("Lore")) {
            displayTag.put("Lore", ListTag())
        }

        val loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING)

        loreTag.clear()

        for (s in jsonLore) {
            loreTag.add(StringTag.valueOf(s))
        }

        apply()
    }

    override fun getLore(): List<String> {
        if (loreCache != null) {
            return loreCache as List<String>
        }

        val lore: MutableList<String> = ArrayList()

        for (s in loreJSON) {
            lore.add(StringUtils.jsonToLegacy(s))
        }

        loreCache = lore
        return lore
    }

    private val loreJSON: List<String>
        get() {
            val displayTag = handle.getOrCreateTagElement("display")
            return if (displayTag.contains("Lore")) {
                val loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING)
                val lore: MutableList<String> = ArrayList(loreTag.size)

                for (i in loreTag.indices) {
                    lore.add(loreTag.getString(i))
                }

                lore
            } else {
                ArrayList()
            }
        }

    private fun apply() {
        if (bukkit !is CraftItemStack) {
            bukkit.itemMeta = CraftItemStack.asCraftMirror(handle).itemMeta
        }
    }
}