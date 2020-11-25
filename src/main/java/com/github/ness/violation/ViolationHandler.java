package com.github.ness.violation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ness.NessAnticheat;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;
import com.github.ness.check.Check;

public class ViolationHandler {

    NessAnticheat ness;
    List<ViolationAction> actions;

    public ViolationHandler(NessAnticheat ness) {
        this.ness = ness;
        this.actions = new ArrayList<ViolationAction>();
        this.actions.add(new PunishAction());
        this.actions.add(new NotifyAction());
    }

    public void addAction(ViolationAction action) {
        this.actions.add(action);
    }

    public void onCheat(Player player, Violation violation, Check check) {
        List<ViolationAction> syncActions = new ArrayList<ViolationAction>();
        List<ViolationAction> asyncActions = new ArrayList<ViolationAction>();
        for (ViolationAction action : actions) {
            if (action.canRunAsync()) {
                asyncActions.add(action);
            } else {
                syncActions.add(action);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ViolationAction action : syncActions) {
                    action.actOn(player, violation);
                }
            }
        }.runTaskLater(ness.getPlugin(), 20);
        for (ViolationAction action : asyncActions) {
            action.actOn(player, violation);
        }
    }
}
