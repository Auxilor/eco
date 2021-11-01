package com.willfp.eco.spigot

import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.core.integrations.anticheat.AnticheatManager
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.customitems.CustomItemsManager
import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.core.integrations.shop.ShopManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.args.EnchantmentArgParser
import com.willfp.eco.core.items.args.TextureArgParser
import com.willfp.eco.internal.display.EcoDisplayHandler
import com.willfp.eco.internal.drops.DropManager
import com.willfp.eco.proxy.BlockBreakProxy
import com.willfp.eco.proxy.FastItemStackFactoryProxy
import com.willfp.eco.proxy.SkullProxy
import com.willfp.eco.spigot.arrows.ArrowDataListener
import com.willfp.eco.spigot.data.DataListener
import com.willfp.eco.spigot.data.EcoPlayerProfileHandler
import com.willfp.eco.spigot.data.PlayerBlockListener
import com.willfp.eco.spigot.data.storage.DataHandler
import com.willfp.eco.spigot.data.storage.MySQLDataHandler
import com.willfp.eco.spigot.data.storage.YamlDataHandler
import com.willfp.eco.spigot.display.*
import com.willfp.eco.spigot.display.frame.clearFrames
import com.willfp.eco.spigot.drops.CollatedRunnable
import com.willfp.eco.spigot.eventlisteners.EntityDeathByEntityListeners
import com.willfp.eco.spigot.eventlisteners.NaturalExpGainListeners
import com.willfp.eco.spigot.eventlisteners.PlayerJumpListeners
import com.willfp.eco.spigot.eventlisteners.armor.ArmorChangeEventListeners
import com.willfp.eco.spigot.eventlisteners.armor.ArmorListener
import com.willfp.eco.spigot.gui.GUIListener
import com.willfp.eco.spigot.integrations.afk.AFKIntegrationCMI
import com.willfp.eco.spigot.integrations.afk.AFKIntegrationEssentials
import com.willfp.eco.spigot.integrations.anticheat.*
import com.willfp.eco.spigot.integrations.antigrief.*
import com.willfp.eco.spigot.integrations.customitems.CustomItemsHeadDatabase
import com.willfp.eco.spigot.integrations.customitems.CustomItemsItemsAdder
import com.willfp.eco.spigot.integrations.customitems.CustomItemsOraxen
import com.willfp.eco.spigot.integrations.hologram.HologramCMI
import com.willfp.eco.spigot.integrations.hologram.HologramGHolo
import com.willfp.eco.spigot.integrations.hologram.HologramHolographicDisplays
import com.willfp.eco.spigot.integrations.mcmmo.McmmoIntegrationImpl
import com.willfp.eco.spigot.integrations.multiverseinventories.MultiverseInventoriesIntegration
import com.willfp.eco.spigot.integrations.shop.ShopShopGuiPlus
import com.willfp.eco.spigot.recipes.ShapedRecipeListener
import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.SkullUtils
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

abstract class EcoSpigotPlugin : EcoPlugin(
    773,
    10043,
    "com.willfp.eco.proxy",
    "&a"
) {
    lateinit var dataHandler: DataHandler

    init {
        Items.registerArgParser(EnchantmentArgParser())
        Items.registerArgParser(TextureArgParser())

        val skullProxy = getProxy(SkullProxy::class.java)
        SkullUtils.initialize(
            { meta: SkullMeta, base64: String -> skullProxy.setSkullTexture(meta, base64) },
            { meta: SkullMeta -> skullProxy.getSkullTexture(meta) }
        )

        val blockBreakProxy = getProxy(BlockBreakProxy::class.java)
        BlockUtils.initialize { player: Player, block: Block -> blockBreakProxy.breakBlock(player, block) }

        postInit()
    }

    private fun postInit() {
        Display.handler = EcoDisplayHandler(this)

        this.dataHandler = if (this.configYml.getBool("mysql.enabled"))
            MySQLDataHandler(this) else YamlDataHandler(this)
    }

    override fun handleEnable() {
        CollatedRunnable(this)

        if (!Prerequisite.HAS_PAPER.isMet) {
            (Eco.getHandler() as EcoHandler).setAdventure(BukkitAudiences.create(this))
        }

        this.logger.info("Ignore messages about deprecated events!")

        if (!this.configYml.getBool("enable-bstats")) {
            logger.severe("")
            logger.severe("----------------------------")
            logger.severe("")
            logger.severe("Looks like you've disabled bStats!")
            logger.severe("This means that information about java version,")
            logger.severe("player count, server version, and other data")
            logger.severe("isn't able to be used to ensure that support isn't dropped!")
            logger.severe("Enable bStats in /plugins/eco/config.yml")
            logger.severe("")
            logger.severe("----------------------------")
            logger.severe("")
        }

        this.getProxy(FastItemStackFactoryProxy::class.java).create(ItemStack(Material.AIR)).unwrap()
    }

    override fun handleDisable() {
        Eco.getHandler().adventure?.close()
    }

    override fun handleReload() {
        CollatedRunnable(this)
        DropManager.update(this)
        this.scheduler.runTimer(
            { clearFrames() },
            this.configYml.getInt("display-frame-ttl").toLong(),
            this.configYml.getInt("display-frame-ttl").toLong()
        )
        this.scheduler.runTimer(
            {
                (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler)
                    .autosave(this.configYml.getBool("autosave.async"))
            },
            this.configYml.getInt("autosave.ticks").toLong(),
            this.configYml.getInt("autosave.ticks").toLong()
        )
    }

    override fun handleAfterLoad() {
        CustomItemsManager.registerAllItems()
        ShopManager.registerEcoProvider()
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            // AntiGrief
            IntegrationLoader("SuperiorSkyblock2") { AntigriefManager.register(AntigriefSuperiorSkyblock2()) },
            IntegrationLoader("WorldGuard") { AntigriefManager.register(AntigriefWorldGuard()) },
            IntegrationLoader("GriefPrevention") { AntigriefManager.register(AntigriefGriefPrevention()) },
            IntegrationLoader("FactionsUUID") { AntigriefManager.register(AntigriefFactionsUUID()) },
            IntegrationLoader("Towny") { AntigriefManager.register(AntigriefTowny()) },
            IntegrationLoader("Lands") { AntigriefManager.register(AntigriefLands(this)) },
            IntegrationLoader("Kingdoms") { AntigriefManager.register(AntigriefKingdoms()) },
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
            IntegrationLoader("GHolo") { HologramManager.register(HologramGHolo()) },

            // AFK
            IntegrationLoader("Essentials") { AFKManager.register(AFKIntegrationEssentials()) },
            IntegrationLoader("CMI") { AFKManager.register(AFKIntegrationCMI()) },

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
            PacketOpenWindowMerchant(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            NaturalExpGainListeners(),
            ArmorListener(),
            EntityDeathByEntityListeners(this),
            ShapedRecipeListener(this),
            PlayerJumpListeners(),
            GUIListener(this),
            ArrowDataListener(this),
            ArmorChangeEventListeners(this),
            DataListener(),
            PlayerBlockListener(this)
        )
    }
}