package com.willfp.eco.core.leaderboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LeaderboardRankTest {
    @Test
    void nullRankIsUnranked() {
        LeaderboardRank rank = LeaderboardRank.of(null, 200, 10, 2);

        Assertions.assertTrue(rank.isUnranked());
        Assertions.assertFalse(rank.isExact());
        Assertions.assertFalse(rank.isPercent());
        Assertions.assertNull(rank.getRank());
        Assertions.assertNull(rank.getPercent());
    }

    @Test
    void unrankedIsASharedSingleton() {
        Assertions.assertSame(LeaderboardRank.unranked(), LeaderboardRank.unranked());
        Assertions.assertSame(LeaderboardRank.unranked(), LeaderboardRank.of(null, 200, 10, 2));
    }

    @Test
    void rankInsideCutoffIsExact() {
        LeaderboardRank rank = LeaderboardRank.of(7, 200, 10, 2);

        Assertions.assertTrue(rank.isExact());
        Assertions.assertFalse(rank.isPercent());
        Assertions.assertFalse(rank.isUnranked());
        Assertions.assertNotNull(rank.getRank());
        Assertions.assertEquals(7, rank.getRank().intValue());
        Assertions.assertNull(rank.getPercent());
    }

    @Test
    void rankOnTheCutoffIsExact() {
        LeaderboardRank rank = LeaderboardRank.of(10, 200, 10, 2);

        Assertions.assertTrue(rank.isExact());
        Assertions.assertNotNull(rank.getRank());
        Assertions.assertEquals(10, rank.getRank().intValue());
    }

    @Test
    void zeroCutoffIsAlwaysExact() {
        LeaderboardRank rank = LeaderboardRank.of(4182, 1000000, 0, 2);

        Assertions.assertTrue(rank.isExact());
        Assertions.assertNotNull(rank.getRank());
        Assertions.assertEquals(4182, rank.getRank().intValue());
        Assertions.assertNull(rank.getPercent());
    }

    @Test
    void rankOutsideCutoffIsAPercentile() {
        LeaderboardRank rank = LeaderboardRank.of(50, 200, 10, 2);

        Assertions.assertTrue(rank.isPercent());
        Assertions.assertFalse(rank.isExact());
        Assertions.assertFalse(rank.isUnranked());
        Assertions.assertNull(rank.getRank());
        Assertions.assertNotNull(rank.getPercent());
        Assertions.assertEquals(25.0, rank.getPercent().doubleValue(), 1.0E-9);
    }

    @Test
    void percentileRespectsDecimalPlaces() {
        LeaderboardRank twoPlaces = LeaderboardRank.of(100, 300, 10, 2);
        Assertions.assertNotNull(twoPlaces.getPercent());
        Assertions.assertEquals(33.33, twoPlaces.getPercent().doubleValue(), 1.0E-9);

        LeaderboardRank onePlace = LeaderboardRank.of(100, 300, 10, 1);
        Assertions.assertNotNull(onePlace.getPercent());
        Assertions.assertEquals(33.3, onePlace.getPercent().doubleValue(), 1.0E-9);

        LeaderboardRank noPlaces = LeaderboardRank.of(100, 300, 10, 0);
        Assertions.assertNotNull(noPlaces.getPercent());
        Assertions.assertEquals(33.0, noPlaces.getPercent().doubleValue(), 1.0E-9);
    }

    @Test
    void noTrackedPlayersOutsideCutoffIsUnranked() {
        Assertions.assertTrue(LeaderboardRank.of(50, 0, 10, 2).isUnranked());
        Assertions.assertTrue(LeaderboardRank.of(50, -1, 10, 2).isUnranked());
    }

    @Test
    void factoriesBuildTheExpectedKinds() {
        LeaderboardRank exact = LeaderboardRank.exact(3);
        Assertions.assertTrue(exact.isExact());
        Assertions.assertNotNull(exact.getRank());
        Assertions.assertEquals(3, exact.getRank().intValue());
        Assertions.assertNull(exact.getPercent());

        LeaderboardRank percent = LeaderboardRank.percent(12.5);
        Assertions.assertTrue(percent.isPercent());
        Assertions.assertNotNull(percent.getPercent());
        Assertions.assertEquals(12.5, percent.getPercent().doubleValue(), 1.0E-9);
        Assertions.assertNull(percent.getRank());
    }
}
