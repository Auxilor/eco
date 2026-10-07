package com.willfp.eco.internal.spigot.actionbar

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.actionbar.ActionBarProvider
import com.willfp.eco.core.actionbar.PersistentActionBar
import org.bukkit.NamespacedKey

class EcoPersistentActionBar(
    private val key: NamespacedKey,
    private val plugin: EcoPlugin,
    private val priority: Int,
    val provider: ActionBarProvider,
    val registrationOrder: Long,
    private val service: PersistentActionBarService
) : PersistentActionBar {
    override fun getKey() = key

    override fun getPlugin() = plugin

    override fun getPriority() = priority

    override fun unregister() = service.unregister(this)
}
