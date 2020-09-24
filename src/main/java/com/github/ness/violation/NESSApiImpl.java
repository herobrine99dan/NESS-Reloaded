package com.github.ness.violation;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.NESSApi;
import com.github.ness.api.ViolationTrigger;

import org.bukkit.entity.Player;

final class NESSApiImpl implements NESSApi {

    private final NESSAnticheat ness;
    
    NESSApiImpl(NESSAnticheat ness) {
    	this.ness = ness;
    }
    
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void addViolationAction(com.github.ness.api.ViolationAction action) {
		addViolationTrigger(new ViolationMigratorUtil.ViolationTriggerForAction(action));
	}
	
    @Override
    public void addViolationTrigger(ViolationTrigger trigger) {
        ness.getViolationManager().addTrigger(trigger);
    }

    @Override
    public void flagHack(@SuppressWarnings("deprecation") com.github.ness.api.Violation violation, Player player) {
        NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(player);
        if (nessPlayer == null) {
        	return;
        }
        nessPlayer.setViolation(violation);
    }

}
