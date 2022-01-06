package com.willfp.eco.internal.spigot

import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.core.integrations.anticheat.AnticheatManager
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.customitems.CustomItemsManager
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.core.integrations.shop.ShopManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.internal.display.EcoDisplayHandler
import com.willfp.eco.internal.drops.DropManager
import com.willfp.eco.internal.entities.EntityArgParserAdult
import com.willfp.eco.internal.entities.EntityArgParserAttackDamage
import com.willfp.eco.internal.entities.EntityArgParserAttackSpeed
import com.willfp.eco.internal.entities.EntityArgParserBaby
import com.willfp.eco.internal.entities.EntityArgParserFlySpeed
import com.willfp.eco.internal.entities.EntityArgParserFollowRange
import com.willfp.eco.internal.entities.EntityArgParserHealth
import com.willfp.eco.internal.entities.EntityArgParserJumpStrength
import com.willfp.eco.internal.entities.EntityArgParserKnockback
import com.willfp.eco.internal.entities.EntityArgParserKnockbackResistance
import com.willfp.eco.internal.entities.EntityArgParserName
import com.willfp.eco.internal.entities.EntityArgParserNoAI
import com.willfp.eco.internal.entities.EntityArgParserSize
import com.willfp.eco.internal.entities.EntityArgParserSpawnReinforcements
import com.willfp.eco.internal.entities.EntityArgParserSpeed
import com.willfp.eco.internal.items.ArgParserColor
import com.willfp.eco.internal.items.ArgParserCustomModelData
import com.willfp.eco.internal.items.ArgParserEnchantment
import com.willfp.eco.internal.items.ArgParserFlag
import com.willfp.eco.internal.items.ArgParserName
import com.willfp.eco.internal.items.ArgParserTexture
import com.willfp.eco.internal.items.ArgParserUnbreakable
import com.willfp.eco.internal.spigot.arrows.ArrowDataListener
import com.willfp.eco.internal.spigot.data.DataListener
import com.willfp.eco.internal.spigot.data.PlayerBlockListener
import com.willfp.eco.internal.spigot.data.storage.ProfileSaver
import com.willfp.eco.internal.spigot.display.PacketAutoRecipe
import com.willfp.eco.internal.spigot.display.PacketChat
import com.willfp.eco.internal.spigot.display.PacketHeldWindowItems
import com.willfp.eco.internal.spigot.display.PacketOpenWindowMerchant
import com.willfp.eco.internal.spigot.display.PacketSetCreativeSlot
import com.willfp.eco.internal.spigot.display.PacketSetSlot
import com.willfp.eco.internal.spigot.display.PacketWindowItems
import com.willfp.eco.internal.spigot.display.frame.clearFrames
import com.willfp.eco.internal.spigot.drops.CollatedRunnable
import com.willfp.eco.internal.spigot.eventlisteners.EntityDeathByEntityListeners
import com.willfp.eco.internal.spigot.eventlisteners.NaturalExpGainListeners
import com.willfp.eco.internal.spigot.eventlisteners.PlayerJumpListenersPaper
import com.willfp.eco.internal.spigot.eventlisteners.PlayerJumpListenersSpigot
import com.willfp.eco.internal.spigot.eventlisteners.armor.ArmorChangeEventListeners
import com.willfp.eco.internal.spigot.eventlisteners.armor.ArmorListener
import com.willfp.eco.internal.spigot.gui.GUIListener
import com.willfp.eco.internal.spigot.integrations.afk.AFKIntegrationCMI
import com.willfp.eco.internal.spigot.integrations.afk.AFKIntegrationEssentials
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatAAC
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatAlice
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatMatrix
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatNCP
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatSpartan
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatVulcan
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefBentoBox
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefCombatLogXV10
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefCombatLogXV11
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefCrashClaim
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefDeluxeCombat
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefFactionsUUID
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefGriefPrevention
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefIridiumSkyblock
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefKingdoms
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefLands
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefSuperiorSkyblock2
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefTowny
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefWorldGuard
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsHeadDatabase
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsItemsAdder
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsOraxen
import com.willfp.eco.internal.spigot.integrations.economy.EconomyVault
import com.willfp.eco.internal.spigot.integrations.hologram.HologramCMI
import com.willfp.eco.internal.spigot.integrations.hologram.HologramDecentHolograms
import com.willfp.eco.internal.spigot.integrations.hologram.HologramHolographicDisplays
import com.willfp.eco.internal.spigot.integrations.mcmmo.McmmoIntegrationImpl
import com.willfp.eco.internal.spigot.integrations.multiverseinventories.MultiverseInventoriesIntegration
import com.willfp.eco.internal.spigot.integrations.shop.ShopShopGuiPlus
import com.willfp.eco.internal.spigot.proxy.BlockBreakProxy
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.SkullProxy
import com.willfp.eco.internal.spigot.proxy.TPSProxy
import com.willfp.eco.internal.spigot.recipes.ShapedRecipeListener
import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.ServerUtils
import com.willfp.eco.util.SkullUtils
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

