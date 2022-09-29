package com.willfp.eco.internal.spigot.integrations.customitems

/*
class CustomItemsMythicMobs(
    private val plugin: EcoPlugin
) : CustomItemsIntegration, Listener {
    init {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "MythicMobs"
    }

    @EventHandler
    fun onLoad(event: MythicDropLoadEvent) {
        val name = event.dropName
        if (name.equals("eco", ignoreCase = true)) {
            event.register(
                MythicMobsDrop(plugin, event.config)
            )
        }
    }

    private class MythicMobsDrop(
        private val plugin: EcoPlugin,
        itemConfig: MythicLineConfig
    ) : IItemDrop {
        private val id = itemConfig.getString(arrayOf("type", "t", "item", "i"), "eco")

        override fun getDrop(data: DropMetadata, v: Double): AbstractItemStack {
            val item = Items.lookup(id)
            if (item is EmptyTestableItem) {
                plugin.logger.warning("Item with ID $id is invalid, check your configs!")
                return BukkitItemStack(ItemStack(Material.AIR))
            }
            return BukkitItemStack(item.item.apply { amount = v.toInt() })
        }
    }
}
 */