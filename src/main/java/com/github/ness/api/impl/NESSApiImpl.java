package com.github.ness.api.impl;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.NESSApi;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class NESSApiImpl implements NESSApi {

    private final NESSAnticheat ness;

    @Override
    public void addViolationAction(ViolationAction action) {
        ness.getViolationManager().addAction(action);
    }

    @Override
    public void flagHack(Violation violation, Player player) {
        NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(player);
        if (nessPlayer == null) {
        	return;
        }
        nessPlayer.setViolation(violation, null);
    }

}
