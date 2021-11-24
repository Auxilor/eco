package com.willfp.eco.proxy

import com.willfp.eco.core.proxy.AbstractProxy

interface AutoCraftProxy : AbstractProxy {
    fun modifyPacket(packet: Any)
}