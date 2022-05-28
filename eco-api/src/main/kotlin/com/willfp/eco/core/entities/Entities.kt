@file:JvmName("EntityExtensions")

package com.willfp.eco.core.entities

import com.willfp.eco.core.entities.ai.EntityController
import org.bukkit.entity.Mob

/** @see EntityController.getFor */
val <T : Mob> T.controller: EntityController<T>
    get() = EntityController.getFor(this)
