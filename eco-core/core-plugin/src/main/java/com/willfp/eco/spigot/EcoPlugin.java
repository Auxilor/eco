package com.willfp.eco.spigot;

import com.willfp.eco.spigot.display.PacketAutoRecipe;
import com.willfp.eco.spigot.display.PacketChat;
import com.willfp.eco.spigot.display.PacketOpenWindowMerchant;
import com.willfp.eco.spigot.display.PacketSetCreativeSlot;
import com.willfp.eco.spigot.display.PacketSetSlot;
import com.willfp.eco.spigot.display.PacketWindowItems;
import com.willfp.eco.spigot.drops.CollatedRunnable;
import com.willfp.eco.spigot.eventlisteners.EntityDeathByEntityListeners;
import com.willfp.eco.spigot.eventlisteners.NaturalExpGainListeners;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatAAC;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatMatrix;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatNCP;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefFactionsUUID;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefGriefPrevention;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefKingdoms;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefLands;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefTowny;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefWorldGuard;
import com.willfp.eco.spigot.integrations.mcmmo.McmmoIntegrationImpl;
import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.events.armorequip.ArmorListener;
import com.willfp.eco.util.events.armorequip.DispenserArmorListener;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.integrations.anticheat.AnticheatManager;
import com.willfp.eco.util.integrations.antigrief.AntigriefManager;
import com.willfp.eco.util.integrations.mcmmo.McmmoManager;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EcoPlugin extends AbstractEcoPlugin {
    /**
     * Instance of eco.
     */
    @Getter
    private static EcoPlugin instance;

    /**
     * Create a new instance of eco.
     */
    public EcoPlugin() {
        super("eco", 87955, 10043, "com.willfp.eco.proxy", "&a");
        instance = this;
        Display.setFinalizeKey(this.getNamespacedKeyFactory().create("finalized"));
    }

    @Override
    public void enable() {
        new CollatedRunnable(this);
        this.getEventManager().registerListener(new NaturalExpGainListeners());
        this.getEventManager().registerListener(new ArmorListener());
        this.getEventManager().registerListener(new DispenserArmorListener());
        this.getEventManager().registerListener(new EntityDeathByEntityListeners(this));
    }

    @Override
    public void disable() {

    }

    @Override
    public void load() {

    }

    @Override
    public void onReload() {
        new CollatedRunnable(this);
    }

    @Override
    public void postLoad() {

    }

    @Override
    public List<IntegrationLoader> getIntegrationLoaders() {
        return Arrays.asList(
                // AntiGrief
                new IntegrationLoader("WorldGuard", () -> AntigriefManager.register(new AntigriefWorldGuard())),
                new IntegrationLoader("GriefPrevention", () -> AntigriefManager.register(new AntigriefGriefPrevention())),
                new IntegrationLoader("FactionsUUID", () -> AntigriefManager.register(new AntigriefFactionsUUID())),
                new IntegrationLoader("Towny", () -> AntigriefManager.register(new AntigriefTowny())),
                new IntegrationLoader("Lands", () -> AntigriefManager.register(new AntigriefLands(this))),
                new IntegrationLoader("Kingdoms", () -> AntigriefManager.register(new AntigriefKingdoms())),

                // Anticheat
                new IntegrationLoader("AAC5", () -> AnticheatManager.register(this, new AnticheatAAC())),
                new IntegrationLoader("Matrix", () -> AnticheatManager.register(this, new AnticheatMatrix())),
                new IntegrationLoader("NoCheatPlus", () -> AnticheatManager.register(this, new AnticheatNCP())),

                // Misc
                new IntegrationLoader("mcMMO", () -> McmmoManager.register(new McmmoIntegrationImpl()))
        );
    }

    @Override
    public List<AbstractCommand> getCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<AbstractPacketAdapter> getPacketAdapters() {
        return Arrays.asList(
                new PacketAutoRecipe(this),
                new PacketChat(this),
                new PacketOpenWindowMerchant(this),
                new PacketSetCreativeSlot(this),
                new PacketSetSlot(this),
                new PacketWindowItems(this)
        );
    }

    @Override
    public List<Listener> getListeners() {
        return new ArrayList<>();
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return new ArrayList<>();
    }
}
