package com.willfp.eco.core.entities;

import com.willfp.eco.core.entities.args.EntityArgParseResult;
import com.willfp.eco.core.entities.args.EntityArgParser;
import com.willfp.eco.core.entities.impl.EmptyTestableEntity;
import com.willfp.eco.core.entities.impl.ModifiedTestableEntity;
import com.willfp.eco.core.entities.impl.SimpleTestableEntity;
import com.willfp.eco.util.NamespacedKeyUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Class to manage all custom and vanilla entities.
 */
public final class Entities {
    /**
     * All entities.
     */
    private static final Map<NamespacedKey, TestableEntity> REGISTRY = new ConcurrentHashMap<>();

    /**
     * All entity parsers.
     */
    private static final List<EntityArgParser> ARG_PARSERS = new ArrayList<>();

    /**
     * The lookup handler.
     */
    private static final EntitiesLookupHandler ENTITIES_LOOKUP_HANDLER = new EntitiesLookupHandler(Entities::doParse);

    /**
     * Register a new custom item.
     *
     * @param key  The key of the item.
     * @param item The item.
     */
    public static void registerCustomEntity(@NotNull final NamespacedKey key,
                                            @NotNull final TestableEntity item) {
        REGISTRY.put(key, item);
    }

    /**
     * Register a new arg parser.
     *
     * @param parser The parser.
     */
    public static void registerArgParser(@NotNull final EntityArgParser parser) {
        ARG_PARSERS.add(parser);
    }

    /**
     * Remove an entity.
     *
     * @param key The key of the entity.
     */
    public static void removeCustomEntity(@NotNull final NamespacedKey key) {
        REGISTRY.remove(key);
    }

    /**
     * This is the backbone of the entire eco entity system.
     * <p>
     * You can look up a TestableEntity for any type or custom entity,
     * and it will return it with any modifiers passed as parameters.
     * <p>
     * If you want to get an Entity instance from this, then just call
     * {@link TestableEntity#spawn(Location)}.
     * <p>
     * The advantages of the testable entity system are that there is the inbuilt
     * {@link TestableEntity#matches(Entity)} - this allows to check if any entity
     * is that testable entity; which may sound negligible, but actually it allows for
     * much more power and flexibility. For example, you can have an entity with an
     * extra metadata tag, extra lore lines, different display name - and it
     * will still work as long as the test passes.
     *
     * @param key The lookup string.
     * @return The testable entity, or an empty testable entity if not found.
     */
    @NotNull
    public static TestableEntity lookup(@NotNull final String key) {
        return ENTITIES_LOOKUP_HANDLER.parseKey(key);
    }

    @NotNull
    private static TestableEntity doParse(@NotNull final String[] args) {
        if (args.length == 0) {
            return new EmptyTestableEntity();
        }

        TestableEntity entity;

        String[] split = args[0].toLowerCase().split(":");

        if (split.length == 1) {
            EntityType type;
            try {
                type = EntityType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                return new EmptyTestableEntity();
            }

            if (type.getEntityClass() == null) {
                return new EmptyTestableEntity();
            }

            entity = new SimpleTestableEntity(type);
        } else {
            String namespace = split[0];
            String keyID = split[1];
            NamespacedKey namespacedKey = NamespacedKeyUtils.create(namespace, keyID);

            TestableEntity part = REGISTRY.get(namespacedKey);

            if (part == null) {
                return new EmptyTestableEntity();
            }

            entity = part;
        }

        String[] modifierArgs = Arrays.copyOfRange(args, 1, args.length);

        List<EntityArgParseResult> parseResults = new ArrayList<>();

        for (EntityArgParser argParser : ARG_PARSERS) {
            EntityArgParseResult result = argParser.parseArguments(modifierArgs);
            if (result != null) {
                parseResults.add(result);
            }
        }

        Function<Location, Entity> spawner = entity::spawn;

        if (!parseResults.isEmpty()) {
            entity = new ModifiedTestableEntity(
                    entity,
                    test -> {
                        for (EntityArgParseResult parseResult : parseResults) {
                            if (!parseResult.test().test(test)) {
                                return false;
                            }
                        }

                        return true;
                    },
                    location -> {
                        Entity spawned = spawner.apply(location);

                        for (EntityArgParseResult parseResult : parseResults) {
                            parseResult.modifier().accept(spawned);
                        }

                        return spawned;
                    }
            );
        }

        return entity;
    }

    /**
     * Get a Testable Entity from an ItemStack.
     * <p>
     * Will search for registered entity first. If there are no matches in the registry,
     * then it will return a {@link com.willfp.eco.core.entities.impl.SimpleTestableEntity} matching the entity type.
     * <p>
     * If the entity is not custom and has unknown type, this will return null.
     *
     * @param entity The Entity.
     * @return The found Testable Entity.
     */
    @Nullable
    public static TestableEntity getEntity(@Nullable final Entity entity) {
        if (entity == null) {
            return null;
        }

        TestableEntity customEntity = getEntity(entity);

        if (customEntity != null) {
            return customEntity;
        }

        for (TestableEntity known : REGISTRY.values()) {
            if (known.matches(entity)) {
                return known;
            }
        }

        if (entity.getType() == EntityType.UNKNOWN) {
            return null;
        }

        return new SimpleTestableEntity(entity.getType());
    }

    /**
     * Get if entity is a custom entity.
     *
     * @param entity The entity to check.
     * @return If is custom.
     */
    public static boolean isCustomEntity(@NotNull final Entity entity) {
        for (TestableEntity testable : REGISTRY.values()) {
            if (testable.matches(entity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all registered custom items.
     *
     * @return A set of all items.
     */
    public static Set<TestableEntity> getCustomEntities() {
        return new HashSet<>(REGISTRY.values());
    }

    private Entities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
