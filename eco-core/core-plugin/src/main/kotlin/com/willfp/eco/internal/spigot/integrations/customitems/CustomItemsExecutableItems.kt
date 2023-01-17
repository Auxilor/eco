package com.willfp.eco.internal.spigot.integrations.customitems

import com.ssomar.score.api.executableitems.ExecutableItemsAPI
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.inventory.ItemStack
import java.util.Optional
import java.util.function.Predicate

class CustomItemsExecutableItems : CustomItemsIntegration {
    override fun registerProvider() {
        Items.registerItemProvider(ExecutableItemsProvider())
    }

    override fun getPluginName(): String {
        return "ExecutableItems"
    }

    private class ExecutableItemsProvider : ItemProvider("executableitems") {
        override fun provideForKey(key: String): TestableItem? {
            val item = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(key).orElse(null) ?: return null
            val namespacedKey = NamespacedKeyUtils.create("executableitems", key)
            return CustomItem(
                namespacedKey,
                Predicate { test: ItemStack ->
                    val customStack = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(test).orElse(null) ?: return@Predicate false
                    customStack.id.equals(key, ignoreCase = true)
                },
                item.buildItem(1, Optional.empty())
            )
        }
    }
}
