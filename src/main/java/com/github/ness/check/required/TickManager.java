package com.github.ness.check.required;

import org.bukkit.Bukkit;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;

public class TickManager extends Check implements Runnable {

    public TickManager(NessPlayer nessPlayer, CheckManager manager) {
        super(TickManager.class, nessPlayer, manager);
        Bukkit.getScheduler().runTaskTimer(manager.getNess().getPlugin(), this, 0L, 1L);
    }

    @Override
    public void run() {
        this.player().setTicks(this.player().getTicks() + 1);
    }

}
