package com.willfp.eco.core.data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExternalDataStoreTest {
    @Test
    void concurrentWritesAreAllVisible() throws InterruptedException {
        int threads = 8;
        int perThread = 500;
        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();

        List<Thread> workers = new java.util.ArrayList<>();
        for (int t = 0; t < threads; t++) {
            int id = t;
            Thread worker = new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        ExternalDataStore.put("key-" + id + "-" + i, i);
                    }
                } catch (Throwable e) {
                    failures.add(e);
                }
            });
            worker.setUncaughtExceptionHandler((thread, e) -> failures.add(e));
            workers.add(worker);
        }

        workers.forEach(Thread::start);
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }

        Assertions.assertTrue(failures.isEmpty(), () -> "threw: " + failures.get(0));

        int missing = 0;
        for (int t = 0; t < threads; t++) {
            for (int i = 0; i < perThread; i++) {
                if (ExternalDataStore.get("key-" + t + "-" + i, Integer.class) == null) {
                    missing++;
                }
            }
        }

        Assertions.assertEquals(0, missing);
    }
}
