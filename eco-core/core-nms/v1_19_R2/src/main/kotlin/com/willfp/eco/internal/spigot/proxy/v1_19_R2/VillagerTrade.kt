package com.willfp.eco.internal.spigot.proxy.v1_19_R2

import com.willfp.eco.core.display.Display
import com.willfp.eco.internal.spigot.proxy.VillagerTradeProxy
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.trading.MerchantOffer
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftMerchantRecipe
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe
import java.lang.reflect.Field

class VillagerTrade : VillagerTradeProxy {
    private val handle: Field = CraftMerchantRecipe::class.java.getDeclaredField("handle")

    override fun displayTrade(
        recipe: MerchantRecipe,
        player: Player
    ): MerchantRecipe {
        recipe as CraftMerchantRecipe

        val nbt = getHandle(recipe).createTag()
        for (tag in arrayOf("buy", "buyB", "sell")) {
            val nms = ItemStack.of(nbt.getCompound(tag))
            val displayed = Display.display(CraftItemStack.asBukkitCopy(nms), player)
            val itemNBT = CraftItemStack.asNMSCopy(displayed).save(CompoundTag())
            nbt.put(tag, itemNBT)
        }

        return CraftMerchantRecipe(MerchantOffer(nbt))
    }

    private fun getHandle(recipe: CraftMerchantRecipe): MerchantOffer {
        return handle[recipe] as MerchantOffer
    }

    init {
        handle.isAccessible = true
    }
}