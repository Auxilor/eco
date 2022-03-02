package com.willfp.eco.internal.spigot.proxy.common.fast

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxy.common.commonsProvider
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.StringUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import kotlin.experimental.and

@Suppress("UsePropertyAccessSyntax")
class EcoFastItemStack(
    private val bukkit: org.bukkit.inventory.ItemStack
) : FastItemStack {
    private var loreCache: List<String>? = null
    private val handle = commonsProvider.asNMSStack(bukkit)

    override fun getEnchants(checkStored: Boolean): Map<Enchantment, Int> {
        val enchantmentNBT =
            if (checkStored && handle.getItem() === Items.ENCHANTED_BOOK) EnchantedBookItem.getEnchantments(
                handle
            ) else handle.getEnchantmentTags()
        val foundEnchantments: MutableMap<Enchantment, Int> = HashMap()
        for (base in enchantmentNBT) {
            val compound = base as CompoundTag
            val key = compound.getString("id")
            val level = ('\uffff'.code.toShort() and compound.getShort("lvl")).toInt()
            val found = Enchantment.getByKey(NamespacedKeyUtils.fromStringOrNull(key))
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
            if (checkStored && handle.getItem() === Items.ENCHANTED_BOOK) EnchantedBookItem.getEnchantments(
                handle
            ) else handle.getEnchantmentTags()
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

        val loreTag = displayTag.getList("Lore", commonsProvider.nbtTagString)

        loreTag.clear()

        for (s in jsonLore) {
            loreTag.add(StringTag.valueOf(s))
        }

        apply()
    }

    override fun getLore(): List<String> {
        if (loreCache != null) {
            return loreCache!!
        }

        val lore = this.getLoreJSON().map { StringUtils.jsonToLegacy(it) }
        loreCache = lore
        return lore
    }

    private fun getLoreJSON(): List<String> {
        val displayTag = handle.getTagElement("display") ?: return emptyList()

        if (!displayTag.contains("Lore")) {
            return emptyList()
        }

        val loreTag = displayTag.getList("Lore", commonsProvider.nbtTagString)
        val lore = ArrayList<String>(loreTag.size)

        for (i in loreTag.indices) {
            lore.add(loreTag.getString(i))
        }

        return lore
    }

    override fun addItemFlags(vararg hideFlags: ItemFlag) {
        for (flag in hideFlags) {
            this.flagBits = this.flagBits or getBitModifier(flag)
        }

        apply()
    }

    override fun removeItemFlags(vararg hideFlags: ItemFlag) {
        for (flag in hideFlags) {
            this.flagBits = this.flagBits and getBitModifier(flag)
        }

        apply()
    }

    override fun getItemFlags(): MutableSet<ItemFlag> {
        val flags = mutableSetOf<ItemFlag>()

        var flagArr: Array<ItemFlag>
        val size = ItemFlag.values().also { flagArr = it }.size

        for (i in 0 until size) {
            val flag = flagArr[i]
            if (this.hasItemFlag(flag)) {
                flags.add(flag)
            }
        }

        return flags
    }

    override fun hasItemFlag(flag: ItemFlag): Boolean {
        val bitModifier = getBitModifier(flag)
        return this.flagBits and bitModifier == bitModifier
    }

    private var flagBits: Int
        get() =
            if (handle.hasTag() && handle.getTag()!!.contains(
                    "HideFlags",
                    99
                )
            ) handle.getTag()!!.getInt("HideFlags") else 0
        set(value) =
            handle.getOrCreateTag().putInt("HideFlags", value)

    override fun getRepairCost(): Int {
        return handle.getBaseRepairCost()
    }

    override fun setRepairCost(cost: Int) {
        handle.setRepairCost(cost)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EcoFastItemStack) {
            return false
        }

        return other.hashCode() == this.hashCode()
    }

    override fun hashCode(): Int {
        return handle.getTag()?.hashCode() ?: (0b00010101 * 31 + Item.getId(handle.getItem()))
    }

    private fun apply() {
        commonsProvider.mergeIfNeeded(bukkit, handle)
    }

    private fun getBitModifier(hideFlag: ItemFlag): Int {
        return 1 shl hideFlag.ordinal
    }

    override fun unwrap(): org.bukkit.inventory.ItemStack {
        return bukkit
    }
}
