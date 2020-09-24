package com.github.ness.api.impl;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.NESSApi;
import com.github.ness.api.ViolationTrigger;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class NESSApiImpl implements NESSApi {

    private final NESSAnticheat ness;
    
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void addViolationAction(com.github.ness.api.ViolationAction action) {
		addViolationTrigger(new ViolationTriggerForLegacyAction(action));
	}
	
	@SuppressWarnings("deprecation")
	private static class ViolationTriggerForLegacyAction implements ViolationTrigger {
		
		private final com.github.ness.api.ViolationAction action;
		
		ViolationTriggerForLegacyAction(com.github.ness.api.ViolationAction action) {
			this.action = action;
		}

		@Override
		public void trigger(Player player, Infraction infraction) {
			action.actOn(player, new com.github.ness.api.Violation(infraction.getCheck(), ""), infraction.getCount());
		}
		
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
