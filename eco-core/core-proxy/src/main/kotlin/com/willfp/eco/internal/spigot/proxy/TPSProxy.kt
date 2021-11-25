package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.proxy.AbstractProxy

interface TPSProxy : AbstractProxy {
    fun getTPS(): Double
}