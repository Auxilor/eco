package com.willfp.eco.internal.entities

import com.willfp.eco.internal.compat.ModernCompatibilityProxy

@ModernCompatibilityProxy("entities.ModernEntityArgParsersImpl")
interface ModernEntityArgParsers {
    fun registerAll()
}
