package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.registry.Registrable;
import com.willfp.eco.core.tuples.Pair;
import java.util.Collection;
import java.util.HashMap;
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
     * Get all UUIDs with saved data for a registered key.
     * <p>
     * This is a blocking operation, and is called on a refresh schedule by the leaderboard
     * service, so implementations must not deserialize stored values. A database-backed handler
     * should project only the UUID column; a handler that already holds its data in memory need
     * only read the keys.
     * <p>
     * A handler that stores each key type separately may skip the types that no
     * {@link com.willfp.eco.core.data.keys.PersistentDataKey} is currently registered for, since
     * nothing can read that data back in any case. A UUID whose only stored data belongs to an
     * unregistered type is therefore not guaranteed to be returned.
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
     * Read a key for many profiles at once.
     * <p>
     * The default implementation reads each UUID individually; handlers backed by a database
     * should override this with a single query, as it is called on a refresh schedule by the
     * leaderboard service.
     * <p>
     * UUIDs with no stored value are omitted from the result rather than mapped to the key's
     * default, so callers can tell "absent" from "stored default". For a list-typed key, a UUID
     * with no stored entries counts as having no stored value and is omitted too, rather than
     * being mapped to an empty list; every implementation must agree on this, so that readAll
     * means the same thing regardless of the storage backend.
     * <p>
     * {@link #read} returns null both for "not found" and for "the read failed", so the default
     * implementation reports a failed read as an absent UUID.
     *
     * @param uuids The uuids to read.
     * @param key   The key.
     * @param <T>   The type of the key.
     * @return The values, keyed by uuid.
     */
    @NotNull
    public <T> Map<UUID, T> readAll(@NotNull final Set<UUID> uuids,
                                    @NotNull final PersistentDataKey<T> key) {
        Map<UUID, T> values = new HashMap<>();

        for (UUID uuid : uuids) {
            T value = read(uuid, key);

            if (value == null) {
                continue;
            }

            // A list-typed key with no stored entries deserializes to an empty collection rather
            // than to null, and the database-backed overrides omit such a UUID, so it is omitted
            // here too.
            if (value instanceof Collection<?> collection && collection.isEmpty()) {
                continue;
            }

            values.put(uuid, value);
        }

        return values;
    }

    /**
     * Read several keys for many profiles at once.
     * <p>
     * The default implementation calls {@link #readAll} once per key. Handlers backed by a
     * database should override this to read every key stored in the same table in one query, as
     * the leaderboard service calls it with every ranked key on the server at once; reading them
     * one key at a time makes as many passes over the table as there are leaderboards.
     * <p>
     * The contract matches {@link #readAll} exactly, so the two can never disagree: UUIDs with no
     * stored value for a key are omitted from that key's map rather than mapped to the key's
     * default, and a list-typed key with no stored entries counts as having no stored value.
     * Every requested key is present in the returned map, mapping to an empty map if no profile
     * has a stored value for it.
     *
     * @param uuids The uuids to read.
     * @param keys  The keys to read.
     * @return The values, keyed by key and then by uuid.
     */
    @NotNull
    public Map<PersistentDataKey<?>, Map<UUID, Object>> readAllKeys(@NotNull final Set<UUID> uuids,
                                                                    @NotNull final Collection<PersistentDataKey<?>> keys) {
        Map<PersistentDataKey<?>, Map<UUID, Object>> values = new HashMap<>();

        for (PersistentDataKey<?> key : keys) {
            values.put(key, new HashMap<>(this.readAll(uuids, key)));
        }

        return values;
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
