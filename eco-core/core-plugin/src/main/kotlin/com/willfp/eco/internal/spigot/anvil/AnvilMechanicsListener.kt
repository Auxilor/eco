package com.willfp.eco.internal.spigot.anvil

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.anvil.AnvilHandler
import com.willfp.eco.core.anvil.AnvilHandlers
import com.willfp.eco.core.anvil.AnvilSettings
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.proxy.ProxyConstants
import com.willfp.eco.core.recipe.workstation.AnvilRecipe
import com.willfp.eco.core.recipe.workstation.WorkstationRecipes
import com.willfp.eco.internal.spigot.proxies.AnvilRepairProxy
import com.willfp.eco.internal.spigot.proxies.OpenInventoryProxy
import com.willfp.eco.util.StringUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import java.util.UUID
import kotlin.math.abs

/** Outcome of an anvil merge: null [result]/[xp] fields aren't set unless a merge succeeded. */
private data class AnvilResult(val result: ItemStack?, val xp: Int?)

/** Sentinel [AnvilResult] returned by [AnvilMechanicsListener.doMerge] when no valid merge exists. */
private val FAIL = AnvilResult(null, null)

/**
 * Drives the eco anvil shell: replaces vanilla's result-slot preview with one computed via
 * [AnvilHandlers], and blocks stale previews from being taken once a newer merge is pending.
 */
