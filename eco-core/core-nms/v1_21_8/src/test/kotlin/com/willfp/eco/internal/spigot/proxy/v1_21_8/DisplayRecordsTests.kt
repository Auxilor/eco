package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayContext
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.internal.spigot.proxy.common.CommonsProvider
import com.willfp.eco.internal.spigot.proxy.common.item.EcoFastItemStack
import com.willfp.eco.internal.spigot.proxy.common.toAdventure
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.io.File
import java.nio.file.Files
import java.util.Random
import net.kyori.adventure.text.Component
import net.minecraft.SharedConstants
import net.minecraft.core.component.DataComponents
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component as NMSComponent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.server.Bootstrap
import net.minecraft.world.item.ItemStack as NMSItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.ItemLore
import org.bukkit.craftbukkit.CraftRegistry
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DisplayRecordsTests {
    private val keyFile: File = Files.createTempDirectory("eco-display-records").resolve("display-keys.dat").toFile()

    private val registries by lazy { RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY) }

    private val records by lazy { newRecords() }

    private val foreign = Component.text("Foreign")

    private val displayLine = Component.text("Eco").insertion("eco:display")

    @BeforeAll
    fun bootstrap() {
        SharedConstants.tryDetectVersion()
        Bootstrap.bootStrap()
        CraftRegistry.setMinecraftRegistry(registries)
        CommonsProvider.setIfNeeded(CommonsInitializer.CommonsProviderImpl)

        val eco = mockk<Eco>(relaxed = true)
        every { eco.displayRecorder } answers { records }
        every { eco.createFastItemStack(any()) } answers { EcoFastItemStack(firstArg()) }
        mockkStatic(Eco::class)
        every { Eco.get() } returns eco
    }

    @AfterAll
    fun cleanUp() {
        unmockkStatic(Eco::class)
        keyFile.parentFile.deleteRecursively()
    }

    private fun lore(vararg lines: NMSComponent): ItemStack =
        CraftItemStack.asCraftMirror(NMSItemStack(Items.DIAMOND_SWORD).apply {
            set(DataComponents.LORE, ItemLore(lines.toList()))
        })

    private fun ItemStack.loreLines(): List<NMSComponent> =
        CraftItemStack.unwrap(this).get(DataComponents.LORE)?.lines ?: emptyList()

    private fun newRecords() = DisplayRecords(keyFile) { registries.createSerializationContext(NbtOps.INSTANCE) }

    private fun item(): ItemStack =
        CraftItemStack.asCraftMirror(NMSItemStack(Items.DIAMOND_SWORD).apply {
            set(DataComponents.LORE, ItemLore(listOf(foreign.toNMS())))
        })

    private fun displayed(original: ItemStack, recorder: DisplayRecords = records): ItemStack {
        val displayed = original.clone()
        recorder.setLore(displayed, listOf(foreign, displayLine))
        CraftItemStack.unwrap(displayed).set(DataComponents.CUSTOM_NAME, Component.text("Name").toNMS())
        recorder.record(displayed, original, intArrayOf(1))
        return displayed
    }

    private fun ItemStack.handle(): NMSItemStack = CraftItemStack.unwrap(this)

    private fun ItemStack.sealedRecord(): CompoundTag? =
        handle().get(DataComponents.CUSTOM_DATA)?.copyTag()?.getCompound("eco_display")?.orElse(null)

    private fun ItemStack.withRecord(record: CompoundTag): ItemStack = apply {
        handle().set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply { put("eco_display", record) }))
    }

    @Test
    fun `restore gives back the exact original item`() {
        val original = item()
        val displayed = displayed(original)

        assertNotNull(displayed.sealedRecord())
        assertTrue(records.restore(displayed))
        assertTrue(NMSItemStack.matches(original.handle(), displayed.handle()))
    }

    @Test
    fun `sealed records hide the original values`() {
        val sealed = displayed(item()).sealedRecord()!!

        assertEquals(setOf("data", "signature"), sealed.keySet())
        assertFalse(String(sealed.getByteArray("data").orElseThrow(), Charsets.ISO_8859_1).contains("Foreign"))
    }

    @Test
    fun `a tampered record is removed without restoring`() {
        val displayed = displayed(item())
        val sealed = displayed.sealedRecord()!!
        val data = sealed.getByteArray("data").orElseThrow()
        data[data.size - 1] = (data[data.size - 1] + 1).toByte()
        sealed.putByteArray("data", data)
        displayed.withRecord(sealed)

        assertFalse(records.restore(displayed))
        assertNull(displayed.handle().get(DataComponents.CUSTOM_DATA))
        assertEquals(2, displayed.handle().get(DataComponents.LORE)!!.lines.size)
    }

    @Test
    fun `a forged record is removed without restoring`() {
        val displayed = displayed(item()).withRecord(CompoundTag().apply {
            putByteArray("data", ByteArray(32))
            putByteArray("signature", ByteArray(32))
        })

        assertFalse(records.restore(displayed))
        assertNull(displayed.sealedRecord())
    }

    @Test
    fun `records stay valid with keys read from the same file`() {
        val original = item()
        val displayed = displayed(original)

        assertTrue(newRecords().restore(displayed))
        assertTrue(NMSItemStack.matches(original.handle(), displayed.handle()))
    }

    @Test
    fun `nothing is recorded when display changed nothing`() {
        val original = item()
        val displayed = original.clone()
        records.record(displayed, original, intArrayOf())

        assertNull(displayed.sealedRecord())
    }

    private fun randomLine(random: Random, depth: Int = 0): MutableComponent {
        val line: MutableComponent = when (random.nextInt(9)) {
            0, 1, 2 -> NMSComponent.literal("")
            3 -> NMSComponent.empty()
            4 -> NMSComponent.literal("Text")
            5 -> NMSComponent.literal("\u00a7zPrefixed")
            6 -> NMSComponent.literal("x\u00a7z")
            7 -> NMSComponent.translatable("\u00a7z.key")
            else -> NMSComponent.keybind("key.jump")
        }

        when (random.nextInt(6)) {
            0 -> line.withStyle(Style.EMPTY.withInsertion("eco:display"))
            1 -> line.withStyle(Style.EMPTY.withInsertion("other"))
            2 -> line.withStyle(Style.EMPTY.withItalic(false))
            3 -> line.withStyle(Style.EMPTY.withBold(true).withInsertion("eco:display"))
        }

        if (depth < 4) {
            repeat(intArrayOf(0, 1, 1, 1, 2, 3)[random.nextInt(6)]) {
                line.append(randomLine(random, depth + 1))
            }
        }

        return line
    }

    @Test
    fun `the display line check on server components never misses a display line`() {
        val random = Random(20261106)
        var display = 0
        var skipped = 0

        repeat(20_000) {
            val line = randomLine(random)
            val isDisplayLine = Display.isDisplayLine(line.toAdventure())
            val mayBe = records.mayHaveDisplayLines(lore(line))

            if (isDisplayLine) {
                display++
                assertTrue(mayBe, line.toString())
            } else if (!mayBe) {
                skipped++
            }
        }

        assertTrue(display > 2_000)
        assertTrue(skipped > 2_000)
    }

    @Test
    fun `a display line behind an empty sibling is found`() {
        val marked = NMSComponent.literal("Eco").withStyle(Style.EMPTY.withInsertion("eco:display"))
        val line = NMSComponent.empty().append(NMSComponent.empty()).append(marked)

        assertTrue(Display.isDisplayLine(line.toAdventure()))
        assertTrue(records.mayHaveDisplayLines(lore(line)))

        val item = lore(NMSComponent.literal("Foreign"), line)

        Display.revert(item)

        assertEquals(listOf("Foreign"), item.loreLines().map { it.string })
    }

    @Test
    fun `adventure backed lines are checked the same way`() {
        assertTrue(records.mayHaveDisplayLines(lore(displayLine.toNMS())))
        assertTrue(records.mayHaveDisplayLines(lore(Component.text().append(displayLine).build().toNMS())))
        assertTrue(records.mayHaveDisplayLines(lore(Component.text("\u00a7zLine").toNMS())))
        assertFalse(records.mayHaveDisplayLines(lore(foreign.toNMS())))
        assertFalse(records.mayHaveDisplayLines(lore(Component.text("Line \u00a7z").toNMS())))
    }

    @Test
    fun `an item without lore has no display lines`() {
        assertFalse(records.mayHaveDisplayLines(lore()))
        assertFalse(records.mayHaveDisplayLines(CraftItemStack.asCraftMirror(NMSItemStack(Items.DIAMOND_SWORD))))
    }

    @Test
    fun `any display line among foreign lines is found`() {
        val plain = NMSComponent.literal("Foreign")
        val styled = NMSComponent.literal("Eco").withStyle(Style.EMPTY.withInsertion("eco:display"))

        assertTrue(records.mayHaveDisplayLines(lore(plain, plain, styled)))
        assertFalse(records.mayHaveDisplayLines(lore(plain, plain, plain)))
    }

    @Test
    fun `a lore state keeps standing for the lore it was taken from`() {
        val item = lore(NMSComponent.literal("Before"))
        val state = records.getLoreState(item)

        records.setLore(item, listOf(Component.text("After")))

        assertEquals(listOf(Component.text("Before")), records.getLore(state))
        assertEquals(listOf(Component.text("After")), records.getLore(records.getLoreState(item)))
        assertEquals(emptyList<Component>(), records.getLore(null))
    }

    private val styledLore = arrayOf<NMSComponent>(
        NMSComponent.literal("Plain"),
        NMSComponent.literal("Red").withStyle(Style.EMPTY.withBold(true)),
        NMSComponent.translatable("item.minecraft.diamond"),
        NMSComponent.empty().withStyle(Style.EMPTY.withItalic(false)).append(NMSComponent.literal("Wrapped")),
        NMSComponent.literal("Not eco's \u00a7z").withStyle(Style.EMPTY.withBold(true)),
    )

    private fun <T> withModule(module: DisplayModule, block: () -> T): T {
        Display.registerDisplayModule(module)

        return try {
            block()
        } finally {
            Display.unregisterDisplayModule(module)
        }
    }

    private fun addingModule() = object : DisplayModule(mockk<EcoPlugin>(relaxed = true), 1) {
        override fun display(context: DisplayContext) {
            context.lore.append(listOf(Component.text("Eco")))
            context.lore.prepend(listOf(Component.text("Top")))
        }
    }

    @Test
    fun `lore round trips through display and revert`() {
        withModule(addingModule()) {
            val original = lore(*styledLore)
            val item = original.clone()

            Display.display(item)

            assertEquals(styledLore.size + 2, item.loreLines().size)
            assertEquals(original.loreLines().map { it.toAdventure() }, item.loreLines().subList(1, styledLore.size + 1).map { it.toAdventure() })
            assertTrue(Display.isDisplayLine(item.loreLines().first().toAdventure()))
            assertTrue(Display.isDisplayLine(item.loreLines().last().toAdventure()))

            Display.revert(item)

            assertTrue(NMSItemStack.matches(original.handle(), item.handle()))
        }
    }

    @Test
    fun `displaying twice does not duplicate lines`() {
        withModule(addingModule()) {
            val original = lore(*styledLore)
            val once = original.clone().also { Display.display(it) }
            val twice = once.clone().also { Display.display(it) }

            assertEquals(once.loreLines().map { it.toAdventure() }, twice.loreLines().map { it.toAdventure() })
            assertTrue(NMSItemStack.matches(original.handle(), twice.also { Display.revert(it) }.handle()))
        }
    }

    @Test
    fun `an item no module changes leaves display exactly as it came`() {
        val original = lore(*styledLore)
        val item = original.clone()

        Display.display(item)

        assertTrue(NMSItemStack.matches(original.handle(), item.handle()))
        assertTrue(original.handle().get(DataComponents.LORE) === item.handle().get(DataComponents.LORE))
    }

    @Test
    fun `display lines left on an item without a record are stripped by revert`() {
        val item = lore(
            NMSComponent.literal("Foreign"),
            NMSComponent.literal("Eco").withStyle(Style.EMPTY.withInsertion("eco:display")),
            NMSComponent.empty().append(NMSComponent.literal("\u00a7zLegacy")),
        )

        Display.revert(item)

        assertEquals(listOf("Foreign"), item.loreLines().map { it.string })
    }

    @Test
    fun `records over the maximum size are not written`() {
        val original = item()
        val displayed = original.clone()
        displayed.handle().set(
            DataComponents.CUSTOM_NAME,
            Component.text("x".repeat(10_000)).toNMS()
        )
        original.handle().set(
            DataComponents.CUSTOM_NAME,
            Component.text("y".repeat(10_000)).toNMS()
        )
        records.record(displayed, original, intArrayOf())

        assertNull(displayed.sealedRecord())
    }
}
