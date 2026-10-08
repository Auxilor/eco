package com.willfp.eco.internal.spigot.proxies

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector

interface ItemUseProxy {
    fun useItemOn(
        player: Player,
        hand: EquipmentSlot,
        block: Block,
        face: BlockFace,
        clickedPosition: Vector?
    ): Boolean
}
