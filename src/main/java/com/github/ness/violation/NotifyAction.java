package com.github.ness.violation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;

public class NotifyAction extends ViolationAction {
    
    public NotifyAction() {
        super(true);
    }

    @Override
    public void actOn(Player player, Violation violation, int violationCount) {
        Bukkit.broadcastMessage("Cheats!");
    }

}
