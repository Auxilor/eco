package com.willfp.eco.internal.spigot.proxies

import com.willfp.eco.core.items.ItemComponentResult
import org.bukkit.inventory.ItemStack

interface ItemComponentsProxy {
    /**
     * Returns a copy of [item] with [components] applied.
     *
     * Components are parsed with the vanilla codecs, using the same format as
     * item components in commands. Invalid components are skipped, with a
     * human-readable message added to [ItemComponentResult.getErrors].
     */
    fun withComponents(item: ItemStack, components: Map<String, Any?>): ItemComponentResult

    /**
     * Returns the components on [item], keyed by component id.
     */
    fun getComponents(item: ItemStack): Map<String, Any?>

    /**
     * Returns the value of the component [key] on [item], or null if the item
     * doesn't have it, or if [key] isn't a component id.
     */
    fun getComponent(item: ItemStack, key: String): Any?

    /**
     * Returns a copy of [item] without the components named by [keys].
     *
     * Keys the item doesn't have, and keys that aren't component ids, are
     * skipped.
     */
    fun removeComponents(item: ItemStack, keys: Collection<String>): ItemStack
}
