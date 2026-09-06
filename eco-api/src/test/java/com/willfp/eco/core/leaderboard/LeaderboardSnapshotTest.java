package com.willfp.eco.core.leaderboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class LeaderboardSnapshotTest {
    private static final UUID FIRST = UUID.nameUUIDFromBytes("first".getBytes());

    private static final UUID SECOND = UUID.nameUUIDFromBytes("second".getBytes());

    private static final UUID THIRD = UUID.nameUUIDFromBytes("third".getBytes());

    private static final UUID UNKNOWN = UUID.nameUUIDFromBytes("unknown".getBytes());

    private static LeaderboardSnapshot snapshot() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        entries.add(new LeaderboardEntry(1, FIRST, 300.0));
        entries.add(new LeaderboardEntry(2, SECOND, 200.0));
        entries.add(new LeaderboardEntry(3, THIRD, 100.0));

        Map<UUID, Integer> ranks = new HashMap<>();
        ranks.put(FIRST, 1);
        ranks.put(SECOND, 2);
        ranks.put(THIRD, 3);

        return new LeaderboardSnapshot(entries, ranks, 3, 1234L);
    }

    @Test
    void positionsAreOneIndexed() {
        LeaderboardSnapshot snapshot = snapshot();

        LeaderboardEntry first = snapshot.getEntry(1);
        Assertions.assertNotNull(first);
        Assertions.assertEquals(FIRST, first.getUuid());
        Assertions.assertEquals(1, first.getPosition());
        Assertions.assertEquals(300.0, first.getValue());

        LeaderboardEntry third = snapshot.getEntry(3);
        Assertions.assertNotNull(third);
        Assertions.assertEquals(THIRD, third.getUuid());
    }

    @Test
    void outOfRangePositionsAreNull() {
        LeaderboardSnapshot snapshot = snapshot();

        Assertions.assertNull(snapshot.getEntry(0));
        Assertions.assertNull(snapshot.getEntry(4));
        Assertions.assertNull(snapshot.getEntry(-1));
        Assertions.assertNull(snapshot.getEntry(Integer.MIN_VALUE));
        Assertions.assertNull(snapshot.getEntry(Integer.MAX_VALUE));
    }

    @Test
    void knownUuidsHaveRanks() {
        LeaderboardSnapshot snapshot = snapshot();

        Assertions.assertEquals(Integer.valueOf(2), snapshot.getRank(SECOND));
    }

    @Test
    void unknownUuidHasNoRank() {
        Assertions.assertNull(snapshot().getRank(UNKNOWN));
    }

    @Test
    void trackedPlayersAndBuiltAtAreExposed() {
        LeaderboardSnapshot snapshot = snapshot();

        Assertions.assertEquals(3, snapshot.getTrackedPlayers());
        Assertions.assertEquals(1234L, snapshot.getBuiltAt());
        Assertions.assertFalse(snapshot.isEmpty());
    }

    @Test
    void ranksMayExceedRetainedEntries() {
        List<LeaderboardEntry> entries = List.of(new LeaderboardEntry(1, FIRST, 300.0));

        Map<UUID, Integer> ranks = new HashMap<>();
        ranks.put(FIRST, 1);
        ranks.put(SECOND, 2);

        LeaderboardSnapshot snapshot = new LeaderboardSnapshot(entries, ranks, 2, 1L);

        Assertions.assertNull(snapshot.getEntry(2));
        Assertions.assertEquals(Integer.valueOf(2), snapshot.getRank(SECOND));
    }

    @Test
    void emptySnapshotAnswersEveryRead() {
        LeaderboardSnapshot empty = LeaderboardSnapshot.EMPTY;

        Assertions.assertEquals(0, empty.getTrackedPlayers());
        Assertions.assertEquals(0L, empty.getBuiltAt());
        Assertions.assertTrue(empty.isEmpty());
        Assertions.assertTrue(empty.getEntries().isEmpty());
        Assertions.assertNull(empty.getEntry(1));
        Assertions.assertNull(empty.getEntry(0));
        Assertions.assertNull(empty.getEntry(-1));
        Assertions.assertNull(empty.getRank(FIRST));
    }

    @Test
    void entriesListIsUnmodifiable() {
        LeaderboardSnapshot snapshot = snapshot();

        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> snapshot.getEntries().add(new LeaderboardEntry(4, UNKNOWN, 0.0))
        );
    }

    @Test
    void mutatingTheSourceCollectionsDoesNotAffectTheSnapshot() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        entries.add(new LeaderboardEntry(1, FIRST, 100.0));

        Map<UUID, Integer> ranks = new HashMap<>();
        ranks.put(FIRST, 1);

        LeaderboardSnapshot snapshot = new LeaderboardSnapshot(entries, ranks, 1, 0L);

        entries.add(new LeaderboardEntry(2, SECOND, 50.0));
        ranks.put(SECOND, 2);

        Assertions.assertEquals(1, snapshot.getEntries().size());
        Assertions.assertNull(snapshot.getEntry(2));
        Assertions.assertNull(snapshot.getRank(SECOND));
    }
}
