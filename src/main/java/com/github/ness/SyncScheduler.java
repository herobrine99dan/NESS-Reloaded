package com.github.ness;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.bukkit.scheduler.BukkitRunnable;

public class SyncScheduler {
    
    /**
     * The SyncScheduler class execute different RunnableDataContainer on the main Thread using a Bukkit Runnable
     * This helps to reduce lag and prevent a server crasher.
     * the Sync Scheduler is runned every 2 Ticks
     * @author herobrine99dan
     * @since 3.0
     * @see RunnableDataContainer
     */

    private final Queue<RunnableDataContainer> actions = new ArrayBlockingQueue<RunnableDataContainer>(10);
    NessAnticheat ness;

    public SyncScheduler(NessAnticheat ness) {
        this.ness = ness;
    }

    void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Runnable runnable : actions) {
                    runnable.run();
                }
                SyncScheduler.this.actions.clear();
            }
        }.runTaskTimer(ness.getPlugin(), 1L, 1L);
    }

    /**
     * Add an RunnableDataContainer to execute in the queue
     * @since 3.0.0
     * @param r
     */
    public void addAction(RunnableDataContainer r) {
        actions.add(r);
    }
}
