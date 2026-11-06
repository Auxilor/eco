package com.willfp.eco.core.display

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.fast.FastItemStack
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DisplayFlowTests {
    private class FakeRecorder : DisplayRecorder {
        var lore: List<Component> = emptyList()
        var writes = 0
        var restores = 0
        var recorded: IntArray? = null
        var reads = 0
        var mayHaveDisplayLines = false

        override fun mirror(itemStack: ItemStack): ItemStack = itemStack

        override fun getLoreState(itemStack: ItemStack): Any = lore

        @Suppress("UNCHECKED_CAST")
        override fun getLore(loreState: Any?): List<Component> {
            reads++
            return loreState as List<Component>
        }

        override fun mayHaveDisplayLines(itemStack: ItemStack): Boolean = mayHaveDisplayLines

        override fun setLore(itemStack: ItemStack, lore: List<Component>) {
            writes++
            this.lore = ArrayList(lore)
        }

        override fun record(itemStack: ItemStack, snapshot: ItemStack, displayLines: IntArray) {
            recorded = displayLines
        }

        override fun restore(itemStack: ItemStack): Boolean {
            restores++
            return false
        }
    }

    private val plugin = mockk<EcoPlugin>(relaxed = true)
    private val recorder = FakeRecorder()
    private val modules = mutableListOf<DisplayModule>()
    private val foreign = Component.text("Foreign")
    private var fastReads = 0
    private var fastWrites: List<List<Component>> = emptyList()

    @BeforeEach
    fun mockEco() {
        val fastItemStack = mockk<FastItemStack>(relaxed = true)
        every { fastItemStack.loreComponents } answers {
            fastReads++
            recorder.lore
        }
        every { fastItemStack.setLoreComponents(any()) } answers {
            fastWrites = fastWrites + listOf(firstArg<List<Component>>())
            recorder.lore = firstArg()
        }

        val eco = mockk<Eco>(relaxed = true)
        every { eco.displayRecorder } returns recorder
        every { eco.createFastItemStack(any()) } returns fastItemStack

        mockkStatic(Eco::class)
        every { Eco.get() } returns eco

        mockkObject(Material.STONE, Material.AIR)
        every { Material.STONE.isAir } returns false
        every { Material.AIR.isAir } returns true

        recorder.lore = listOf(foreign)
    }

    @AfterEach
    fun cleanUp() {
        modules.forEach { Display.unregisterDisplayModule(it) }
        unmockkStatic(Eco::class)
        unmockkObject(Material.STONE, Material.AIR)
    }

    private fun register(module: DisplayModule) {
        modules += module
        Display.registerDisplayModule(module)
    }

    private fun item(material: Material = Material.STONE): ItemStack {
        val itemStack = mockk<ItemStack>(relaxed = true)
        every { itemStack.type } returns material
        every { itemStack.hasItemMeta() } returns true
        every { itemStack.clone() } returns mockk(relaxed = true)
        return itemStack
    }

    @Test
    fun `lines from the new api are written once next to untouched foreign lore`() {
        register(object : DisplayModule(plugin, 1) {
            override fun display(context: DisplayContext) {
                context.lore.append(listOf(Component.text("Eco")))
            }
        })

        Display.display(item())

        assertEquals(1, recorder.writes)
        assertSame(foreign, recorder.lore[0])
        assertTrue(Display.isDisplayLine(recorder.lore[1]))
        assertArrayEquals(intArrayOf(1), recorder.recorded)
    }

    @Test
    fun `a legacy module that changes nothing writes no lore`() {
        register(object : DisplayModule(plugin, 1) {
            override fun display(itemStack: ItemStack, player: Player?, vararg args: Any) {}
        })

        Display.display(item())

        assertEquals(0, recorder.writes)
        assertEquals(listOf(foreign), recorder.lore)
        assertArrayEquals(intArrayOf(), recorder.recorded)
    }

    @Test
    fun `legacy and new modules combine without restyling foreign lore`() {
        register(object : DisplayModule(plugin, 1) {
            override fun display(context: DisplayContext) {
                context.lore.append(listOf(Component.text("Eco")))
            }
        })

        register(object : DisplayModule(plugin, 2) {
            override fun display(itemStack: ItemStack, vararg args: Any) {
                recorder.setLore(itemStack, recorder.lore.map { Component.text().append(it).build() } + Component.text("§zLegacy"))
            }
        })

        Display.display(item())

        assertSame(foreign, recorder.lore[0])
        assertEquals(3, recorder.lore.size)
        assertTrue(Display.isDisplayLine(recorder.lore[1]))
        assertTrue(Display.isDisplayLine(recorder.lore[2]))
        assertArrayEquals(intArrayOf(1, 2), recorder.recorded)
    }

    @Test
    fun `consecutive legacy modules render the lore once after all modules`() {
        for (weight in 1..2) {
            register(object : DisplayModule(plugin, weight) {
                override fun display(itemStack: ItemStack, vararg args: Any) {
                    recorder.setLore(itemStack, recorder.lore + Component.text("§zLegacy $weight"))
                }
            })
        }

        Display.display(item())

        assertEquals(3, recorder.writes)
        assertSame(foreign, recorder.lore[0])
        assertArrayEquals(intArrayOf(1, 2), recorder.recorded)
    }

    @Test
    fun `display restores an already displayed item first`() {
        Display.display(item())

        assertTrue(recorder.restores > 0)
    }

    @Test
    fun `lore is not read when no module touches it`() {
        register(object : DisplayModule(plugin, 1) {
            override fun display(context: DisplayContext) {
                context.lore.append(emptyList<Component>())
            }
        })

        Display.display(item())

        assertEquals(0, recorder.reads)
        assertEquals(0, fastReads)
        assertEquals(0, recorder.writes)
        assertSame(foreign, recorder.lore[0])
        assertArrayEquals(intArrayOf(), recorder.recorded)
    }

    @Test
    fun `lore is read once when a module adds lines`() {
        register(object : DisplayModule(plugin, 1) {
            override fun display(context: DisplayContext) {
                context.lore.append(listOf(Component.text("Eco")))
            }
        })

        Display.display(item())

        assertEquals(1, recorder.reads)
        assertEquals(0, fastReads)
        assertEquals(1, recorder.writes)
        assertSame(foreign, recorder.lore[0])
        assertTrue(Display.isDisplayLine(recorder.lore[1]))
        assertEquals(2, recorder.lore.size)
    }

    @Test
    fun `a module asking for foreign lines sees the lore on the item`() {
        var seen: List<Component>? = null

        register(object : DisplayModule(plugin, 1) {
            override fun display(context: DisplayContext) {
                seen = context.lore.foreignLines
            }
        })

        Display.display(item())

        assertEquals(listOf(foreign), seen)
        assertEquals(0, recorder.writes)
    }

    @Test
    fun `a legacy module is matched against the lore from before it ran`() {
        val replaced = Component.text("Replaced")

        register(object : DisplayModule(plugin, 1) {
            override fun display(itemStack: ItemStack, vararg args: Any) {
                recorder.setLore(itemStack, listOf(replaced, Component.text().append(foreign).build()))
            }
        })

        Display.display(item())

        assertEquals(2, recorder.lore.size)
        assertSame(replaced, recorder.lore[0])
        assertSame(foreign, recorder.lore[1])
    }

    @Test
    fun `a module writing lore straight to the item does not win over the built lore`() {
        register(object : DisplayModule(plugin, 1) {
            override fun display(context: DisplayContext) {
                recorder.lore = listOf(Component.text("Written directly"))
            }
        })

        Display.display(item())

        assertEquals(listOf(foreign), recorder.lore)
    }

    @Test
    fun `revert reads no lore on an item without display lines`() {
        recorder.mayHaveDisplayLines = false

        Display.revert(item())

        assertEquals(0, fastReads)
        assertEquals(emptyList<List<Component>>(), fastWrites)
        assertEquals(listOf(foreign), recorder.lore)
    }

    @Test
    fun `revert strips display lines and nothing else`() {
        recorder.mayHaveDisplayLines = true
        recorder.lore = listOf(foreign, Component.text("Eco").insertion("eco:display"))

        Display.revert(item())

        assertEquals(1, fastReads)
        assertEquals(listOf(listOf(foreign)), fastWrites)
    }

    @Test
    fun `revert leaves lore alone when the check was only a maybe`() {
        recorder.mayHaveDisplayLines = true

        Display.revert(item())

        assertEquals(emptyList<List<Component>>(), fastWrites)
        assertEquals(listOf(foreign), recorder.lore)
    }

    @Test
    fun `air is left alone`() {
        register(object : DisplayModule(plugin, 1) {
            override fun display(context: DisplayContext) {
                context.lore.append(listOf(Component.text("Eco")))
            }
        })

        Display.display(item(Material.AIR))

        assertEquals(0, recorder.writes)
        assertEquals(0, recorder.restores)
        assertNull(recorder.recorded)
        assertFalse(recorder.lore.any { Display.isDisplayLine(it) })
    }
}
