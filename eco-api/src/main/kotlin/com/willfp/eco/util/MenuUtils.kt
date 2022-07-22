@file:JvmName("MenuUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.entity.Player

/** @see MenuUtils.getOpenMenu */
val Player.openMenu: Menu?
    get() = MenuUtils.getOpenMenu(this)
