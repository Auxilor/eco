package proxy

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.proxy.AbstractProxy
import org.bukkit.inventory.ItemStack

interface FastItemStackFactoryProxy : AbstractProxy {
    fun create(itemStack: ItemStack): FastItemStack
}