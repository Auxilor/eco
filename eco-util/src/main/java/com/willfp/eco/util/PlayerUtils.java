package com.willfp.eco.util;

import com.willfp.eco.util.optional.Prerequisite;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@UtilityClass
public class PlayerUtils {
    /**
     * If the meta set function has been set.
     */
    private boolean initialized = false;

    /**
     * The block break function.
     */
    private BiConsumer<Player, Block> blockBreakConsumer = null;

    /**
     * The cooldown function.
     */
    private Function<Player, Double> cooldownFunction = null;

    /**
     * Break the block as if the player had done it manually.
     *
     * @param player The player to break the block as.
     * @param block  The block to break.
     */
    public void breakBlock(@NotNull final Player player,
                           @NotNull final Block block) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(blockBreakConsumer, "Must be initialized!");

        blockBreakConsumer.accept(player, block);
    }

    /**
     * Get the attack cooldown for a player.
     *
     * @param player The player's attack cooldown.
     * @return A value between 0 and 1, with 1 representing full power.
     */
    public double getAttackCooldown(@NotNull final Player player) {
        Validate.isTrue(initialized, "Must be initialized!");
        Validate.notNull(cooldownFunction, "Must be initialized!");

        if (Prerequisite.MINIMUM_1_16.isMet()) {
            return player.getAttackCooldown();
        }

        return cooldownFunction.apply(player);
    }

    /**
     * Initialize the block break function.
     *
     * @param function The function.
     */
    public void initializeBlockBreak(@NotNull final BiConsumer<Player, Block> function) {
        blockBreakConsumer = function;

        initialized = cooldownFunction != null;
    }

    /**
     * Initialize the cooldown function.
     *
     * @param function The function.
     */
    public void initializeCooldown(@NotNull final Function<Player, Double> function) {
        cooldownFunction = function;

        initialized = blockBreakConsumer != null;
    }
}
