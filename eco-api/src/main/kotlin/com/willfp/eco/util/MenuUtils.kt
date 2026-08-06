@file:JvmName("MenuUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.entity.Player

/**
 * The eco [Menu] this player currently has open, or null if the player has no eco menu open.
 *
 * Inventories that are not eco menus are not reported here.
 *
 * @see MenuUtils.getOpenMenu
 */
val Player.openMenu: Menu?
    get() = MenuUtils.getOpenMenu(this)
