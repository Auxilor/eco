package com.willfp.eco.core.dragdrop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private class StubHandler(override val id: String) : DragAndDropHandler {
    override fun matches(cursor: ItemStack, current: ItemStack) = true
    override fun apply(player: Player, cursor: ItemStack, current: ItemStack) = DragAndDropResult.APPLIED
}

internal class DragAndDropHandlersTests {
    @BeforeEach
    fun clear() {
        DragAndDropHandlers.all().map { it.first.id }.forEach { DragAndDropHandlers.unregister(it) }
    }

    @Test
    fun registerAddsHandler() {
        DragAndDropHandlers.register(StubHandler("test:one"))
        assertEquals(1, DragAndDropHandlers.all().size, "one handler registered")
        assertEquals("test:one", DragAndDropHandlers.all()[0].first.id)
    }

    @Test
    fun registerSameIdReplacesInPlace() {
        DragAndDropHandlers.register(StubHandler("test:one"))
        DragAndDropHandlers.register(StubHandler("test:two"))
        DragAndDropHandlers.register(StubHandler("test:one"))

        val ids = DragAndDropHandlers.all().map { it.first.id }
        assertEquals(listOf("test:one", "test:two"), ids, "re-registering test:one keeps its original slot, doesn't duplicate or reorder")
    }

    @Test
    fun unregisterRemovesHandler() {
        DragAndDropHandlers.register(StubHandler("test:one"))
        DragAndDropHandlers.unregister("test:one")
        assertTrue(DragAndDropHandlers.all().isEmpty(), "unregistered handler is gone")
    }

    @Test
    fun unregisterAllRemovesByPluginPrefix() {
        DragAndDropHandlers.register(StubHandler("ecoarmor:crystal"))
        DragAndDropHandlers.register(StubHandler("ecoarmor:shard"))
        DragAndDropHandlers.register(StubHandler("ecoscrolls:inscribe"))

        DragAndDropHandlers.unregisterAll("ecoarmor")

        val ids = DragAndDropHandlers.all().map { it.first.id }
        assertEquals(listOf("ecoscrolls:inscribe"), ids, "only ecoarmor: prefixed handlers removed")
    }

    @Test
    fun settingsDefaultToNoCraftingTableException() {
        DragAndDropHandlers.register(StubHandler("test:one"))
        assertEquals(false, DragAndDropHandlers.all()[0].second.allowCraftingTableNonResultSlot)
    }
}
