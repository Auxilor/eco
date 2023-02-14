package com.willfp.eco.internal.spigot.proxy.v1_18_R1.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import net.kyori.adventure.nbt.api.BinaryTagHolder
import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.nbt.TagParser
import net.minecraft.network.protocol.game.ClientboundChatPacket
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object PacketChat : PacketListener {
    private val gsonComponentSerializer = GsonComponentSerializer.gson()

    private val field = ClientboundChatPacket::class.java.declaredFields
        .first { it.type == net.minecraft.network.chat.Component::class.java }
        .apply { isAccessible = true }

    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundChatPacket ?: return

        val newMessage = modifyComponent(packet.message, event.player)

        field.set(packet, newMessage)
    }

    private fun modifyComponent(obj: net.minecraft.network.chat.Component, player: Player): Any {
        val component = gsonComponentSerializer.deserialize(
            net.minecraft.network.chat.Component.Serializer.toJson(
                obj
            )
        ).asComponent() as BuildableComponent<*, *>

        val newComponent = modifyBaseComponent(component, player)

        return net.minecraft.network.chat.Component.Serializer.fromJson(
            gsonComponentSerializer.serialize(newComponent.asComponent())
        ) ?: obj
    }

    private fun modifyBaseComponent(baseComponent: Component, player: Player): Component {
        var component = baseComponent

        if (component is TranslatableComponent) {
            val args = mutableListOf<Component>()
            for (arg in component.args()) {
                args.add(modifyBaseComponent(arg, player))
            }
            component = component.args(args)
        }

        val children = mutableListOf<Component>()
        for (child in component.children()) {
            children.add(modifyBaseComponent(child, player))
        }
        component = component.children(children)

        @Suppress("UNCHECKED_CAST")
        val hoverEvent: HoverEvent<Any> = component.style().hoverEvent() as HoverEvent<Any>? ?: return component

        val showItem = hoverEvent.value()

        if (showItem !is HoverEvent.ShowItem) {
            return component
        }

        val newShowItem = showItem.nbt(
            @Suppress("UnstableApiUsage", "DEPRECATION")
            BinaryTagHolder.of(
                CraftItemStack.asNMSCopy(
                    Display.display(
                        CraftItemStack.asBukkitCopy(
                            CraftItemStack.asNMSCopy(
                                ItemStack(
                                    Material.matchMaterial(
                                        showItem.item()
                                            .toString()
                                    ) ?: return component,
                                    showItem.count()
                                )
                            ).apply {
                                this.tag = TagParser.parseTag(
                                    showItem.nbt()?.string() ?: return component
                                ) ?: return component
                            }
                        ),
                        player
                    )
                ).orCreateTag.toString()
            )
        )

        val newHover = hoverEvent.value(newShowItem)
        val style = component.style().hoverEvent(newHover)
        return component.style(style)
    }
}
