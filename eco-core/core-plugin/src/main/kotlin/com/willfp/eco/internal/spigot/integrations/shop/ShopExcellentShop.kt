package com.willfp.eco.internal.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopIntegration
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.impl.PriceEconomy
import com.willfp.eco.core.price.impl.PriceFree
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import su.nightexpress.excellentshop.ShopPlugin
import su.nightexpress.excellentshop.api.event.TransactionValidatedEvent
import su.nightexpress.excellentshop.api.product.Product
import su.nightexpress.excellentshop.api.product.TradeType
import su.nightexpress.excellentshop.util.ShopUtils
import su.nightexpress.excellentshop.virtualshop.VirtualShopModule
import su.nightexpress.excellentshop.virtualshop.shop.VirtualShop
import su.nightexpress.nightcore.integration.currency.CurrencyId

class ShopExcellentShop : ShopIntegration {
    override fun getSellEventAdapter(): Listener {
        return ExcellentShopSellEventListeners
    }

    override fun getUnitValue(itemStack: ItemStack, player: Player): Price {
        val product = findSellProduct(
            itemStack.clone().apply {
                amount = 1
            },
            player
        ) ?: return PriceFree()

        if (!product.isVaultPriced) {
            return PriceFree()
        }

        return PriceEconomy(product.getFinalSellPrice(player))
    }

    override fun isSellable(itemStack: ItemStack, player: Player): Boolean {
        val product = findSellProduct(itemStack, player) ?: return false

        return product.isSellable && product.getFinalSellPrice(player) > 0
    }

    object ExcellentShopSellEventListeners : Listener {
        @EventHandler
        fun shopEventToEcoEvent(event: TransactionValidatedEvent) {
            val transaction = event.transaction

            if (transaction.type != TradeType.SELL) {
                return
            }

            // Previews are simulated transactions used to render menu values, so they
            // must not fire sell events.
            if (transaction.isPreview) {
                return
            }

            val player = transaction.player

            for (item in transaction.itemsList) {
                val product = item.product()

                if (product.shop !is VirtualShop) {
                    continue
                }

                if (!product.isVaultPriced) {
                    continue
                }

                val currency = product.currency

                val ecoEvent = ShopSellEvent(
                    player,
                    PriceEconomy(item.price().query(currency)),
                    product.preview.clone()
                )

                Bukkit.getPluginManager().callEvent(ecoEvent)

                item.price().set(currency, ecoEvent.value.getValue(player) * ecoEvent.multiplier)
            }
        }
    }

    override fun getPluginName(): String {
        return "ExcellentShop"
    }

    private companion object {
        /**
         * VirtualShopModule#getBestProductFor searches every shop regardless of access,
         * so the lookup goes through [ShopUtils] directly in order to preserve the
         * per-player shop filtering that the ExcellentShop 4.x API used to apply.
         */
        fun findSellProduct(itemStack: ItemStack, player: Player): Product? {
            val virtualShop = JavaPlugin.getPlugin(ShopPlugin::class.java)
                .moduleRegistry
                .byType(VirtualShopModule::class.java)
                .orElse(null) ?: return null

            return ShopUtils.findBestProduct(itemStack, TradeType.SELL, virtualShop.getShops(player))
        }

        /**
         * Products are priced in a single currency, and eco prices map onto Vault only.
         */
        val Product.isVaultPriced: Boolean
            get() = currency.internalId == CurrencyId.VAULT
    }
}
