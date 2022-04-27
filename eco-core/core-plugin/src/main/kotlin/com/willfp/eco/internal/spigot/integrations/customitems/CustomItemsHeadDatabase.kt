package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import me.arcaniax.hdb.api.DatabaseLoadEvent
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsHeadDatabase(
    plugin: EcoPlugin
) : CustomItemsIntegration {
    private val provider = HeadDBProvider()

    init {
        plugin.eventManager.registerListener(provider)
    }

    override fun registerProvider() {
        Items.registerItemProvider(HeadDBProvider())
    }

    override fun getPluginName(): String {
        return "HeadDatabase"
    }

    private inner class HeadDBProvider : ItemProvider("headdb"), Listener {
        private lateinit var api: HeadDatabaseAPI

        override fun provideForKey(key: String): TestableItem? {
            if (!this::api.isInitialized) {
                return null
            }

            val head = api.getItemHead(key) ?: return null
            val namespacedKey = NamespacedKeyUtils.create("headdb", key)
            return CustomItem(
                namespacedKey,
                Predicate { test: ItemStack ->
                    val found = api.getItemID(test) ?: return@Predicate false
                    found == key
                },
                head
            )
        }
        @EventHandler
        fun onLoad(@Suppress("UNUSED_PARAMETER") event: DatabaseLoadEvent) {
            api = HeadDatabaseAPI()
        }
    }
}