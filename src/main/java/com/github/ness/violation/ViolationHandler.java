package com.github.ness.violation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

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
    
    public void onCheat(Player player, Violation violation, int violationCount, Check check) {
        for(ViolationAction action : actions) {
            action.actOn(player, violation, violationCount);
        }
    }

}
