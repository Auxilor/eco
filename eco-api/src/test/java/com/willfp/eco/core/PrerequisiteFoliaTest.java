package com.willfp.eco.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrerequisiteFoliaTest {
    @Test
    void notMetOffFolia() {
        Assertions.assertFalse(Prerequisite.HAS_FOLIA.isMet());
    }

    @Test
    void hasADescription() {
        Assertions.assertFalse(Prerequisite.HAS_FOLIA.getDescription().isEmpty());
    }
}
