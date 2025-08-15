package com.willfp.eco.internal.spigot.proxy.common.modern

import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.item.ContinuallyAppliedPersistentDataContainer
import com.willfp.eco.internal.spigot.proxy.common.item.ImplementedFIS
import com.willfp.eco.internal.spigot.proxy.common.makePdc
import com.willfp.eco.internal.spigot.proxy.common.mergeIfNeeded
import com.willfp.eco.internal.spigot.proxy.common.setPdc
import com.willfp.eco.internal.spigot.proxy.common.toAdventure
import com.willfp.eco.internal.spigot.proxy.common.toItem
import com.willfp.eco.internal.spigot.proxy.common.toMaterial
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.toComponent
import com.willfp.eco.util.toLegacy
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Unit
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.enchantment.ItemEnchantments
import org.bukkit.craftbukkit.CraftRegistry
import org.bukkit.craftbukkit.enchantments.CraftEnchantment
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import kotlin.math.max

private val unstyledComponent = Component.empty().style {
    it.color(null).decoration(TextDecoration.ITALIC, false)
}

private fun Component.unstyled(): Component {
    return unstyledComponent.append(this)
}

open class NewEcoFastItemStack(
    private val bukkit: ItemStack,
) : ImplementedFIS {
    // Cast is there because, try as I might, I can't get IntellIJ to recognise half the classes in the dev bundle
    @Suppress("USELESS_CAST")
    protected val handle = bukkit.asNMSStack() as net.minecraft.world.item.ItemStack

    private val pdc = (handle.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: CompoundTag()).makePdc()

    override fun getEnchants(checkStored: Boolean): Map<Enchantment, Int> {
        val enchantments = handle.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY

        val map = mutableMapOf<Enchantment, Int>()

        for ((enchantment, level) in enchantments.entrySet()) {
            val bukkit = CraftEnchantment.minecraftToBukkit(enchantment.value())

            map[bukkit] = level
        }

        if (checkStored) {
            val stored = handle.get(DataComponents.STORED_ENCHANTMENTS) ?: return map

            for ((enchantment, level) in stored.entrySet()) {
                val bukkit = CraftEnchantment.minecraftToBukkit(enchantment.value())

                map[bukkit] = max(map.getOrDefault(bukkit, 0), level)
            }
        }

        return map
    }

    override fun getEnchantmentLevel(
        enchantment: Enchantment,
        checkStored: Boolean
    ): Int {
        val minecraft = CraftRegistry.bukkitToMinecraftHolder(
            enchantment,
            Registries.ENCHANTMENT
        )

        val enchantments = handle.get(DataComponents.ENCHANTMENTS) ?: return 0
        var level = enchantments.getLevel(minecraft)

        if (checkStored) {
            val storedEnchantments = handle.get(DataComponents.STORED_ENCHANTMENTS) ?: return 0
            level = max(level, storedEnchantments.getLevel(minecraft))
        }

        return level
    }

    override fun setLore(lore: List<String>?) = setLoreComponents(lore?.map { it.toComponent() })

    override fun setLoreComponents(lore: List<Component>?) {
        if (lore == null) {
            handle.set<ItemLore>(DataComponents.LORE, null)
        } else {
            val components = lore
                .map { it.unstyled() }
                .map { it.toNMS() }

            handle.set(
                DataComponents.LORE, ItemLore(
                    components,
                    components
                )
            )
        }

        apply()
    }

    override fun getLoreComponents(): List<Component> {
        return handle.get(DataComponents.LORE)?.lines?.map { it.toAdventure() } ?: emptyList()
    }

    override fun getLore(): List<String> =
        getLoreComponents().map { StringUtils.toLegacy(it) }

    override fun setDisplayName(name: Component?) {
        if (name == null) {
            handle.set<net.minecraft.network.chat.Component>(DataComponents.ITEM_NAME, null)
            handle.set<net.minecraft.network.chat.Component>(DataComponents.CUSTOM_NAME, null)
        } else {
            handle.set(
                DataComponents.CUSTOM_NAME,
                name.unstyled().toNMS()
            )
        }

        apply()
    }

    override fun setDisplayName(name: String?) = setDisplayName(name?.toComponent())

    override fun getDisplayNameComponent(): Component {
        return handle.get(DataComponents.CUSTOM_NAME)?.toAdventure()
            ?: handle.get(DataComponents.ITEM_NAME)?.toAdventure()
            ?: Component.translatable(bukkit.type.toItem().descriptionId)
    }

    override fun getDisplayName(): String = displayNameComponent.toLegacy()

    protected fun <T> net.minecraft.world.item.ItemStack.modifyComponent(
        component: DataComponentType<T>,
        modifier: (T) -> T
    ) {
        val current = handle.get(component) ?: return
        this.set(component, modifier(current))
    }

    override fun addItemFlags(vararg hideFlags: ItemFlag) {
        for (flag in hideFlags) {
            when (flag) {
                ItemFlag.HIDE_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.ENCHANTMENTS) { enchantments ->
                        enchantments.withTooltip(false)
                    }
                }

                ItemFlag.HIDE_ATTRIBUTES -> {
                    handle.modifyComponent(DataComponents.ATTRIBUTE_MODIFIERS) { attributes ->
                        attributes.withTooltip(false)
                    }
                }

                ItemFlag.HIDE_UNBREAKABLE -> {
                    handle.modifyComponent(DataComponents.UNBREAKABLE) { unbreakable ->
                        unbreakable.withTooltip(false)
                    }
                }

                ItemFlag.HIDE_DESTROYS -> {
                    handle.modifyComponent(DataComponents.CAN_BREAK) { destroys ->
                        destroys.withTooltip(false)
                    }
                }

                ItemFlag.HIDE_PLACED_ON -> {
                    handle.modifyComponent(DataComponents.CAN_PLACE_ON) { placedOn ->
                        placedOn.withTooltip(false)
                    }
                }

                ItemFlag.HIDE_ADDITIONAL_TOOLTIP -> {
                    handle.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
                }

                ItemFlag.HIDE_DYE -> {
                    handle.modifyComponent(DataComponents.DYED_COLOR) { dyed ->
                        dyed.withTooltip(false)
                    }
                }

                ItemFlag.HIDE_ARMOR_TRIM -> {
                    handle.modifyComponent(DataComponents.TRIM) { trim ->
                        trim.withTooltip(false)
                    }
                }

                ItemFlag.HIDE_STORED_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.STORED_ENCHANTMENTS) { storedEnchants ->
                        storedEnchants.withTooltip(false)
                    }
                }
            }
        }

        apply()
    }

    override fun removeItemFlags(vararg hideFlags: ItemFlag) {
        for (flag in hideFlags) {
            when (flag) {
                ItemFlag.HIDE_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.ENCHANTMENTS) { enchantments ->
                        enchantments.withTooltip(true)
                    }
                }

                ItemFlag.HIDE_ATTRIBUTES -> {
                    handle.modifyComponent(DataComponents.ATTRIBUTE_MODIFIERS) { attributes ->
                        attributes.withTooltip(true)
                    }
                }

                ItemFlag.HIDE_UNBREAKABLE -> {
                    handle.modifyComponent(DataComponents.UNBREAKABLE) { unbreakable ->
                        unbreakable.withTooltip(true)
                    }
                }

                ItemFlag.HIDE_DESTROYS -> {
                    handle.modifyComponent(DataComponents.CAN_BREAK) { destroys ->
                        destroys.withTooltip(true)
                    }
                }

                ItemFlag.HIDE_PLACED_ON -> {
                    handle.modifyComponent(DataComponents.CAN_PLACE_ON) { placedOn ->
                        placedOn.withTooltip(true)
                    }
                }

                ItemFlag.HIDE_ADDITIONAL_TOOLTIP -> {
                    handle.remove(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                }

                ItemFlag.HIDE_DYE -> {
                    handle.modifyComponent(DataComponents.DYED_COLOR) { dyed ->
                        dyed.withTooltip(true)
                    }
                }

                ItemFlag.HIDE_ARMOR_TRIM -> {
                    handle.modifyComponent(DataComponents.TRIM) { trim ->
                        trim.withTooltip(true)
                    }
                }

                ItemFlag.HIDE_STORED_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.STORED_ENCHANTMENTS) { storedEnchants ->
                        storedEnchants.withTooltip(true)
                    }
                }
            }
        }

        apply()
    }

    override fun getItemFlags(): Set<ItemFlag> {
        val currentFlags = mutableSetOf<ItemFlag>()
        for (f in ItemFlag.entries) {
            if (hasItemFlag(f)) {
                currentFlags.add(f)
            }
        }
        return currentFlags
    }

    override fun hasItemFlag(flag: ItemFlag): Boolean {
        return when (flag) {
            ItemFlag.HIDE_ENCHANTS -> {
                val enchantments = handle.get(DataComponents.ENCHANTMENTS) ?: return false
                !enchantments.showInTooltip
            }

            ItemFlag.HIDE_ATTRIBUTES -> {
                val attributes = handle.get(DataComponents.ATTRIBUTE_MODIFIERS) ?: return false
                !attributes.showInTooltip
            }

            ItemFlag.HIDE_UNBREAKABLE -> {
                val unbreakable = handle.get(DataComponents.UNBREAKABLE) ?: return false
                !unbreakable.showInTooltip
            }

            ItemFlag.HIDE_DESTROYS -> {
                val destroys = handle.get(DataComponents.CAN_BREAK) ?: return false
                !destroys.showInTooltip()
            }

            ItemFlag.HIDE_PLACED_ON -> {
                val placedOn = handle.get(DataComponents.CAN_PLACE_ON) ?: return false
                !placedOn.showInTooltip()
            }

            ItemFlag.HIDE_ADDITIONAL_TOOLTIP -> {
                handle.get(DataComponents.HIDE_ADDITIONAL_TOOLTIP) != null
            }

            ItemFlag.HIDE_DYE -> {
                val dyed = handle.get(DataComponents.DYED_COLOR) ?: return false
                !dyed.showInTooltip()
            }

            ItemFlag.HIDE_ARMOR_TRIM -> {
                val armorTrim = handle.get(DataComponents.TRIM) ?: return false
                !armorTrim.showInTooltip
            }

            ItemFlag.HIDE_STORED_ENCHANTS -> {
                val storedEnchants = handle.get(DataComponents.STORED_ENCHANTMENTS) ?: return false
                !storedEnchants.showInTooltip
            }
        }
    }

    override fun getRepairCost(): Int {
        return handle.get(DataComponents.REPAIR_COST) ?: 0
    }

    override fun setRepairCost(cost: Int) {
        handle.set(DataComponents.REPAIR_COST, cost)

        apply()
    }

    override fun getPersistentDataContainer(): PersistentDataContainer {
        return ContinuallyAppliedPersistentDataContainer(this.pdc, this)
    }

    override fun getAmount(): Int = handle.getCount()

    override fun setAmount(amount: Int) {
        handle.setCount(amount)
        apply()
    }

    override fun setType(material: org.bukkit.Material) {
        @Suppress("DEPRECATION")
        handle.setItem(material.toItem())
        apply()
    }

    override fun getType(): org.bukkit.Material = handle.getItem().toMaterial()

    /*
    Custom model data doesn't work based on an integer since 1.21.3, so these methods do nothing
     */

    override fun getCustomModelData(): Int? =
        null

    override fun setCustomModelData(data: Int?) {
        if (data == null) {
            handle.remove(DataComponents.CUSTOM_MODEL_DATA)
        }

        apply()
    }

    // END

    override fun equals(other: Any?): Boolean {
        if (other !is NewEcoFastItemStack) {
            return false
        }

        return other.hashCode() == this.hashCode()
    }

    override fun hashCode(): Int {
        return net.minecraft.world.item.ItemStack.hashItemAndComponents(handle)
    }

    override fun apply() {
        val customData = handle.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
        val updated = customData.update {
            it.setPdc(pdc)
        }

        if (updated.isEmpty) {
            handle.remove(DataComponents.CUSTOM_DATA)
        } else {
            handle.set(DataComponents.CUSTOM_DATA, updated)
        }

        bukkit.mergeIfNeeded(handle)
    }

    override fun unwrap(): ItemStack {
        return bukkit
    }
}
