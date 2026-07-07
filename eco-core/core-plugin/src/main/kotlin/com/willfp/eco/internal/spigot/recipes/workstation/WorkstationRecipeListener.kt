package com.willfp.eco.internal.spigot.recipes.workstation

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.recipe.workstation.AnvilRecipe
import com.willfp.eco.core.recipe.workstation.BrewingRecipe
import com.willfp.eco.core.recipe.workstation.CrafterRecipe
import com.willfp.eco.core.recipe.workstation.GrindstoneRecipe
import com.willfp.eco.core.recipe.workstation.SmeltingRecipe
import com.willfp.eco.core.recipe.workstation.SmeltingType
import com.willfp.eco.core.recipe.workstation.SmithingRecipe
import com.willfp.eco.core.recipe.workstation.StonecuttingRecipe
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
import org.bukkit.event.block.CrafterCraftEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.inventory.MerchantInventory
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.inventory.SmithingInventory
import org.bukkit.inventory.StonecutterInventory
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class WorkstationRecipeListener(private val plugin: EcoPlugin) : Listener {

    // ── Furnace smelting ──────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSmelt(event: FurnaceSmeltEvent) {
        val recipe = WorkstationRecipes.getAll(SmeltingRecipe::class.java)
            .firstOrNull { it.smeltingType != SmeltingType.CAMPFIRE && it.input.matches(event.source) }
            ?: return
        event.result = recipe.output?.clone() ?: return
    }

    // ── Campfire cooking ──────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCampfire(event: BlockCookEvent) {
        val recipe = WorkstationRecipes.getAll(SmeltingRecipe::class.java)
            .firstOrNull { it.smeltingType == SmeltingType.CAMPFIRE && it.input.matches(event.source) }
            ?: return
        event.result = recipe.output?.clone() ?: return
    }

    // ── Brewing stand ─────────────────────────────────────────────────────

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

        val ing = ingredientSlot.clone()
        if (ing.amount <= 1) brewer.ingredient = null
        else { ing.amount--; brewer.ingredient = ing }

        val item = recipe.output?.clone() ?: return
        matchedSlots.forEach { brewer.setItem(it, item.clone()) }
    }

    // ── Grindstone ────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareGrindstone(event: PrepareGrindstoneEvent) {
        val player = event.view.player as? Player ?: return
        val inv = event.inventory
        val recipe = WorkstationRecipes.getAll(GrindstoneRecipe::class.java)
            .firstOrNull {
                it.item1.matches(inv.getItem(0)) &&
                (it.item2 == null || it.item2!!.matches(inv.getItem(1)))
            } ?: return
        event.result = recipe.output?.clone()
        WorkstationRecipes.setPendingRecipe(player.uniqueId, recipe)
        plugin.server.scheduler.runTask(plugin, Runnable { player.updateInventory() })
    }

    // ── Anvil ─────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val player = event.view.player as? Player ?: return
        val inv = event.inventory
        val recipe = WorkstationRecipes.getAll(AnvilRecipe::class.java)
            .firstOrNull {
                it.base.matches(inv.getItem(0)) &&
                (it.material == null || it.material!!.matches(inv.getItem(1)))
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

    // ── Smithing table ────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSmithingCraft(event: CraftItemEvent) {
        if (event.view.topInventory !is SmithingInventory) return
        val inv = event.view.topInventory
        WorkstationRecipes.getAll(SmithingRecipe::class.java)
            .firstOrNull {
                it.template.matches(inv.getItem(0)) &&
                it.base.matches(inv.getItem(1)) &&
                it.addition.matches(inv.getItem(2))
            } ?: return
        // Vanilla result already set by Bukkit recipe registration; nothing more to do.
    }

    // ── Stonecutter ───────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onStonecutterCraft(event: CraftItemEvent) {
        if (event.view.topInventory !is StonecutterInventory) return
        val recipeKey = (event.recipe as? org.bukkit.Keyed)?.key ?: return
        WorkstationRecipes.getByKey(recipeKey) as? StonecuttingRecipe ?: return
        // Vanilla result already set by Bukkit recipe registration; nothing more to do.
    }

    // ── Crafter block ─────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCrafterCraft(event: CrafterCraftEvent) {
        val recipeKey = (event.recipe as? org.bukkit.Keyed)?.key ?: return
        val baseKey = if (recipeKey.namespace == "recipebook" && recipeKey.key.endsWith("_crafter"))
            NamespacedKey("recipebook", recipeKey.key.removeSuffix("_crafter"))
        else recipeKey
        WorkstationRecipes.getByKey(baseKey) as? CrafterRecipe ?: return
        // Vanilla delivers item; nothing more to do.
    }

    // ── Villager / merchant ───────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onMerchantOpen(event: InventoryOpenEvent) {
        if (event.inventory.type != InventoryType.MERCHANT) return
        val player = event.player as? Player ?: return
        val merchant = (event.inventory.holder as? org.bukkit.entity.AbstractVillager) ?: return
        val isWanderingTrader = merchant is org.bukkit.entity.WanderingTrader

        val filteredRecipes = WorkstationRecipes.getAll(VillagerRecipe::class.java)
            .filter { vr ->
                if (isWanderingTrader) {
                    if (!vr.isWanderingTrader) return@filter false
                } else {
                    if (vr.isWanderingTrader) return@filter false
                    if (vr.profession != null || vr.minLevel > 0) {
                        val v = merchant as? org.bukkit.entity.Villager ?: return@filter false
                        if (vr.profession != null && v.profession != vr.profession) return@filter false
                        if (vr.minLevel > 0 && v.villagerLevel < vr.minLevel) return@filter false
                    }
                }
                val pdcKey = NamespacedKey("recipebook", "vr_${vr.key.key}")
                val pdc = merchant.persistentDataContainer
                if (pdc.has(pdcKey, PersistentDataType.BYTE)) {
                    pdc.get(pdcKey, PersistentDataType.BYTE) == 1.toByte()
                } else {
                    val include = vr.chance >= 1.0 || Math.random() <= vr.chance
                    pdc.set(pdcKey, PersistentDataType.BYTE, if (include) 1.toByte() else 0.toByte())
                    include
                }
            }

        val tradeNsKey = NamespacedKey("recipebook", "trade_key")
        val existing = merchant.recipes.toMutableList()

        existing.removeIf { mr ->
            val tradeKey = mr.result.itemMeta
                ?.persistentDataContainer
                ?.get(tradeNsKey, PersistentDataType.STRING)
                ?: return@removeIf false
            val recipe = WorkstationRecipes.getByKey(NamespacedKey("recipebook", tradeKey))
                ?: return@removeIf true
            filteredRecipes.none { it.key == recipe.key }
        }

        filteredRecipes.forEach { vr ->
            val alreadyAdded = existing.any { mr ->
                val tag = mr.result.itemMeta?.persistentDataContainer
                    ?.get(tradeNsKey, PersistentDataType.STRING)
                if (tag != null) tag == vr.key.key else mr.result.isSimilar(vr.output)
            }
            if (!alreadyAdded) {
                val resultItem = (vr.output ?: return@forEach).clone()
                resultItem.itemMeta = resultItem.itemMeta?.also { meta ->
                    meta.persistentDataContainer.set(tradeNsKey, PersistentDataType.STRING, vr.key.key)
                }
                val mr = MerchantRecipe(resultItem, Int.MAX_VALUE)
                mr.villagerExperience = vr.villagerXp
                mr.setExperienceReward(vr.villagerXp > 0)
                mr.addIngredient(vr.input1Display ?: vr.input1.item ?: return@forEach)
                vr.input2?.let { inp2 -> mr.addIngredient(vr.input2Display ?: inp2.item ?: return@let) }
                existing.add(mr)
            }
        }
        merchant.recipes = existing
    }

    // ── Result slot click (grindstone / anvil / merchant) ─────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onResultClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inv = event.inventory
        val isResultSlot = when (inv.type) {
            InventoryType.GRINDSTONE,
            InventoryType.ANVIL -> event.rawSlot == 2
            InventoryType.MERCHANT -> event.rawSlot == 2
            else -> return
        }
        if (!isResultSlot) return
        WorkstationRecipes.clearPendingRecipe(player.uniqueId)
    }
}
