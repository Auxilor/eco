package com.willfp.eco.core.data.handlers;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PersistentDataHandlerTest {
    private static final int THREADS = 2;

    private static final int TASKS = 16;

    @Test
    void executorNeverRunsMoreTasksThanItHasThreads() throws InterruptedException {
        AtomicInteger running = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(TASKS);

        PersistentDataHandler handler = new PersistentDataHandler("test", THREADS) {
            @Override
            public Set<UUID> getSavedUUIDs() {
                return Set.of();
            }

            @Override
            protected void doSave() {
                peak.accumulateAndGet(running.incrementAndGet(), Math::max);

                try {
                    // Hold the thread so overlapping tasks are actually concurrent
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    running.decrementAndGet();
                    done.countDown();
                }
            }
        };

        for (int i = 0; i < TASKS; i++) {
            handler.save();
        }

        Assertions.assertTrue(done.await(30, TimeUnit.SECONDS), "tasks did not complete");

        // An unbounded pool starts a thread per queued task, so a bulk write ends up with
        // thousands of threads fighting over a handful of connections.
        Assertions.assertTrue(
                peak.get() <= THREADS,
                () -> "ran " + peak.get() + " tasks at once with only " + THREADS + " threads"
        );
    }
}
