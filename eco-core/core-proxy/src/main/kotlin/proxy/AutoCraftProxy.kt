package proxy

import com.willfp.eco.core.proxy.AbstractProxy

interface AutoCraftProxy : AbstractProxy {
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun modifyPacket(packet: Any)
}