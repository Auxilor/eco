package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.data.ServerProfile;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.data.keys.PersistentDataKeyType;

/**
 * Utilities / API methods for the server.
 */
public final class ServerUtils {
    /**
     * The data key on the server profile which holds the top player local id.
     * The default value is 1 to allow gradual migration of BlockUtils isPlayerPlaced values.
     */
    private static final PersistentDataKey<Integer> TOP_LOCAL_ID = new PersistentDataKey<>(
            NamespacedKeyUtils.createEcoKey("top_player_local_id"),
            PersistentDataKeyType.INT,
            1
    );

    /**
     * Get the current server TPS.
     *
     * @return The TPS.
     */
    public static double getTps() {
        double tps = Eco.get().getTPS();

        if (tps > 20) {
            return 20;
        } else {
            return tps;
        }
    }

    
    /**
     * Generates a local ID for a player by incrementing the top value.
     * Local IDs are more compact than UUIDs, but are local to our storage backing. 
     * 
     * @return The player local ID.
     */
    public static Integer generateLocalID() {
        ServerProfile profile = ServerProfile.load();
        Integer local_id = profile.read(TOP_LOCAL_ID);
        local_id += 1;
        profile.write(TOP_LOCAL_ID, local_id);
        return local_id;
    }

    private ServerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
