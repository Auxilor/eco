
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.proxy.FastItemStackFactoryProxy
import com.willfp.eco.proxy.v1_16_R3.NMSFastItemStack
import org.bukkit.inventory.ItemStack

class FastItemStackFactory : FastItemStackFactoryProxy {
    override fun create(itemStack: ItemStack): FastItemStack {
        return NMSFastItemStack(itemStack)
    }
}