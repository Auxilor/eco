package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.TPSProxy

class TPS : TPSProxy {
    override fun getTPS(): Double {
        return 20.0
    }
}
