package com.willfp.eco.internal.spigot.recipes.workstation

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.recipe.workstation.AnvilRecipe
import com.willfp.eco.core.recipe.workstation.BrewingRecipe
import com.willfp.eco.core.recipe.workstation.GrindstoneRecipe
import com.willfp.eco.core.recipe.workstation.SmeltingRecipe
import com.willfp.eco.core.recipe.workstation.SmeltingType
import com.willfp.eco.core.recipe.workstation.VillagerRecipe
import com.willfp.eco.core.recipe.workstation.WorkstationRecipe
import com.willfp.eco.core.recipe.workstation.WorkstationRecipes
import com.willfp.eco.util.formatEco
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockCookEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.inventory.MerchantInventory
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class WorkstationRecipeListener(private val plugin: EcoPlugin) : Listener {

    // Furnace / smoker / blast furnace / campfire cooking
    //
    // FurnaceSmeltEvent extends BlockCookEvent but doesn't override getHandlers(), so it
    // shares BlockCookEvent's HandlerList - registering separate handlers for each type
    // means both fire for every cook event. Merged into one to avoid that trap.

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCook(event: BlockCookEvent) {
        val isCampfire = event !is FurnaceSmeltEvent
        val recipe = WorkstationRecipes.getAll(SmeltingRecipe::class.java)
            .firstOrNull { (it.smeltingType == SmeltingType.CAMPFIRE) == isCampfire && it.input.matches(event.source) }
            ?: return
        event.result = recipe.output?.clone() ?: return
    }

    // Brewing stand

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBrew(event: BrewEvent) {
        val brewer = event.contents
        val ingredientSlot = brewer.ingredient ?: return

        val recipe = WorkstationRecipes.getAll(BrewingRecipe::class.java)
            .firstOrNull { r ->
                r.ingredient.matches(ingredientSlot) &&
                (0..2).any { slot -> r.base.matches(brewer.getItem(slot)) }
            } ?: return

        val matchedSlots = (0..2).filter { recipe.base.matches(brewer.getItem(it)) }
        if (matchedSlots.isEmpty()) return

        event.isCancelled = true

        val ingredient = ingredientSlot.clone()
        if (ingredient.amount <= 1) brewer.ingredient = null
        else { ingredient.amount--; brewer.ingredient = ingredient }

        val item = recipe.output?.clone() ?: return
        matchedSlots.forEach { brewer.setItem(it, item.clone()) }
    }

    // Grindstone

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareGrindstone(event: PrepareGrindstoneEvent) {
        val player = event.view.player as? Player ?: return
        val inventory = event.inventory
        val recipe = WorkstationRecipes.getAll(GrindstoneRecipe::class.java)
            .firstOrNull {
                it.item1.matches(inventory.getItem(0)) &&
                (it.item2 == null || it.item2!!.matches(inventory.getItem(1)))
            } ?: return
        event.result = recipe.output?.clone()
        WorkstationRecipes.setPendingRecipe(player.uniqueId, recipe)
        plugin.server.scheduler.runTask(plugin, Runnable { player.updateInventory() })
    }

    // Anvil

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val player = event.view.player as? Player ?: return
        val inventory = event.inventory
        val recipe = WorkstationRecipes.getAll(AnvilRecipe::class.java)
            .firstOrNull {
                it.base.matches(inventory.getItem(0)) &&
                (it.material == null || it.material!!.matches(inventory.getItem(1)))
            } ?: return

        val result = recipe.output?.clone() ?: return
        recipe.resultName?.let { name ->
            val meta = result.itemMeta
            meta?.setDisplayName(name.formatEco())
            result.itemMeta = meta
        }
        event.result = result
        event.inventory.repairCost = recipe.repairCost
        WorkstationRecipes.setPendingRecipe(player.uniqueId, recipe)
    }

    // Villager / merchant

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onMerchantOpen(event: InventoryOpenEvent) {
        if (event.inventory.type != InventoryType.MERCHANT) return
        val player = event.player as? Player ?: return
        val merchant = (event.inventory.holder as? org.bukkit.entity.AbstractVillager) ?: return
        val isWanderingTrader = merchant is org.bukkit.entity.WanderingTrader

        val filteredRecipes = WorkstationRecipes.getAll(VillagerRecipe::class.java)
            .filter { villagerRecipe ->
                if (isWanderingTrader) {
                    if (!villagerRecipe.isWanderingTrader) return@filter false
                } else {
                    if (villagerRecipe.isWanderingTrader) return@filter false
                    if (villagerRecipe.profession != null || villagerRecipe.minLevel > 0) {
                        val villager = merchant as? org.bukkit.entity.Villager ?: return@filter false
                        if (villagerRecipe.profession != null && villager.profession != villagerRecipe.profession) return@filter false
                        if (villagerRecipe.minLevel > 0 && villager.villagerLevel < villagerRecipe.minLevel) return@filter false
                    }
                }
                val pdcKey = NamespacedKey("recipebook", "vr_${villagerRecipe.key.key}")
                val pdc = merchant.persistentDataContainer
                if (pdc.has(pdcKey, PersistentDataType.BYTE)) {
                    pdc.get(pdcKey, PersistentDataType.BYTE) == 1.toByte()
                } else {
                    val include = villagerRecipe.chance >= 1.0 || Math.random() <= villagerRecipe.chance
                    pdc.set(pdcKey, PersistentDataType.BYTE, if (include) 1.toByte() else 0.toByte())
                    include
                }
            }

        fun VillagerRecipe.matchesExisting(merchantRecipe: MerchantRecipe): Boolean {
            val ingredients = merchantRecipe.ingredients
            if (ingredients.isEmpty() || !input1.matches(ingredients[0])) return false
            val secondInput = input2
            return if (secondInput != null) ingredients.size > 1 && secondInput.matches(ingredients[1])
                   else ingredients.size <= 1
        }

        val existing = merchant.recipes.toMutableList()

        existing.removeIf { merchantRecipe ->
            val matched = WorkstationRecipes.getAll(VillagerRecipe::class.java)
                .firstOrNull { it.matchesExisting(merchantRecipe) } ?: return@removeIf false
            filteredRecipes.none { it.key == matched.key }
        }

        filteredRecipes.forEach { villagerRecipe ->
            val resultItem = (villagerRecipe.output ?: return@forEach).clone()
            val matchIndex = existing.indexOfFirst { merchantRecipe -> villagerRecipe.matchesExisting(merchantRecipe) }

            if (matchIndex >= 0) {
                // Villagers persist their trades (and the result ItemStack's full NBT) to disk,
                // so a trade added by an older plugin version - e.g. one that used to stamp a
                // PDC tag onto the result - keeps that stale item forever unless we rebuild the
                // entry here. MerchantRecipe has no setResult, so preserve the trade's economy
                // state and swap in a fresh result item whenever it no longer matches.
                val old = existing[matchIndex]
                if (!old.result.isSimilar(resultItem)) {
                    val refreshed = MerchantRecipe(
                        resultItem, old.uses, old.maxUses, old.hasExperienceReward(),
                        old.villagerExperience, old.priceMultiplier, old.demand, old.specialPrice
                    )
                    refreshed.ingredients = old.ingredients
                    existing[matchIndex] = refreshed
                }
                return@forEach
            }

            val merchantRecipe = MerchantRecipe(resultItem, Int.MAX_VALUE)
            merchantRecipe.villagerExperience = villagerRecipe.villagerXp
            merchantRecipe.setExperienceReward(villagerRecipe.villagerXp > 0)
            merchantRecipe.addIngredient((villagerRecipe.input1Display ?: villagerRecipe.input1.item ?: return@forEach).clone())
            villagerRecipe.input2?.let { secondInput ->
                merchantRecipe.addIngredient((villagerRecipe.input2Display ?: secondInput.item ?: return@let).clone())
            }
            existing.add(merchantRecipe)
        }
        merchant.recipes = existing
    }

    // Result slot click (grindstone / anvil / merchant)

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onResultClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inventory = event.inventory
        val isResultSlot = when (inventory.type) {
            InventoryType.GRINDSTONE,
            InventoryType.ANVIL -> event.rawSlot == 2
            InventoryType.MERCHANT -> event.rawSlot == 2
            else -> return
        }
        if (!isResultSlot) return
        WorkstationRecipes.clearPendingRecipe(player.uniqueId)
    }
}
