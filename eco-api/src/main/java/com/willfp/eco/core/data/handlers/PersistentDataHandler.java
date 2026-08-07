package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.registry.Registrable;
import com.willfp.eco.core.tuples.Pair;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles persistent data.
 * <p>
 * All reads and writes are dispatched to an internal executor, so serializers never
 * run on the calling thread.
 */
public abstract class PersistentDataHandler implements Registrable {
    /**
     * The id of the handler.
     */
    private final String id;

    /**
     * The executor that all reads and writes are dispatched to.
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
     * If write commits to disk, this method does not need to be overridden;
     * the default implementation does nothing.
     * <p>
     * This method is called asynchronously by {@link #save()}, and on the calling
     * thread by {@link #shutdown()}.
     */
    protected void doSave() {
        // Save to disk
    }

    /**
     * Get if the handler should autosave.
     *
     * @return If the handler should autosave. Defaults to true.
     */
    public boolean shouldAutosave() {
        return true;
    }

    /**
     * Save the data.
     * <p>
     * This submits {@link #doSave()} to the executor and returns immediately.
     */
    public final void save() {
        executor.submit(this::doSave);
    }

    /**
     * Read a key from persistent data.
     * <p>
     * The read runs on the executor, but this method blocks until it completes.
     *
     * @param uuid The uuid of the profile to read from.
     * @param key  The key.
     * @param <T>  The type of the key.
     * @return The value, or null if not found or if the read failed.
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
     * <p>
     * The write is submitted to the executor and this method returns immediately,
     * without waiting for it to complete.
     *
     * @param uuid  The uuid of the profile to write to.
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
     * <p>
     * The keys are read in parallel, but this method blocks until every read has
     * completed. Keys with no stored value are omitted from the result.
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

    /**
     * Load profile data, writing every entry of the serialized profile into this handler.
     * <p>
     * The writes are submitted asynchronously; use {@link #shutdown()} to await them.
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
     * <p>
     * Calls {@link #doSave()} on the calling thread, then blocks until every submitted
     * read and write has completed. If the executor has already been shut down, only
     * the save is performed.
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
