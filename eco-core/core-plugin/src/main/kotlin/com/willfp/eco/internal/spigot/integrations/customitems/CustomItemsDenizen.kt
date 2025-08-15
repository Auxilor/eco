package com.willfp.eco.internal.spigot.integrations.customitems

import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizen.scripts.containers.core.ItemScriptHelper
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.event.Listener

class CustomItemsDenizen : CustomItemsIntegration, Listener {

    override fun registerProvider() {
        Items.registerItemProvider(DenizenProvider())
    }

    override fun getPluginName(): String {
        return "Denizen"
    }

    private class DenizenProvider : ItemProvider("denizen") {
        override fun provideForKey(key: String): TestableItem? {
            val item = ItemTag.valueOf(key, false) ?: return null
            val id = item.scriptName
            val namespacedKey = NamespacedKeyUtils.create("denizen", id)
            val stack = item.itemStack
            return CustomItem(
                namespacedKey,
                { id.equals(ItemScriptHelper.getItemScriptNameText(it), ignoreCase = true) },
                stack
            )
        }
    }
}
