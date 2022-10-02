package com.willfp.eco.internal.gui

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.entity.Player

class MergedStateMenu(
    private val base: Menu,
    private val additional: Menu
) : Menu by base {
    override fun getState(player: Player): Map<String, Any> {
        return base.getState(player) + additional.getState(player)
    }

    override fun addState(player: Player, key: String, value: Any?) {
        base.addState(player, key, value)
    }

    override fun clearState(player: Player) {
        base.clearState(player)
        additional.clearState(player)
    }

    override fun removeState(player: Player, key: String) {
        base.removeState(player, key)
        additional.removeState(player, key)
    }
}