abstract class EcoSpigotPlugin : EcoPlugin(
    773,
    10043,
    "com.willfp.eco.internal.spigot.proxy",
    "&a"
) {
    init {
        Items.registerArgParser(ArgParserEnchantment())
        Items.registerArgParser(ArgParserColor())
        Items.registerArgParser(ArgParserTexture())
        Items.registerArgParser(ArgParserCustomModelData())
        Items.registerArgParser(ArgParserFlag())
        Items.registerArgParser(ArgParserUnbreakable())
        Items.registerArgParser(ArgParserName())

        Entities.registerArgParser(EntityArgParserName())
        Entities.registerArgParser(EntityArgParserNoAI())
        Entities.registerArgParser(EntityArgParserAttackDamage())
        Entities.registerArgParser(EntityArgParserAttackSpeed())
        Entities.registerArgParser(EntityArgParserFlySpeed())
        Entities.registerArgParser(EntityArgParserFollowRange())
        Entities.registerArgParser(EntityArgParserHealth())
        Entities.registerArgParser(EntityArgParserJumpStrength())
        Entities.registerArgParser(EntityArgParserKnockback())
        Entities.registerArgParser(EntityArgParserKnockbackResistance())
        Entities.registerArgParser(EntityArgParserSize())
        Entities.registerArgParser(EntityArgParserSpawnReinforcements())
        Entities.registerArgParser(EntityArgParserSpeed())
        Entities.registerArgParser(EntityArgParserBaby())
        Entities.registerArgParser(EntityArgParserAdult())

        val skullProxy = getProxy(SkullProxy::class.java)
        SkullUtils.initialize(
            { meta, base64 -> skullProxy.setSkullTexture(meta, base64) },
            { meta -> skullProxy.getSkullTexture(meta) }
        )

        val blockBreakProxy = getProxy(BlockBreakProxy::class.java)
        BlockUtils.initialize { player, block -> blockBreakProxy.breakBlock(player, block) }

        val tpsProxy = getProxy(TPSProxy::class.java)
        ServerUtils.initialize { tpsProxy.getTPS() }

        postInit()
    }

    private fun postInit() {
        Display.setHandler(EcoDisplayHandler(this))
    }

    override fun handleEnable() {
        CollatedRunnable(this)

        if (!Prerequisite.HAS_PAPER.isMet) {
            (Eco.getHandler() as EcoHandler).setAdventure(BukkitAudiences.create(this))
        }

        this.logger.info("Ignore messages about deprecated events!")

        // Init FIS
        this.getProxy(FastItemStackFactoryProxy::class.java).create(ItemStack(Material.AIR)).unwrap()
    }

    override fun handleDisable() {
        this.logger.info("Saving player data...")
        val start = System.currentTimeMillis()
        Eco.getHandler().playerProfileHandler.save()
        this.logger.info("Saved player data! Took ${System.currentTimeMillis() - start}ms")
        Eco.getHandler().adventure?.close()
    }

    override fun handleReload() {
        CollatedRunnable(this)
        DropManager.update(this)
        ProfileSaver(this)
        this.scheduler.runTimer(
            { clearFrames() },
            this.configYml.getInt("display-frame-ttl").toLong(),
            this.configYml.getInt("display-frame-ttl").toLong()
        )
    }

    override fun handleAfterLoad() {
        CustomItemsManager.registerAllItems()
        ShopManager.registerEcoProvider()
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            // AntiGrief
            IntegrationLoader("IridiumSkyblock") { AntigriefManager.register(AntigriefIridiumSkyblock()) },
            IntegrationLoader("DeluxeCombat") { AntigriefManager.register(AntigriefDeluxeCombat()) },
            IntegrationLoader("SuperiorSkyblock2") { AntigriefManager.register(AntigriefSuperiorSkyblock2()) },
            IntegrationLoader("BentoBox") { AntigriefManager.register(AntigriefBentoBox()) },
            IntegrationLoader("WorldGuard") { AntigriefManager.register(AntigriefWorldGuard()) },
            IntegrationLoader("GriefPrevention") { AntigriefManager.register(AntigriefGriefPrevention()) },
            IntegrationLoader("FactionsUUID") { AntigriefManager.register(AntigriefFactionsUUID()) },
            IntegrationLoader("Towny") { AntigriefManager.register(AntigriefTowny()) },
            IntegrationLoader("Lands") { AntigriefManager.register(AntigriefLands(this)) },
            IntegrationLoader("Kingdoms") { AntigriefManager.register(AntigriefKingdoms()) },
            IntegrationLoader("CrashClaim") { AntigriefManager.register(AntigriefCrashClaim()) },
            IntegrationLoader("CombatLogX") {
                val pluginManager = Bukkit.getPluginManager()
                val combatLogXPlugin = pluginManager.getPlugin("CombatLogX") ?: return@IntegrationLoader
                val pluginVersion = combatLogXPlugin.description.version
                if (pluginVersion.startsWith("10")) {
                    AntigriefManager.register(AntigriefCombatLogXV10())
                }
                if (pluginVersion.startsWith("11")) {
                    AntigriefManager.register(AntigriefCombatLogXV11())
                }
            },

            // Anticheat
            IntegrationLoader("AAC5") { AnticheatManager.register(this, AnticheatAAC()) },
            IntegrationLoader("Matrix") { AnticheatManager.register(this, AnticheatMatrix()) },
            IntegrationLoader("NoCheatPlus") { AnticheatManager.register(this, AnticheatNCP()) },
            IntegrationLoader("Spartan") { AnticheatManager.register(this, AnticheatSpartan()) },
            IntegrationLoader("Vulcan") { AnticheatManager.register(this, AnticheatVulcan()) },
            IntegrationLoader("Alice") { AnticheatManager.register(this, AnticheatAlice()) },

            // Custom Items
            IntegrationLoader("Oraxen") { CustomItemsManager.register(CustomItemsOraxen()) },
            IntegrationLoader("ItemsAdder") { CustomItemsManager.register(CustomItemsItemsAdder()) },
            IntegrationLoader("HeadDatabase") { CustomItemsManager.register(CustomItemsHeadDatabase(this)) },

            // Shop
            IntegrationLoader("ShopGUIPlus") { ShopManager.register(ShopShopGuiPlus()) },

            // Hologram
            IntegrationLoader("HolographicDisplays") { HologramManager.register(HologramHolographicDisplays(this)) },
            IntegrationLoader("CMI") { HologramManager.register(HologramCMI()) },
            IntegrationLoader("DecentHolograms") { HologramManager.register(HologramDecentHolograms()) },
            //IntegrationLoader("GHolo") { HologramManager.register(HologramGHolo()) },

            // AFK
            IntegrationLoader("Essentials") { AFKManager.register(AFKIntegrationEssentials()) },
            IntegrationLoader("CMI") { AFKManager.register(AFKIntegrationCMI()) },

            // Economy
            IntegrationLoader("Vault") {
                val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
                if (rsp != null) {
                    EconomyManager.register(EconomyVault(rsp.provider))
                }
            },

            // Misc
            IntegrationLoader("mcMMO") { McmmoManager.register(McmmoIntegrationImpl()) },
            IntegrationLoader("Multiverse-Inventories") {
                this.eventManager.registerListener(
                    MultiverseInventoriesIntegration(this)
                )
            }
        )
    }

    override fun loadPacketAdapters(): List<AbstractPacketAdapter> {
        return listOf(
            PacketAutoRecipe(this),
            PacketChat(this),
            PacketSetCreativeSlot(this),
            PacketSetSlot(this),
            PacketWindowItems(this),
            PacketHeldWindowItems(this),
            PacketOpenWindowMerchant(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        val listeners = mutableListOf(
            NaturalExpGainListeners(),
            ArmorListener(),
            EntityDeathByEntityListeners(this),
            ShapedRecipeListener(this),
            GUIListener(this),
            ArrowDataListener(this),
            ArmorChangeEventListeners(this),
            DataListener(this),
            PlayerBlockListener(this)
        )

        if (Prerequisite.HAS_PAPER.isMet) {
            listeners.add(PlayerJumpListenersPaper())
        } else {
            listeners.add(PlayerJumpListenersSpigot())
        }

        return listeners
    }
}