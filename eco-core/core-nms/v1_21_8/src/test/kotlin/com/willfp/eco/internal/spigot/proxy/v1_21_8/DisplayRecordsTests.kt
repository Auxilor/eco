package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.core.Eco
import com.willfp.eco.internal.spigot.proxy.common.CommonsProvider
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.io.File
import java.nio.file.Files
import net.kyori.adventure.text.Component
import net.minecraft.SharedConstants
import net.minecraft.core.component.DataComponents
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
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
        mockkStatic(Eco::class)
        every { Eco.get() } returns eco
    }

    @AfterAll
    fun cleanUp() {
        unmockkStatic(Eco::class)
        keyFile.parentFile.deleteRecursively()
    }

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
