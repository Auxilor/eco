package com.willfp.eco.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.util.NamespacedKeyUtils
import me.arcaniax.hdb.api.HeadDatabaseAPI
import me.arcaniax.hdb.enums.CategoryEnum
import java.util.function.Predicate

class CustomItemsHeadDatabase : CustomItemsWrapper {
    private val api = HeadDatabaseAPI()

    override fun registerAllItems() {
        for (categoryEnum in CategoryEnum.values()) {
            for (head in api.getHeads(categoryEnum).toList()) {
                val stack = head.head
                val id = head.id
                val key = NamespacedKeyUtils.create("headdb", id.lowercase());
                CustomItem(
                    key,
                    Predicate { test ->
                        val headId = api.getItemID(test) ?: return@Predicate false
                        headId.equals(id, ignoreCase = true)
                    },
                    stack
                ).register()
            }
        }
    }

    override fun getPluginName(): String {
        return "HeadDatabase"
    }
}