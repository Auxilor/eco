package com.willfp.eco.internal.spigot.integrations.antigrief

import com.plotsquared.bukkit.util.BukkitUtil
import com.plotsquared.core.configuration.Settings
import com.plotsquared.core.permissions.Permission
import com.plotsquared.core.player.PlotPlayer
import com.plotsquared.core.plot.PlotArea
import com.plotsquared.core.plot.flag.PlotFlag
import com.plotsquared.core.plot.flag.implementations.AnimalAttackFlag
import com.plotsquared.core.plot.flag.implementations.BreakFlag
import com.plotsquared.core.plot.flag.implementations.DoneFlag
import com.plotsquared.core.plot.flag.implementations.DropProtectionFlag
import com.plotsquared.core.plot.flag.implementations.ExplosionFlag
import com.plotsquared.core.plot.flag.implementations.HostileAttackFlag
import com.plotsquared.core.plot.flag.implementations.MiscBreakFlag
import com.plotsquared.core.plot.flag.implementations.PlaceFlag
import com.plotsquared.core.plot.flag.implementations.PveFlag
import com.plotsquared.core.plot.flag.implementations.PvpFlag
import com.plotsquared.core.plot.flag.implementations.TamedAttackFlag
import com.plotsquared.core.plot.flag.types.BlockTypeWrapper
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.entity.Animals
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Enemy
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Tameable

class AntigriefPlotSquared : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        return canBuild(
            player,
            block,
            BreakFlag::class.java,
            Permission.PERMISSION_ADMIN_DESTROY_ROAD,
            Permission.PERMISSION_ADMIN_DESTROY_UNOWNED,
            Permission.PERMISSION_ADMIN_DESTROY_OTHER
        )
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val area = location.plotArea ?: return true
        val plot = area.getOwnedPlot(BukkitUtil.adapt(location)) ?: return false

        return plot.getFlag(ExplosionFlag::class.java)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        return canBuild(
            player,
            block,
            PlaceFlag::class.java,
            Permission.PERMISSION_ADMIN_BUILD_ROAD,
            Permission.PERMISSION_ADMIN_BUILD_UNOWNED,
            Permission.PERMISSION_ADMIN_BUILD_OTHER
        )
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val area = victim.location.plotArea ?: return true
        val plotPlayer = player.plotPlayer ?: return true
        val plot = area.getPlot(BukkitUtil.adapt(victim.location))

        val (flags, permission) = when (victim) {
            is Player -> listOf(PvpFlag::class.java) to Permission.PERMISSION_ADMIN_PVP
            is ArmorStand -> listOf(MiscBreakFlag::class.java) to Permission.PERMISSION_ADMIN_DESTROY
            is Enemy -> listOf(HostileAttackFlag::class.java, PveFlag::class.java) to Permission.PERMISSION_ADMIN_PVE
            is Tameable -> listOf(TamedAttackFlag::class.java, PveFlag::class.java) to Permission.PERMISSION_ADMIN_PVE
            is Animals -> listOf(AnimalAttackFlag::class.java, PveFlag::class.java) to Permission.PERMISSION_ADMIN_PVE
            else -> listOf(PveFlag::class.java) to Permission.PERMISSION_ADMIN_PVE
        }

        val isAllowed = if (plot == null) {
            area.isRoadFlags && flags.any { area.getRoadFlag<Boolean>(it) }
        } else {
            flags.any { plot.getFlag<Boolean>(it) } || (victim !is Player && plot.isAdded(player.uniqueId))
        }

        val stub = when {
            plot == null -> "road"
            plot.hasOwner() -> "other"
            else -> "unowned"
        }

        return isAllowed || plotPlayer.hasPermission("$permission.$stub")
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val area = location.plotArea ?: return true
        val plot = area.getOwnedPlot(BukkitUtil.adapt(location))
            ?: return !(area.isRoadFlags && area.getRoadFlag(DropProtectionFlag::class.java))

        return plot.isAdded(player.uniqueId) || !plot.getFlag(DropProtectionFlag::class.java)
    }

    private fun canBuild(
        player: Player,
        block: Block,
        flag: Class<out PlotFlag<List<BlockTypeWrapper>, *>>,
        roadPermission: Permission,
        unownedPermission: Permission,
        otherPermission: Permission
    ): Boolean {
        val area = block.location.plotArea ?: return true
        val plotPlayer = player.plotPlayer ?: return true
        val plot = area.getPlot(BukkitUtil.adapt(block.location)) ?: return plotPlayer.hasPermission(roadPermission)

        if (!area.buildRangeContainsY(block.y) && !plotPlayer.hasPermission(Permission.PERMISSION_ADMIN_BUILD_HEIGHT_LIMIT)) {
            return false
        }

        if (!plot.hasOwner()) {
            return plotPlayer.hasPermission(unownedPermission)
        }

        if (!plot.isAdded(player.uniqueId)) {
            return plot.getFlag(flag).any { it.matches(block.type) } || plotPlayer.hasPermission(otherPermission)
        }

        if (Settings.Done.RESTRICT_BUILDING && DoneFlag.isDone(plot)) {
            return plotPlayer.hasPermission(Permission.PERMISSION_ADMIN_BUILD_OTHER)
        }

        return true
    }

    /*
    BlockTypeWrapper#accepts takes a WorldEdit block type, so the block type or
    block tag is matched through its id instead to avoid depending on WorldEdit.
     */
    private fun BlockTypeWrapper.matches(material: Material): Boolean {
        val id = this.toString()

        if (!id.startsWith("#")) {
            return NamespacedKey.fromString(id) == material.key
        }

        val tagKey = NamespacedKey.fromString(id.removePrefix("#")) ?: return false

        return Bukkit.getTag(Tag.REGISTRY_BLOCKS, tagKey, Material::class.java)?.isTagged(material) == true
    }

    private val Location.plotArea: PlotArea?
        get() = BukkitUtil.adapt(this).plotArea

    private val Player.plotPlayer: PlotPlayer<*>?
        get() = runCatching { BukkitUtil.adapt(this) }.getOrNull()

    override fun getPluginName(): String {
        return "PlotSquared"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AntigriefIntegration) {
            return false
        }

        return other.pluginName == this.pluginName
    }

    override fun hashCode(): Int {
        return this.pluginName.hashCode()
    }
}
