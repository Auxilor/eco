package com.willfp.eco.proxy

import com.willfp.eco.core.proxy.AbstractProxy

interface TPSProxy : AbstractProxy {
    fun getTPS(): Double
}