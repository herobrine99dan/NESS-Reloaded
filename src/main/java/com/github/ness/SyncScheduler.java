package com.github.ness;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.bukkit.scheduler.BukkitRunnable;

public class SyncScheduler {

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

    public void addAction(RunnableDataContainer r) {
        actions.add(r);
    }
}
