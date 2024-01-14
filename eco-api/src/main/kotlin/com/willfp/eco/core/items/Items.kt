@file:JvmName("ItemsExtensions")

package com.willfp.eco.core.items

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer

/** @see Items.toLookupString */
fun ItemStack?.toLookupString(): String =
    Items.toLookupString(this)

/** @see Items.mergeFrom */
fun ItemStack.mergeFrom(other: ItemStack): ItemStack =
    Items.mergeFrom(other, this)

/** @see Items.mergeFrom */
fun ItemMeta.mergeFrom(other: ItemMeta): ItemMeta =
    Items.mergeFrom(other, this)

/**
 * @see Items.getBaseNBT
 * @see Items.setBaseNBT
 */
var ItemStack.baseNBT: PersistentDataContainer
    get() = Items.getBaseNBT(this)
    set(value) {
        Items.setBaseNBT(this, value)
    }

/** @see Items.setBaseNBT */
fun ItemStack.clearNBT() =
    Items.setBaseNBT(this, null)

/** @see Items.toSNBT */
fun ItemStack.toSNBT() =
    Items.toSNBT(this)

/** @see Items.isEmpty */
@Deprecated("Use ItemStack.isEcoEmpty", ReplaceWith("Items.isEmpty(this)"))
val ItemStack?.isEmpty: Boolean
    get() = Items.isEmpty(this)

/** @see Items.isEmpty */
val ItemStack?.isEcoEmpty: Boolean
    get() = Items.isEmpty(this)

/** @see Items.matchesAny */
fun Collection<TestableItem>.matches(item: ItemStack): Boolean =
    Items.matchesAny(item, this)

/** @see Items.matchesAny */
fun Collection<TestableItem>.matches(items: Collection<ItemStack>): Boolean =
    Items.matchesAny(items, this)