class AnvilMechanicsListener(
    private val plugin: EcoPlugin
) : Listener {
    /**
     * With `use-vanilla-item-name-format` off (default), FastItemStack blanket-forces italics
     * off on every name/lore component, so a legacy `§o` has to be injected here to keep
     * anvil-renamed names italic like vanilla. With it on, names are left as bare components
     * and pick up vanilla's implicit italic automatically, so no injection is needed.
     */
    private fun withAnvilRenameItalics(name: String): String {
        return if (plugin.configYml.getBool("use-vanilla-item-name-format")) {
            name
        } else {
            "§o$name"
        }
    }

    /** Per-player counter bumped on every [onAnvilPrepare], used to invalidate in-flight previews. */
    private val latestPreviewGeneration = mutableMapOf<UUID, Int>()

    /** Per-player generation of the preview actually rendered into the result slot. */
    private val renderedPreviewGeneration = mutableMapOf<UUID, Int>()

    /**
     * The result eco expects to find on the anvil prepare currently being handled: vanilla's own
     * result up until [onAnvilPrepare] clears it, null after. Anything else means another plugin
     * claimed the result. Thread-local because Folia fires anvil events on region threads rather
     * than a single main thread.
     */
    private val expectedResult = ThreadLocal<ItemStack?>()

    /**
     * Snapshots vanilla's result so [onAnvilPrepare] can tell a plain vanilla merge apart from one
     * another plugin has already claimed. Cloned, so a plugin mutating the result in place is
     * detected as well as one calling `setResult`. Runs before every other listener, save any also
     * at [EventPriority.LOWEST] that Bukkit happened to register first.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onAnvilPrepareLowest(event: PrepareAnvilEvent) {
        expectedResult.set(event.result?.clone())
    }

    private val anvilGuiClass: Class<*>? = try {
        Class.forName(
            "net.wesjd.anvilgui.version.Wrapper" +
                    ProxyConstants.NMS_VERSION.substring(1) +
                    "\$AnvilContainer"
        )
    } catch (_: ClassNotFoundException) {
        null
    }

    private fun openMenuClass(player: Player): Class<*> =
        plugin.getProxy(OpenInventoryProxy::class.java).getOpenInventory(player)::class.java

    /** Prevents taking a stale preview result before its async computation has finished rendering. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onAnvilResultClick(event: InventoryClickEvent) {
        if (AnvilHandlers.handler() == null) return
        val player = event.whoClicked as? Player ?: return
        val inventory = event.view.topInventory as? AnvilInventory ?: return
        if (event.rawSlot != 2) return
        if (openMenuClass(player) == anvilGuiClass) return

        val latestGeneration = latestPreviewGeneration[player.uniqueId] ?: return
        val renderedGeneration = renderedPreviewGeneration[player.uniqueId]
        if (latestGeneration == renderedGeneration) return

        event.isCancelled = true
        event.currentItem = null
        inventory.setItem(2, null)
    }

    /** Clears preview-tracking state for the player when they close the anvil. */
    @EventHandler
    fun onAnvilClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        latestPreviewGeneration.remove(player.uniqueId)
        renderedPreviewGeneration.remove(player.uniqueId)
    }

    /** Clears preview-tracking state for the player when they disconnect. */
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        latestPreviewGeneration.remove(event.player.uniqueId)
        renderedPreviewGeneration.remove(event.player.uniqueId)
    }

    /**
     * Suppresses vanilla's anvil result, then asynchronously computes the eco merge result
     * via [doMerge] and renders it into the result slot if it's still the latest preview
     * requested for the player (guards against overlapping edits producing stale previews).
     */
    @Suppress("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAnvilPrepare(event: PrepareAnvilEvent) {
        val expected = expectedResult.get()

        val handler = AnvilHandlers.handler() ?: return
        val settings = AnvilHandlers.settings() ?: return

        val leftItem = event.inventory.getItem(0)
        val rightItem = event.inventory.getItem(1)
        val viewer = event.viewers.getOrNull(0) as? Player

        // A matching custom AnvilRecipe (WorkstationRecipeListener, lower priority) has already
        // set event.result. Defer to it entirely, clearing any stale preview-generation state
        // from prior vanilla-merge use, so the enchant-merge shell doesn't clobber it and the
        // result-click guard stays inert.
        val hasCustomRecipe = WorkstationRecipes.getAll(AnvilRecipe::class.java).any {
            it.base.matches(leftItem) && (it.material == null || it.material!!.matches(rightItem))
        }

        // Same deal for a third-party plugin that already set its own result: it no longer matches
        // what vanilla computed, so it belongs to that plugin. Deferring is the only way it can win
        // - the render below happens in a scheduled task that runs after the event has finished, so
        // it would otherwise overwrite the plugin's result whatever priority it listened at.
        val hasForeignResult = event.result != expected

        if (hasCustomRecipe || hasForeignResult) {
            viewer?.uniqueId?.let {
                latestPreviewGeneration.remove(it)
                renderedPreviewGeneration.remove(it)
            }
            return
        }

        val player = viewer ?: return
        val generation = (latestPreviewGeneration[player.uniqueId] ?: 0) + 1
        latestPreviewGeneration[player.uniqueId] = generation
        renderedPreviewGeneration.remove(player.uniqueId)

        if (handler.isBlocked(leftItem, rightItem)) {
            event.result = null
            event.inventory.setItem(2, null)
            return
        }

        if (openMenuClass(player) == anvilGuiClass) return

        // Only the prior-work penalties the inputs carry. Vanilla's own view cost also prices the
        // merge (enchant levels, repair units), which doMerge computes again, so using it as the
        // base charged twice for the same work.
        val priorWorkCost = (leftItem?.fast()?.repairCost ?: 0) + (rightItem?.fast()?.repairCost ?: 0)
        event.result = null
        event.inventory.setItem(2, null)

        expectedResult.set(null)

        plugin.scheduler.on(player).run {
            if (latestPreviewGeneration[player.uniqueId] != generation) return@run

            // The result was cleared above, so anything in it now was put there by another plugin
            // listening after us - at HIGHEST but registered later, or at MONITOR. Those run once
            // the event has already passed us, so this is the only place they can be noticed.
            // Drop the preview state as well, so the result-click guard doesn't cancel taking it.
            if (event.result != null) {
                latestPreviewGeneration.remove(player.uniqueId)
                renderedPreviewGeneration.remove(player.uniqueId)
                return@run
            }

            val left = event.inventory.getItem(0)?.clone()
            val old = left?.clone()
            val right = event.inventory.getItem(1)?.clone()

            val result = doMerge(
                left,
                right,
                @Suppress("REMOVAL", "DEPRECATION")
                event.inventory.renameText ?: "",
                player,
                handler,
                settings
            )
            if (result == FAIL) return@run

            event.result = null
            event.inventory.setItem(2, null)

            val price = result.xp ?: 0
            val outItem = result.result ?: ItemStack(Material.AIR)
            val oldLeft = event.inventory.getItem(0)
            if (oldLeft == null || oldLeft.type != outItem.type) return@run
            if (left == old) return@run

            val renameCost = if (old?.fast()?.displayName != outItem.fast().displayName) 1 else 0
            val cost = computeAnvilCost(priorWorkCost, renameCost, price)
            if (cost <= 0) return@run

            val leftEnchants = left?.fast()?.getEnchants(true) ?: emptyMap()
            val outEnchants = outItem.fast().getEnchants(true)
            if (event.inventory.getItem(1) == null && leftEnchants != outEnchants) return@run

            if (settings.useReworkPenalty) {
                outItem.fast().repairCost = applyReworkPenalty(outItem.fast().repairCost)
            }

            val clampRepairCost = settings.clampRepairCost
            val maxRepairCost = settings.maxRepairCost
            if (latestPreviewGeneration[player.uniqueId] != generation) return@run

            event.view.maximumRepairCost = maxRepairCost
            event.view.repairCost = if (clampRepairCost) cost.coerceAtMost(maxRepairCost) else cost

            if (!clampRepairCost && maxRepairCost > 0 && cost >= maxRepairCost) return@run
            if (latestPreviewGeneration[player.uniqueId] != generation) return@run

            event.result = outItem
            event.inventory.setItem(2, outItem)
            renderedPreviewGeneration[player.uniqueId] = generation
        }
    }

    /**
     * Computes the eco anvil merge of [left] (result item) and [right] (sacrifice/material)
     * per [handler]/[settings], applying rename, unit-repair, enchant-merge and durability-merge
     * rules. Returns [FAIL] if the inputs can't be merged (e.g. empty left, incompatible right).
     */
    private fun doMerge(
        left: ItemStack?,
        right: ItemStack?,
        itemName: String,
        player: Player,
        handler: AnvilHandler,
        settings: AnvilSettings
    ): AnvilResult {
        if (left == null || left.type == Material.AIR) return FAIL

        val formattedItemName = if (settings.colorNameAllowed(player)) {
            StringUtils.format(itemName)
        } else {
            @Suppress("DEPRECATION")
            ChatColor.stripColor(itemName)
        }.let { if (it.isNullOrEmpty()) left.fast().displayName else it }
            .let { withAnvilRenameItalics(it) }

        if (right == null || right.type == Material.AIR) {
            if (left.fast().displayName == formattedItemName) return FAIL
            left.fast().displayName = formattedItemName
            return AnvilResult(left, 0)
        }

        val leftMeta = left.itemMeta
        val rightMeta = right.itemMeta
        var unitRepairCost = 0

        if (left.type != right.type) {
            val unitRepair = if (leftMeta is Damageable) {
                plugin.getProxy(AnvilRepairProxy::class.java).unitRepair(left, right)
            } else {
                null
            }

            if (unitRepair != null && leftMeta is Damageable) {
                unitRepairCost = unitRepair.units
                leftMeta.damage = unitRepair.damage
                right.amount -= unitRepair.units
            } else {
                if (right.type != Material.ENCHANTED_BOOK) return FAIL
            }
        }

        left.fast().displayName = formattedItemName

        val leftEnchants = left.fast().getEnchants(true)
        val rightEnchants = right.fast().getEnchants(true)
        val outEnchants = leftEnchants.toMutableMap()

        for ((enchant, level) in rightEnchants) {
            if (outEnchants.containsKey(enchant)) {
                val currentLevel = outEnchants[enchant]!!
                outEnchants[enchant] = mergeEnchantLevel(currentLevel, level, handler.maxLevel(enchant))
            } else {
                if (handler.canCombine(enchant, level, left, outEnchants.keys)) {
                    if (outEnchants.size < settings.enchantLimit.infiniteIfNegative()) {
                        outEnchants[enchant] = level
                    }
                }
            }
        }

        if (leftMeta is Damageable && rightMeta is Damageable && unitRepairCost == 0 && rightMeta !is EnchantmentStorageMeta) {
            val damage = plugin.getProxy(AnvilRepairProxy::class.java).combineRepair(left, right)
            if (damage != null) {
                leftMeta.damage = damage
            }
        }

        if (leftMeta is EnchantmentStorageMeta) {
            for (storedEnchant in leftMeta.storedEnchants.keys.toSet()) {
                leftMeta.removeStoredEnchant(storedEnchant)
            }
            for ((enchant, level) in outEnchants) leftMeta.addStoredEnchant(enchant, level, true)
        } else {
            for (storedEnchant in leftMeta.enchants.keys.toSet()) {
                leftMeta.removeEnchant(storedEnchant)
            }
            for ((enchant, level) in outEnchants) leftMeta.addEnchant(enchant, level, true)
        }

        left.itemMeta = leftMeta

        val enchantLevelDiff = abs(leftEnchants.values.sum() - outEnchants.values.sum())
        val xpCost = computeXpCost(enchantLevelDiff, unitRepairCost, settings.costExponent)
        return AnvilResult(left, xpCost)
    }
}
