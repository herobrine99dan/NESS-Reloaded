package com.github.ness;

import java.util.Collection;
import java.util.UUID;

import com.github.ness.api.AnticheatCheck;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.NESSApi;
import com.github.ness.api.ViolationTrigger;
import com.github.ness.violation.ViolationMigratorUtil;

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
		addViolationTrigger(ViolationMigratorUtil.triggerForAction(action));
	}
	
    @Override
    public void addViolationTrigger(ViolationTrigger trigger) {
        ness.getViolationManager().addTrigger(trigger);
    }

    @SuppressWarnings("deprecation")
	@Override
    public void flagHack(com.github.ness.api.Violation violation, Player player) {
        NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(player);
        if (nessPlayer == null) {
        	return;
        }
        nessPlayer.setViolation(violation);
    }

	@Override
	public Collection<? extends AnticheatCheck> getAllChecks() {
		return ness.getCheckManager().getAllChecks();
	}

	@Override
	public Collection<? extends AnticheatPlayer> getAllPlayers() {
		return ness.getCheckManager().getAllPlayers();
	}

	@Override
	public AnticheatPlayer getPlayer(UUID uuid) {
		return ness.getCheckManager().getPlayer(uuid);
	}

}
