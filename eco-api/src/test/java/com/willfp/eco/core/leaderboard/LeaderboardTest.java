package com.willfp.eco.core.leaderboard;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

class LeaderboardTest {
    private static final UUID FIRST = UUID.nameUUIDFromBytes("first".getBytes());

    private static final UUID SECOND = UUID.nameUUIDFromBytes("second".getBytes());

    private static final UUID UNKNOWN = UUID.nameUUIDFromBytes("unknown".getBytes());

    /**
     * A leaderboard backed by a fixed snapshot, to exercise the default methods without a
     * running server. Only the retained top entry is kept, while both players are ranked, so the
     * capped-entries / uncapped-ranks split is covered too.
     */
    private static final class FixedLeaderboard implements Leaderboard {
        private final LeaderboardSnapshot snapshot;

        private FixedLeaderboard(@NotNull final LeaderboardSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public @NotNull String getId() {
            return "test:fixed";
        }

        @Override
        public @NotNull EcoPlugin getPlugin() {
            throw new UnsupportedOperationException("No plugin in tests");
        }

        @Override
        public @NotNull LeaderboardSnapshot getSnapshot() {
            return this.snapshot;
        }

        @Override
        public @NotNull LeaderboardRank getRank(@NotNull final UUID uuid) {
            return LeaderboardRank.of(this.snapshot.getRank(uuid), this.snapshot.getTrackedPlayers(), 0, 1);
        }

        @Override
        public @NotNull CompletableFuture<Void> refresh() {
            return CompletableFuture.completedFuture(null);
        }
    }

    private static Leaderboard leaderboard() {
        return new FixedLeaderboard(new LeaderboardSnapshot(
                List.of(new LeaderboardEntry(1, FIRST, 300.0)),
                Map.of(FIRST, 1, SECOND, 2),
                2,
                1234L
        ));
    }

    @Test
    void getTopReadsFromTheSnapshot() {
        LeaderboardEntry top = leaderboard().getTop(1);

        Assertions.assertNotNull(top);
        Assertions.assertEquals(FIRST, top.getUuid());
        Assertions.assertEquals(300.0, top.getValue());
    }

    @Test
    void getTopIsNullOutsideTheRetainedEntries() {
        Leaderboard leaderboard = leaderboard();

        Assertions.assertNull(leaderboard.getTop(0));
        Assertions.assertNull(leaderboard.getTop(2));
        Assertions.assertNull(leaderboard.getTop(-1));
    }

    @Test
    void getPositionIsUncapped() {
        Leaderboard leaderboard = leaderboard();

        Assertions.assertEquals(1, leaderboard.getPosition(FIRST));

        // Not a retained entry, but still ranked.
        Assertions.assertNull(leaderboard.getTop(2));
        Assertions.assertEquals(2, leaderboard.getPosition(SECOND));
    }

    @Test
    void getPositionIsNullForUnrankedPlayers() {
        Assertions.assertNull(leaderboard().getPosition(UNKNOWN));
    }

    @Test
    void emptySnapshotAnswersEveryRead() {
        Leaderboard leaderboard = new FixedLeaderboard(LeaderboardSnapshot.EMPTY);

        Assertions.assertNull(leaderboard.getTop(1));
        Assertions.assertNull(leaderboard.getPosition(FIRST));
        Assertions.assertTrue(leaderboard.getRank(FIRST).isUnranked());
    }
}
