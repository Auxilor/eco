package com.willfp.eco.internal.particle.config

import org.bukkit.NamespacedKey

/** Thrown when a preset reference cycle is detected. */
class CyclicPresetException(val cycle: List<NamespacedKey>) :
    RuntimeException("Cyclic preset reference: ${cycle.joinToString(" -> ")}")