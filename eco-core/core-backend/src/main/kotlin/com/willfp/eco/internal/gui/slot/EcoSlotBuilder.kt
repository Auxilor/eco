package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.core.gui.slot.functional.SlotUpdater
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.function.BiPredicate
import java.util.function.Predicate

class EcoSlotBuilder(private val provider: SlotProvider) : SlotBuilder {
    private var captive = false
    private var captiveFromEmpty = false
    private var updater: SlotUpdater = SlotUpdater { player, menu, _ -> provider.provide(player, menu) }

    private val handlers = mutableMapOf<ClickType, MutableList<SlotHandler>>()

    private var notCaptiveFor: (Player) -> Boolean = { false }
    private var notCaptiveForItem: (Player, ItemStack?) -> Boolean = { _, _ -> false}

    override fun onClick(type: ClickType, action: SlotHandler): SlotBuilder {
        handlers.computeIfAbsent(type) { mutableListOf() } += action
        return this
    }

    override fun notCaptiveFor(predicate: Predicate<Player>): SlotBuilder {
        notCaptiveFor = { predicate.test(it) }
        return this
    }

    /**
     * Prevent captive for players that match a predicate.
     *
     * @param predicate The predicate. Returns true when the slot should not be captive.
     * @return The builder.
     */
    override fun notCaptiveForItem(predicate: BiPredicate<Player, ItemStack?>): SlotBuilder {
        notCaptiveForItem = { player, item -> predicate.test(player, item) }
        return this
    }

    override fun setCaptive(fromEmpty: Boolean): SlotBuilder {
        captive = true
        captiveFromEmpty = fromEmpty
        return this
    }

    override fun setUpdater(updater: SlotUpdater): SlotBuilder {
        this.updater = updater
        return this
    }

    override fun build(): Slot {
        return if (captive) {
            EcoCaptiveSlot(
                provider,
                captiveFromEmpty,
                notCaptiveFor,
                notCaptiveForItem
            )
        } else {
            EcoSlot(
                provider,
                handlers,
                updater
            )
        }
    }
}
