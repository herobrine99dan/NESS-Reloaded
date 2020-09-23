package com.github.ness.api.impl;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.NESSApi;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationTrigger;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class NESSApiImpl implements NESSApi {

    private final NESSAnticheat ness;

    @Override
    public void addViolationTrigger(ViolationTrigger trigger) {
        ness.getViolationManager().addTrigger(trigger);
    }

    @Override
    public void flagHack(Violation violation, Player player) {
        NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(player);
        if (nessPlayer == null) {
        	return;
        }
        nessPlayer.setViolation(violation);
    }

}
