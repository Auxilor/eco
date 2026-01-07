package com.willfp.eco.core.blocks;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.willfp.eco.core.blocks.impl.EmptyTestableBlock;
import com.willfp.eco.core.blocks.impl.MaterialTestableBlock;
import com.willfp.eco.core.blocks.impl.UnrestrictedMaterialTestableBlock;
import com.willfp.eco.core.blocks.provider.BlockProvider;
import com.willfp.eco.core.blocks.tag.BlockTag;
import com.willfp.eco.util.NamespacedKeyUtils;
import com.willfp.eco.util.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Class to manage all custom and vanilla blocks.
 */
public final class Blocks {
    /**
     * All entities.
     */
    private static final Map<NamespacedKey, TestableBlock> REGISTRY = new ConcurrentHashMap<>();

    /**
     * Cached custom block lookups, using {@link Location}.
     */
    private static final LoadingCache<HashedBlock, Optional<TestableBlock>> CACHE = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(
                    key -> {
                        TestableBlock match = null;
                        for (TestableBlock block : REGISTRY.values()) {
                            if (block.shouldMarkAsCustom() && block.matches(key.getBlock())) {
                                match = block;
                                break;
                            }
                        }

                        return Optional.ofNullable(match);
                    }
            );

    /**
     * All block providers.
     */
    private static final Map<String, BlockProvider> PROVIDERS = new ConcurrentHashMap<>();

    /**
     * The lookup handler.
     */
    private static final BlocksLookupHandler BLOCKS_LOOKUP_HANDLER = new BlocksLookupHandler(Blocks::doParse);

    /**
     * Instance of EmptyTestableBlock.
     */
    private static final TestableBlock EMPTY_TESTABLE_BLOCK = new EmptyTestableBlock();

    /**
     * Friendly material names (without underscores, etc.)
     */
    private static final Map<String, Material> FRIENDLY_MATERIAL_NAMES = new HashMap<>();

