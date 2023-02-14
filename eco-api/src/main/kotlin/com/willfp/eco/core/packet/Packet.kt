@file:JvmName("PacketExtensions")

package com.willfp.eco.core.packet

import org.bukkit.entity.Player

/** @see Packet.send */
fun Player.sendPacket(packet: Packet) =
    packet.send(this)
