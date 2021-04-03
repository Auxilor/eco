package com.willfp.eco.core.scheduling;

public interface TimedRunnable extends Runnable {
    /**
     * The TimedRunnable interface is generally used for repeating tasks.
     * This method is to retrieve the ticks between repetitions.
     *
     * @return The ticks between repetitions.
     */
    long getTime();
}
