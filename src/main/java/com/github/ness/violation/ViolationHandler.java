package com.github.ness.violation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.RunnableDataContainer;
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

    public void onCheat(Player player, Violation violation, int violationCount, Check check) {
        for (ViolationAction action : actions) {
            if (action.canRunAsync()) {
                action.actOn(player, violation, violationCount);
            } else {// TODO Fix this
                ViolationRunnableDataContainer runnable = new ViolationRunnableDataContainer(null, player, violation,
                        violationCount, action);
                action.actOn(player, violation, violationCount);
                ness.getSyncScheduler().addAction(runnable);
            }
        }
    }

    class ViolationRunnableDataContainer extends RunnableDataContainer {

        public ViolationRunnableDataContainer(NessPlayer nessPlayer, Object... objects) {
            super(nessPlayer, objects);
        }

        @Override
        public void run() {
            Player player = (Player) this.getArray()[0];
            Violation violation = (Violation) this.getArray()[1];
            int violationCount = (int) this.getArray()[2];
            ViolationAction action = (ViolationAction) this.getArray()[3];
            action.actOn(player, violation, violationCount);
        }

    }

}
