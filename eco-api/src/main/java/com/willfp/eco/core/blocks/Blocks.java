package com.willfp.eco.core.blocks;

import com.willfp.eco.core.blocks.impl.EmptyTestableBlock;
import com.willfp.eco.core.blocks.impl.MaterialTestableBlock;
import com.willfp.eco.core.blocks.impl.UnrestrictedMaterialTestableBlock;
import com.willfp.eco.core.blocks.provider.BlockProvider;
import com.willfp.eco.util.NamespacedKeyUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to manage all custom and vanilla blocks.
 */
public final class Blocks {
    /**
     * All entities.
     */
    private static final Map<NamespacedKey, TestableBlock> REGISTRY = new ConcurrentHashMap<>();

    /**
     * All block providers.
     */
    private static final Map<String, BlockProvider> PROVIDERS = new ConcurrentHashMap<>();

    /**
     * The lookup handler.
     */
    private static final BlocksLookupHandler BLOCKS_LOOKUP_HANDLER = new BlocksLookupHandler(Blocks::doParse);

    /**
     * Register a new custom block.
     *
     * @param key   The key of the block.
     * @param block The block.
     */
    public static void registerCustomBlock(@NotNull final NamespacedKey key,
                                           @NotNull final TestableBlock block) {
        REGISTRY.put(key, block);
    }

    /**
     * Register a new block provider.
     *
     * @param provider The provider.
     */
    public static void registerBlockProvider(@NotNull final BlockProvider provider) {
        PROVIDERS.put(provider.getNamespace(), provider);
    }

    /**
     * Remove a block.
     *
     * @param key The key of the block.
     */
    public static void removeCustomBlock(@NotNull final NamespacedKey key) {
        REGISTRY.remove(key);
    }

    /**
     * This is the backbone of the eco block system.
     * <p>
     * You can look up a TestableBlock for any material or custom block,
     * and it will return it.
     * <p>
     * If you want to get a Block instance from this, then just call
     * {@link TestableBlock#place(Location)}.
     *
     * @param key The lookup string.
     * @return The testable block, or an empty testable block if not found.
     */
    @NotNull
    public static TestableBlock lookup(@NotNull final String key) {
        return BLOCKS_LOOKUP_HANDLER.parseKey(key);
    }

    @NotNull
    private static TestableBlock doParse(@NotNull final String[] args) {
        if (args.length == 0) {
            return new EmptyTestableBlock();
        }

        String[] split = args[0].toLowerCase().split(":");
        if (split.length == 1) {
            if (args[0].startsWith("*")) {
                Material type = Material.getMaterial(args[0].substring(1));
                return (type == null) ? new EmptyTestableBlock() : new UnrestrictedMaterialTestableBlock(type);
            } else {
                Material type = Material.getMaterial(args[0].toUpperCase());
                return (type == null) ? new EmptyTestableBlock() : new MaterialTestableBlock(type);
            }
        }

        NamespacedKey namespacedKey = NamespacedKeyUtils.create(split[0], split[1]);
        TestableBlock block = REGISTRY.get(namespacedKey);

        if (block != null) {
            return block;
        }

        BlockProvider provider = PROVIDERS.get(split[0]);
        if (provider == null) {
            return new EmptyTestableBlock();
        }

        block = provider.provideForKey(split[1]);
        if (block == null) {
            return new EmptyTestableBlock();
        }

        registerCustomBlock(namespacedKey, block);
        return block;
    }

    /**
     * Get if block is a custom block.
     *
     * @param block The block to check.
     * @return If is custom.
     */
    public static boolean isCustomBlock(@NotNull final Block block) {
        for (TestableBlock testable : REGISTRY.values()) {
            if (testable.matches(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all registered custom blocks.
     *
     * @return A set of all blocks.
     */
    public static Set<TestableBlock> getCustomBlocks() {
        return new HashSet<>(REGISTRY.values());
    }

    private Blocks() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
