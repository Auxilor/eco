package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.willfp.eco.core.items.ItemComponentResult
import com.willfp.eco.internal.spigot.proxies.ItemComponentsProxy
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.ItemStack as NmsItemStack
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

private const val ATTRIBUTE_MODIFIERS = "minecraft:attribute_modifiers"

private const val ANY_SLOT = "any"

class ItemComponents : ItemComponentsProxy {
    override fun withComponents(item: ItemStack, components: Map<String, Any?>): ItemComponentResult {
        val errors = mutableListOf<String>()
        val nms = CraftItemStack.asNMSCopy(item)
        val ops = registryOps()

        for ((key, value) in components) {
            try {
                apply(nms, key, value, ops)
            } catch (e: IllegalArgumentException) {
                errors += "$key: ${e.message}"
            }
        }

        return ItemComponentResult(CraftItemStack.asCraftMirror(nms), errors)
    }

    override fun getComponents(item: ItemStack): Map<String, Any?> {
        val nms = CraftItemStack.asNMSCopy(item)
        val ops = registryOps()
        val components = mutableMapOf<String, Any?>()

        for (component in nms.components) {
            val type = component.type()
            val id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type) ?: continue

            components[id.toString()] = encode(type, component.value(), ops) ?: continue
        }

        return components
    }

    override fun getComponent(item: ItemStack, key: String): Any? {
        val type = componentType(key) ?: return null
        val value = CraftItemStack.asNMSCopy(item).get(type) ?: return null

        return encode(type, value, registryOps())
    }

    override fun removeComponents(item: ItemStack, keys: Collection<String>): ItemStack {
        val nms = CraftItemStack.asNMSCopy(item)

        for (key in keys) {
            nms.remove(componentType(key) ?: continue)
        }

        return CraftItemStack.asCraftMirror(nms)
    }

    private fun registryOps(): RegistryOps<Tag> =
        RegistryOps.create(NbtOps.INSTANCE, MinecraftServer.getServer().registryAccess())

    /** The component type [key] names, or null if it isn't one, or can't be set on items. */
    @Suppress("UNCHECKED_CAST")
    private fun componentType(key: String): DataComponentType<Any>? {
        val id = ResourceLocation.tryParse(key) ?: return null
        val type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(id).orElse(null) ?: return null

        return if (type.codec() == null) null else type as DataComponentType<Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun encode(type: DataComponentType<*>, value: Any, ops: RegistryOps<Tag>): Any? {
        val codec = (type as DataComponentType<Any>).codec() ?: return null

        return codec.encodeStart(ops, value).result().orElse(null)?.toPlainValue()
    }

    @Suppress("UNCHECKED_CAST")
    private fun apply(stack: NmsItemStack, key: String, value: Any?, ops: RegistryOps<Tag>) {
        val id = ResourceLocation.tryParse(key)
            ?: throw IllegalArgumentException("invalid component id")

        val type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(id).orElseThrow {
            IllegalArgumentException("unknown component")
        } as DataComponentType<Any>

        val codec = type.codec()
            ?: throw IllegalArgumentException("component cannot be set on items")

        // Setting a component replaces the base item's default outright, so an
        // item that only configures its attack damage silently loses the base
        // item's attack speed, armor, and everything else it came with. Merge
        // instead: a configured modifier replaces the default for its attribute
        // and slot, and the defaults it says nothing about are kept.
        val tag = if (key == ATTRIBUTE_MODIFIERS) {
            mergeWithDefaults(stack.get(type), codec, value.toTag(), ops)
        } else {
            value.toTag()
        }

        val parsed = codec.parse(ops, tag)
        val parsedValue = parsed.result().orElseThrow {
            IllegalArgumentException(parsed.error().map { it.message() }.orElse("invalid value"))
        }

        stack.set(type, parsedValue)
    }

    private fun mergeWithDefaults(
        existing: Any?,
        codec: Codec<Any>,
        configured: Tag,
        ops: RegistryOps<Tag>
    ): Tag {
        if (existing == null || configured !is ListTag) {
            return configured
        }

        // Encoded rather than read through the record, so this doesn't depend on
        // the shape of the component class in any given version.
        val defaults = codec.encodeStart(ops, existing).result().orElse(null) as? ListTag
            ?: return configured

        val merged = ListTag()

        for (default in defaults) {
            if (default is CompoundTag && configured.any { it is CompoundTag && it.replaces(default, ops) }) {
                continue
            }

            merged.add(default)
        }

        merged.addAll(configured)

        return merged
    }

    /** A modifier replaces a default modifier for the same attribute in the same slot. */
    private fun CompoundTag.replaces(default: CompoundTag, ops: RegistryOps<Tag>): Boolean {
        val type = attributeType(ops) ?: return false

        if (type != default.attributeType(ops)) {
            return false
        }

        val slot = slotGroup(ops)
        val defaultSlot = default.slotGroup(ops)

        return slot == defaultSlot || slot == ANY_SLOT || defaultSlot == ANY_SLOT
    }

    private fun CompoundTag.attributeType(ops: RegistryOps<Tag>): String? {
        val type = string("type", ops) ?: return null
        return if (":" in type) type else "minecraft:$type"
    }

    private fun CompoundTag.slotGroup(ops: RegistryOps<Tag>): String =
        string("slot", ops) ?: ANY_SLOT

    private fun CompoundTag.string(key: String, ops: RegistryOps<Tag>): String? =
        get(key)?.let { ops.getStringValue(it).result().orElse(null) }

    private fun Any?.toTag(): Tag = when (this) {
        is String -> StringTag.valueOf(this)
        is Boolean -> ByteTag.valueOf(this)
        is Byte -> ByteTag.valueOf(this)
        is Short -> ShortTag.valueOf(this)
        is Int -> IntTag.valueOf(this)
        is Long -> LongTag.valueOf(this)
        is Float -> FloatTag.valueOf(this)
        is Double -> DoubleTag.valueOf(this)
        is Iterable<*> -> ListTag().also { list -> forEach { list.add(it.toTag()) } }
        is Map<*, *> -> CompoundTag().also { tag ->
            for ((key, value) in this) {
                tag.put(key.toString(), value.toTag())
            }
        }
        else -> throw IllegalArgumentException("unsupported value: $this")
    }

    /**
     * Converted through JSON rather than read off the tag directly, because the
     * NBT accessors differ between versions while the DFU conversion doesn't.
     */
    private fun Tag.toPlainValue(): Any? =
        NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, this).toPlainValue()

    private fun JsonElement.toPlainValue(): Any? = when {
        isJsonNull -> null
        isJsonObject -> asJsonObject.entrySet().associate { (key, value) -> key to value.toPlainValue() }
        isJsonArray -> asJsonArray.map { it.toPlainValue() }
        else -> asJsonPrimitive.let {
            when {
                it.isBoolean -> it.asBoolean
                it.isNumber -> it.asDouble
                else -> it.asString
            }
        }
    }
}
