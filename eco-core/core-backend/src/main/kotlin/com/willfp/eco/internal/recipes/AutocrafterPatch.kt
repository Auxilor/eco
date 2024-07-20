package com.willfp.eco.internal.recipes

import com.willfp.eco.internal.compat.ModernCompatibilityProxy
import org.bukkit.event.Listener

@ModernCompatibilityProxy("recipes.AutocrafterPatchImpl")
interface AutocrafterPatch: Listener
