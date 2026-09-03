package com.willfp.eco.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FoliaSupportTest {
    @Test
    void supportedOffFolia() {
        Assertions.assertFalse(FoliaSupport.isUnsupported("Anything"));
    }

    @Test
    void requireDoesNotThrowOffFolia() {
        Assertions.assertDoesNotThrow(() -> FoliaSupport.requireSupported("Anything"));
    }
}
