package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.registry.Registrable;
import com.willfp.eco.core.tuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Handles persistent data.
 */
public abstract class PersistentDataHandler implements Registrable {
    /**
     * The id.
     */
    private final String id;

    /**
     * The executor.
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Create a new persistent data handler.
     *
     * @param id The id.
     */
    protected PersistentDataHandler(@NotNull final String id) {
        this.id = id;
    }

    /**
     * Get all UUIDs with saved data.
     * <p>
     * This is a blocking operation.
     *
     * @return All saved UUIDs.
     */
    public abstract Set<UUID> getSavedUUIDs();

    /**
     * Save to disk.
     * <p>
     * If write commits to disk, this method does not need to be overridden.
     * <p>
     * This method is called asynchronously.
     */
    protected void doSave() {
        // Save to disk
    }

    /**
     * If the handler should autosave.
     *
     * @return If the handler should autosave.
     */
    public boolean shouldAutosave() {
        return true;
    }

    /**
     * Save the data.
     */
    public final void save() {
        executor.submit(this::doSave);
    }

    /**
     * Read a key from persistent data.
     *
     * @param uuid The uuid.
     * @param key  The key.
     * @param <T>  The type of the key.
     * @return The value, or null if not found.
     */
    @Nullable
    public final <T> T read(@NotNull final UUID uuid,
                            @NotNull final PersistentDataKey<T> key) {
        DataTypeSerializer<T> serializer = key.getType().getSerializer(this);
        Future<T> future = executor.submit(() -> serializer.readAsync(uuid, key));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write a key to persistent data.
     *
     * @param uuid  The uuid.
     * @param key   The key.
     * @param value The value.
     * @param <T>   The type of the key.
     */
    public final <T> void write(@NotNull final UUID uuid,
                                @NotNull final PersistentDataKey<T> key,
                                @NotNull final T value) {
        DataTypeSerializer<T> serializer = key.getType().getSerializer(this);
        executor.submit(() -> serializer.writeAsync(uuid, key, value));
    }

    /**
     * Serialize profile.
     *
     * @param uuid The uuid to serialize.
     * @param keys The keys to serialize.
     * @return The serialized data.
     */
    @NotNull
    public final SerializedProfile serializeProfile(@NotNull final UUID uuid,
                                                    @NotNull final Set<PersistentDataKey<?>> keys) {
        Map<PersistentDataKey<?>, CompletableFuture<Object>> futures = keys.stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> CompletableFuture.supplyAsync(() -> read(uuid, key), executor)
                ));

        Map<PersistentDataKey<?>, Object> data = futures.entrySet().stream()
                .map(entry -> new Pair<PersistentDataKey<?>, Object>(entry.getKey(), entry.getValue().join()))
                .filter(entry -> entry.getSecond() != null)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        return new SerializedProfile(uuid, data);
    }

    /**`
     * Load profile data.
     *
     * @param profile The profile.
     */
    @SuppressWarnings("unchecked")
    public final void loadSerializedProfile(@NotNull final SerializedProfile profile) {
        for (Map.Entry<PersistentDataKey<?>, Object> entry : profile.data().entrySet()) {
            PersistentDataKey<?> key = entry.getKey();
            Object value = entry.getValue();

            // This cast is safe because the data is serialized
            write(profile.uuid(), (PersistentDataKey<? super Object>) key, value);
        }
    }

    /**
     * Save and shutdown the handler.
     *
     * @throws InterruptedException If the writes could not be awaited.
     */
    public final void shutdown() throws InterruptedException {
        doSave();

        if (executor.isShutdown()) {
            return;
        }

        executor.shutdown();
        while (!executor.awaitTermination(2, TimeUnit.MINUTES)) {
            // Wait
        }
    }

    @Override
    @NotNull
    public final String getID() {
        return id;
    }
}
