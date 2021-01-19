package com.willfp.eco.spigot;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.drops.internal.FastCollatedDropQueue;
import com.willfp.eco.util.events.armorequip.ArmorListener;
import com.willfp.eco.util.events.armorequip.DispenserArmorListener;
import com.willfp.eco.util.events.entitydeathbyentity.EntityDeathByEntityListeners;
import com.willfp.eco.util.events.naturalexpgainevent.NaturalExpGainListeners;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import com.willfp.eco.util.recipe.RecipeListener;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class EcoSpigotMain extends AbstractEcoPlugin {
    /**
     * Create a new instance of eco.
     */
    public EcoSpigotMain() {
        super("eco", 87955, 10043, "com.willfp.eco.proxy", "&a");
    }

    @Override
    public void enable() {
        new FastCollatedDropQueue.CollatedRunnable(this);
        this.getEventManager().registerListener(new NaturalExpGainListeners());
        this.getEventManager().registerListener(new ArmorListener());
        this.getEventManager().registerListener(new DispenserArmorListener());
        this.getEventManager().registerListener(new EntityDeathByEntityListeners(this));
        this.getEventManager().registerListener(new RecipeListener(this));
    }

    @Override
    public void disable() {

    }

    @Override
    public void load() {

    }

    @Override
    public void onReload() {
        new FastCollatedDropQueue.CollatedRunnable(this);
    }

    @Override
    public void postLoad() {

    }

    @Override
    public List<IntegrationLoader> getIntegrationLoaders() {
        return new ArrayList<>();
    }

    @Override
    public List<AbstractCommand> getCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<AbstractPacketAdapter> getPacketAdapters() {
        return new ArrayList<>();
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
