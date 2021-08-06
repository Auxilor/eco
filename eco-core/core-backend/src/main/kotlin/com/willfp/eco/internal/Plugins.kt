package com.willfp.eco.internal

import com.willfp.eco.core.EcoPlugin
import lombok.experimental.UtilityClass

@UtilityClass
object Plugins {
    val LOADED_ECO_PLUGINS = HashMap<String, EcoPlugin>()
}