    /**
     * All tags.
     */
    private static final Map<String, BlockTag> TAGS = new HashMap<>();

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
     * Turn an Block back into a lookup string.
     *
     * @param block The Block.
     * @return The lookup string.
     */
    @NotNull
    public static String toLookupString(@Nullable final Block block) {
        if (block == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        CustomBlock customblock = getCustomBlock(block);

        if (customblock != null) {
            builder.append(customblock.getKey());
        } else {
            builder.append(block.getType().name().toLowerCase());
        }

        return builder.toString();
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

        String base = split[0];
        boolean isTag = base.startsWith("#");

        if (isTag) {
            String tag = base.substring(1);
            BlockTag blockTag = TAGS.get(tag);

            if (blockTag == null) {
                return new EmptyTestableBlock();
            }
            return blockTag.toTestableBlock();
        }

        if (split.length == 1) {
            String blockType = args[0];
            boolean isWildcard = blockType.startsWith("*");
            if (isWildcard) {
                blockType = blockType.substring(1);
            }
            Material material = FRIENDLY_MATERIAL_NAMES.get(blockType.toLowerCase());
            if (material == null || material == Material.AIR) {
                return new EmptyTestableBlock();
            }
            return isWildcard ? new UnrestrictedMaterialTestableBlock(material) : new MaterialTestableBlock(material);
        }

        String namespace = split[0];
        String keyID = split[1];
        NamespacedKey namespacedKey = NamespacedKeyUtils.create(namespace, keyID);
        TestableBlock block = REGISTRY.get(namespacedKey);

        if (block != null) {
            return block;
        }

        BlockProvider provider = PROVIDERS.get(namespace);
        if (provider == null) {
            return new EmptyTestableBlock();
        }

        String reformattedKey = keyID.replace("__", ":");

        block = provider.provideForKey(reformattedKey);
        if (block == null) {
            return new EmptyTestableBlock();
        }

        registerCustomBlock(namespacedKey, block);
        return block;
    }

    /**
     * Get a Testable Block from a Block.
     * <p>
     * Will search for registered blocks first. If there are no matches in the registry,
     * then it will return a {@link MaterialTestableBlock} matching the block type.
     * <p>
     * Does not account for modifiers (arg parser data).
     *
     * @param block The Block.
     * @return The found Testable Block.
     */
    @NotNull
    public static TestableBlock getBlock(@Nullable final Block block) {
        if (block == null || block.getType().isAir()) {
            return new EmptyTestableBlock();
        }

        CustomBlock customBlock = getCustomBlock(block);

        if (customBlock != null) {
            return customBlock;
        }

        for (TestableBlock known : REGISTRY.values()) {
            if (known.matches(block)) {
                return known;
            }
        }
        return new MaterialTestableBlock(block.getType());
    }

    /**
     * Get if block is a custom block.
     *
     * @param block The block to check.
     * @return If is custom.
     */
    public static boolean isCustomBlock(@NotNull final Block block) {
        return getCustomBlock(block) != null;
    }

    /**
     * Get custom block from block.
     *
     * @param block The block.
     * @return The custom block, or null if not exists.
     */
    @Nullable
    public static CustomBlock getCustomBlock(@Nullable final Block block) {
        if (block == null) {
            return null;
        }

        return CACHE.get(HashedBlock.of(block)).map(Blocks::getOrWrap).orElse(null);
    }

    /**
     * Get all registered custom blocks.
     *
     * @return A set of all blocks.
     */
    public static Set<CustomBlock> getCustomBlocks() {
        return REGISTRY.values().stream().map(Blocks::getOrWrap).collect(Collectors.toSet());
    }

    /**
     * Return a CustomBlock instance for a given TestableBlock.
     *
     * @param block The block.
     * @return The CustomBlock.
     */
    @NotNull
    public static CustomBlock getOrWrap(@NotNull final TestableBlock block) {
        if (block instanceof CustomBlock) {
            return (CustomBlock) block;
        } else {
            return new CustomBlock(
                    NamespacedKeyUtils.createEcoKey("wrapped_" + NumberUtils.randInt(0, 100000)),
                    block::matches,
                    block::place
            );
        }
    }

    /**
     * Convert an array of materials to an array of testable blocks.
     *
     * @param materials The materials.
     * @return An array of functionally identical testable blocks.
     */
    @NotNull
    public static TestableBlock[] fromMaterials(@NotNull final Material... materials) {
        return Arrays.stream(materials)
                .filter(Material::isBlock)
                .map(MaterialTestableBlock::new)
                .toArray(MaterialTestableBlock[]::new);
    }

    /**
     * Convert a collection of materials into a collection of testable blocks.
     *
     * @param materials The materials.
     * @return A collection of functionally identical testable blocks.
     */
    @NotNull
    public static Collection<TestableBlock> fromMaterials(@NotNull final Iterable<Material> materials) {
        List<TestableBlock> blocks = new ArrayList<>();
        for (Material material : materials) {
            if (material.isBlock()) {
                blocks.add(new MaterialTestableBlock(material));
            }
        }

        return blocks;
    }

    /**
     * Get if an block is empty.
     *
     * @param block The block.
     * @return If empty.
     */
    public static boolean isEmpty(@Nullable final Block block) {
        return EMPTY_TESTABLE_BLOCK.matches(block);
    }

    /**
     * Get if a block matches any blocks.
     *
     * @param block          The block.
     * @param testableBlocks The testable blocks.
     * @return If matches any.
     */
    public static boolean matchesAny(@Nullable final Block block,
                                     @NotNull final Collection<TestableBlock> testableBlocks) {
        for (TestableBlock testableBlock : testableBlocks) {
            if (testableBlock.matches(block)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get all registered custom blocks.
     *
     * @param blocks         The blocks.
     * @param testableBlocks The testable blocks.
     * @return If matches any.
     */
    public static boolean matchesAny(@NotNull final Collection<Block> blocks,
                                     @NotNull final Collection<TestableBlock> testableBlocks) {
        for (Block block : blocks) {
            if (matchesAny(block, testableBlocks)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Register a new block tag.
     *
     * @param tag The tag.
     */
    public static void registerTag(@NotNull final BlockTag tag) {
        TAGS.put(tag.getIdentifier(), tag);
    }

    /**
     * Get all tags.
     *
     * @return All tags.
     */
    public static Collection<BlockTag> getTags() {
        return TAGS.values();
    }

    private Blocks() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        for (Material material : Material.values()) {
            if (!material.isBlock()) continue; // skip not blocks

            FRIENDLY_MATERIAL_NAMES.put(material.name().toLowerCase(), material);

            String oneWord = material.name().toLowerCase().replace("_", "");
            if (!FRIENDLY_MATERIAL_NAMES.containsKey(oneWord)) {
                FRIENDLY_MATERIAL_NAMES.put(oneWord, material);
            }

            String plural = material.name().toLowerCase() + "s";
            if (!FRIENDLY_MATERIAL_NAMES.containsKey(plural)) {
                FRIENDLY_MATERIAL_NAMES.put(plural, material);
            }

            String oneWordPlural = oneWord + "s";
            if (!FRIENDLY_MATERIAL_NAMES.containsKey(oneWordPlural)) {
                FRIENDLY_MATERIAL_NAMES.put(oneWordPlural, material);
            }
        }
    }
}
