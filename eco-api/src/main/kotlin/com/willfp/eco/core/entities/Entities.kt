@file:JvmName("EntityExtensions")

package com.willfp.eco.core.entities

import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack

/** @see EntityController.getFor */
val <T : Mob> T.controller: EntityController<T>
    get() = EntityController.getFor(this)
