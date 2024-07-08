package com.willfp.eco.internal.items

import com.willfp.eco.internal.compat.ModernCompatibilityProxy

@ModernCompatibilityProxy("items.ModernItemArgParsersImpl")
interface ModernItemArgParsers {
    fun registerAll()
}
