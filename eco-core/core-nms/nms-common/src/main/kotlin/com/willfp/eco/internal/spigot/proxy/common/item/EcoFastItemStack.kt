package com.willfp.eco.internal.spigot.proxy.common.item

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.spigot.proxy.common.NBT_TAG_STRING
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.makePdc
import com.willfp.eco.internal.spigot.proxy.common.mergeIfNeeded
import com.willfp.eco.internal.spigot.proxy.common.setPdc
import com.willfp.eco.internal.spigot.proxy.common.toItem
import com.willfp.eco.internal.spigot.proxy.common.toMaterial
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.toComponent
import com.willfp.eco.util.toLegacy
import net.kyori.adventure.text.Component
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

@Suppress("UsePropertyAccessSyntax")
class EcoFastItemStack(
    private val bukkit: org.bukkit.inventory.ItemStack
) : FastItemStack {
    private val handle = bukkit.asNMSStack()
    private val pdc = (if (handle.hasTag()) handle.getTag()!! else CompoundTag()).makePdc()

    override fun getEnchants(checkStored: Boolean): Map<Enchantment, Int> {
        val enchantmentNBT =
            if (checkStored && handle.getItem() === Items.ENCHANTED_BOOK) EnchantedBookItem.getEnchantments(
                handle
            ) else handle.getEnchantmentTags()

        val foundEnchantments = mutableMapOf<Enchantment, Int>()

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

    override fun getEnchantmentLevel(
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

    override fun setLore(lore: List<String>?) = setLoreComponents(lore?.map { it.toComponent() })

    override fun setLoreComponents(lore: List<Component>?) {
        val jsonLore = mutableListOf<String>()

        if (lore != null) {
            for (s in lore) {
                jsonLore.add(StringUtils.componentToJson(s))
            }
        }

        val displayTag = handle.getOrCreateTagElement("display")

        if (!displayTag.contains("Lore")) {
            displayTag.put("Lore", ListTag())
        }

        val loreTag = displayTag.getList("Lore", NBT_TAG_STRING)

        loreTag.clear()

        for (s in jsonLore) {
            loreTag.add(StringTag.valueOf(s))
        }

        apply()
    }

    override fun getLoreComponents(): List<Component> {
        val displayTag = handle.getTagElement("display") ?: return emptyList()

        if (!displayTag.contains("Lore")) {
            return emptyList()
        }

        val loreTag = displayTag.getList("Lore", NBT_TAG_STRING)
        val jsonLore = mutableListOf<String>()

        for (i in loreTag.indices) {
            jsonLore.add(loreTag.getString(i))
        }

        return jsonLore.map { StringUtils.jsonToComponent(it) }
    }

    override fun getLore(): List<String> =
        getLoreComponents().map { StringUtils.toLegacy(it) }

    override fun setDisplayName(name: Component?) {
        val displayTag = handle.getOrCreateTagElement("display")

        displayTag.remove("Name")

        if (name != null) {
            displayTag.put("Name", StringTag.valueOf(StringUtils.componentToJson(name)))
        }

        apply()
    }

    override fun setDisplayName(name: String?) = setDisplayName(name?.toComponent())

    override fun getDisplayNameComponent(): Component {
        val displayTag =
            handle.getTagElement("display") ?: return Component.translatable(bukkit.type.toItem().getDescriptionId())

        if (!displayTag.contains("Name")) {
            return Component.translatable(bukkit.type.toItem().getDescriptionId())
        }

        val nameTag = displayTag.getString("Name")

        return StringUtils.jsonToComponent(nameTag)
    }

    override fun getDisplayName(): String = displayNameComponent.toLegacy()

    override fun addItemFlags(vararg hideFlags: ItemFlag) {
        for (f in hideFlags) {
            this.flagBits = this.flagBits or getBitModifier(f)
        }

        apply()
    }

    override fun removeItemFlags(vararg hideFlags: ItemFlag) {
        for (f in hideFlags) {
            this.flagBits = this.flagBits and getBitModifier(f).inv()
        }

        apply()
    }

    override fun getItemFlags(): Set<ItemFlag> {
        val currentFlags = mutableSetOf<ItemFlag>()
        for (f in ItemFlag.values()) {
            if (hasItemFlag(f)) {
                currentFlags.add(f)
            }
        }
        return currentFlags
    }

    override fun hasItemFlag(flag: ItemFlag): Boolean {
        val bitModifier = getBitModifier(flag)
        return this.flagBits and bitModifier == bitModifier
    }

    override fun getBaseTag(): PersistentDataContainer =
        (if (handle.hasTag()) handle.getTag()!! else CompoundTag()).makePdc(base = true)

    override fun setBaseTag(container: PersistentDataContainer?) {
        (if (handle.hasTag()) handle.getTag()!! else CompoundTag()).setPdc(container, item = handle)
        apply()
    }

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    private var flagBits: Byte
        get() =
            if (handle.hasTag() && handle.getTag()!!.contains(
                    "HideFlags",
                    99
                )
            ) handle.getTag()!!.getInt("HideFlags").toByte() else 0
        set(value) =
            handle.getOrCreateTag().putInt("HideFlags", value.toInt())

    override fun getRepairCost(): Int {
        return handle.getBaseRepairCost()
    }

    override fun setRepairCost(cost: Int) {
        handle.setRepairCost(cost)
        apply()
    }

    override fun getPersistentDataContainer(): PersistentDataContainer {
        return ContinuallyAppliedPersistentDataContainer(this.pdc, this)
    }

    override fun getAmount(): Int = handle.getCount()

    override fun setAmount(amount: Int) {
        handle.setCount(amount)
    }

    override fun setType(material: Material) {
        if (material == Material.AIR) {
            handle.setTag(null)
        }
        @Suppress("DEPRECATION")
        handle.setItem(material.toItem())
        apply()
    }

    override fun getType(): Material = handle.getItem().toMaterial()

    override fun getCustomModelData(): Int? = handle.getTag()?.getInt("CustomModelData")

    override fun setCustomModelData(data: Int?) {
        if (data == null) {
            val tag = handle.getTag() ?: return
            tag.remove("CustomModelData")
        } else {
            handle.getOrCreateTag().putInt("CustomModelData", data)
        }

        apply()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EcoFastItemStack) {
            return false
        }

        return other.hashCode() == this.hashCode()
    }

    override fun hashCode(): Int {
        @Suppress("UNNECESSARY_SAFE_CALL")
        return handle.getTag()?.hashCode() ?: (0b00010101 * 31 + Item.getId(handle.getItem()))
    }

    internal fun apply() {
        if (handle.hasTag()) {
            handle.getTag()?.setPdc(this.pdc)
        }

        bukkit.mergeIfNeeded(handle)
    }

    private fun getBitModifier(hideFlag: ItemFlag): Byte {
        return (1 shl hideFlag.ordinal).toByte()
    }

    override fun unwrap(): org.bukkit.inventory.ItemStack {
        return bukkit
    }
}

private class ContinuallyAppliedPersistentDataContainer(
    val handle: PersistentDataContainer,
    val fis: EcoFastItemStack
) : PersistentDataContainer by handle {
    override fun <T : Any, Z : Any> set(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
        handle.set(key, type, value)
        fis.apply()
    }

    override fun remove(key: NamespacedKey) {
        handle.remove(key)
        fis.apply()
    }
}
