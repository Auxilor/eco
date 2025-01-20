package com.willfp.eco.internal.spigot.proxy.common.modern

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

/**
 * Cross-version compat method for accessing registries.
 */
interface RegistryAccessor {
    /**
     * Get registry by [key] or throw.
     */
    fun <T> getRegistry(key: ResourceKey<Registry<T>>): Registry<T>
}
