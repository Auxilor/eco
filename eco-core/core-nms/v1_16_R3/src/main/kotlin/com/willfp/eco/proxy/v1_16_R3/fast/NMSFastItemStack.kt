package com.willfp.eco.proxy.v1_16_R3.fast

import com.willfp.eco.internal.fast.EcoFastItemStack
import com.willfp.eco.proxy.v1_16_R3.FastItemStackUtils
import com.willfp.eco.util.StringUtils
import net.minecraft.server.v1_16_R3.*
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import java.lang.reflect.Field
import kotlin.experimental.and

class NMSFastItemStack(itemStack: org.bukkit.inventory.ItemStack) : EcoFastItemStack<ItemStack>(
    FastItemStackUtils.getNMSStack(itemStack), itemStack
) {
    private var loreCache: List<String>? = null
    override fun getEnchantmentsOnItem(checkStored: Boolean): Map<Enchantment, Int> {
        val enchantmentNBT = if (checkStored && handle.item === Items.ENCHANTED_BOOK) ItemEnchantedBook.d(
            handle
        ) else handle.enchantments
        val foundEnchantments: MutableMap<Enchantment, Int> = HashMap()
        for (base in enchantmentNBT) {
            val compound = base as NBTTagCompound
            val key = compound.getString("id")
            val level: Int = ('\uffff'.code.toShort() and compound.getShort("lvl")).toInt()
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
        val enchantmentNBT = if (checkStored && handle.item === Items.ENCHANTED_BOOK) ItemEnchantedBook.d(
            handle
        ) else handle.enchantments
        for (base in enchantmentNBT) {
            val compound = base as NBTTagCompound
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
        val displayTag = handle.a("display")
        if (!displayTag.hasKey("Lore")) {
            displayTag["Lore"] = NBTTagList()
        }
        val loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING)
        loreTag.clear()
        for (s in jsonLore) {
            loreTag.add(NBTTagString.a(s))
        }
        apply()
    }

    override fun getLore(): List<String> {
        if (loreCache != null) {
            return loreCache as List<String>
        }
        val lore: MutableList<String> = ArrayList()
        for (s in getLoreJSON()) {
            lore.add(StringUtils.jsonToLegacy(s))
        }
        loreCache = lore
        return lore
    }

    private fun getLoreJSON(): List<String> {
        val displayTag = handle.a("display")
        return if (displayTag.hasKey("Lore")) {
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
            if (handle.hasTag() && handle.tag!!.hasKeyOfType(
                    "HideFlags",
                    99
                )
            ) handle.tag!!.getInt("HideFlags") else 0
        set(value) =
            handle.orCreateTag.setInt("HideFlags", value)

    override fun getRepairCost(): Int {
        return handle.repairCost;
    }

    override fun setRepairCost(cost: Int) {
        handle.repairCost = cost;
    }

    private fun apply() {
        if (bukkit !is CraftItemStack) {
            bukkit.itemMeta = CraftItemStack.asCraftMirror(handle).itemMeta
        }
    }

    companion object {
        private var field: Field

        init {
            lateinit var temp: Field
            try {
                val handleField = CraftItemStack::class.java.getDeclaredField("handle")
                handleField.isAccessible = true
                temp = handleField
            } catch (e: ReflectiveOperationException) {
                e.printStackTrace()
            }
            field = temp
        }

        fun getNMSStack(itemStack: org.bukkit.inventory.ItemStack): ItemStack? {
            return if (itemStack !is CraftItemStack) {
                CraftItemStack.asNMSCopy(itemStack)
            } else {
                field.get(itemStack) as ItemStack
            }
        }
    }
}