package com.willfp.eco.internal.gui.view

import com.willfp.eco.core.gui.view.LocationViewBuilder
import com.willfp.eco.core.gui.view.MerchantViewBuilder
import com.willfp.eco.core.gui.view.ViewBuilder
import org.bukkit.Location
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.view.builder.InventoryViewBuilder
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder
import org.bukkit.inventory.view.builder.MerchantInventoryViewBuilder

// Thin wrappers over Bukkit's builders. The only behaviour here is routing the title through
// ViewTitles, which is the one call that differs between server software.
open class EcoViewBuilder<V : InventoryView, B : InventoryViewBuilder<V>>(
    protected var handle: B
) : ViewBuilder<V> {
    override fun title(title: String?): ViewBuilder<V> = apply {
        handle = ViewTitles.apply(handle, title)
    }

    override fun build(player: HumanEntity): V = handle.build(player)

    @Suppress("UNCHECKED_CAST")
    override fun copy(): ViewBuilder<V> = EcoViewBuilder<V, B>(handle.copy() as B)
}

class EcoLocationViewBuilder<V : InventoryView>(
    handle: LocationInventoryViewBuilder<V>
) : EcoViewBuilder<V, LocationInventoryViewBuilder<V>>(handle), LocationViewBuilder<V> {
    override fun title(title: String?): LocationViewBuilder<V> = apply {
        handle = ViewTitles.apply(handle, title)
    }

    override fun location(location: Location): LocationViewBuilder<V> = apply {
        handle = handle.location(location)
    }

    override fun checkReachable(checkReachable: Boolean): LocationViewBuilder<V> = apply {
        handle = handle.checkReachable(checkReachable)
    }

    override fun copy(): LocationViewBuilder<V> = EcoLocationViewBuilder(handle.copy())
}

class EcoMerchantViewBuilder<V : InventoryView>(
    handle: MerchantInventoryViewBuilder<V>
) : EcoViewBuilder<V, MerchantInventoryViewBuilder<V>>(handle), MerchantViewBuilder<V> {
    override fun title(title: String?): MerchantViewBuilder<V> = apply {
        handle = ViewTitles.apply(handle, title)
    }

    override fun merchant(merchant: Merchant): MerchantViewBuilder<V> = apply {
        handle = handle.merchant(merchant)
    }

    override fun checkReachable(checkReachable: Boolean): MerchantViewBuilder<V> = apply {
        handle = handle.checkReachable(checkReachable)
    }

    override fun copy(): MerchantViewBuilder<V> = EcoMerchantViewBuilder(handle.copy())
}
