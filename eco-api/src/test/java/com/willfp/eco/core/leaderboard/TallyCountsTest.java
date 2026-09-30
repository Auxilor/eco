package com.willfp.eco.core.leaderboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class TallyCountsTest {
    private static TallyCounts counts() {
        Map<String, Integer> buckets = new HashMap<>();
        buckets.put("miner", 3);
        buckets.put("farmer", 2);

        return TallyCounts.of(buckets);
    }

    @Test
    void knownBucketsReadBackTheirCount() {
        TallyCounts counts = counts();

        Assertions.assertEquals(3, counts.get("miner"));
        Assertions.assertEquals(2, counts.get("farmer"));
    }

    @Test
    void unknownBucketIsZero() {
        Assertions.assertEquals(0, counts().get("fisher"));
    }

    @Test
    void totalIsTheSumOfEveryBucket() {
        Assertions.assertEquals(5, counts().getTotal());
    }

    @Test
    void totalCountsMembershipsNotDistinctPlayers() {
        // The same player being in both buckets is not visible here, and must not be: each
        // count means "players in this bucket", so the total is a sum of memberships.
        Map<String, Integer> buckets = new HashMap<>();
        buckets.put("miner", 1);
        buckets.put("farmer", 1);

        Assertions.assertEquals(2, TallyCounts.of(buckets).getTotal());
    }

    @Test
    void emptyCountsAnswerEveryRead() {
        Assertions.assertEquals(0, TallyCounts.EMPTY.get("miner"));
        Assertions.assertEquals(0, TallyCounts.EMPTY.getTotal());
        Assertions.assertTrue(TallyCounts.EMPTY.getBuckets().isEmpty());
    }

    @Test
    void bucketsAreUnmodifiable() {
        Map<String, Integer> buckets = counts().getBuckets();

        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> buckets.put("fisher", 1)
        );
    }

    @Test
    void mutatingTheSourceMapDoesNotAffectTheCounts() {
        Map<String, Integer> source = new HashMap<>();
        source.put("miner", 3);

        TallyCounts counts = TallyCounts.of(source);

        source.put("farmer", 9);
        source.put("miner", 100);

        Assertions.assertEquals(3, counts.get("miner"));
        Assertions.assertEquals(0, counts.get("farmer"));
        Assertions.assertEquals(3, counts.getTotal());
        Assertions.assertEquals(1, counts.getBuckets().size());
    }
}
