package com.github.ness.violation;

import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationTrigger;

@SuppressWarnings("deprecation")
final class ViolationMigratorUtil {

	private ViolationMigratorUtil() {}
	
	static Violation violationFromInfraction(Infraction infraction) {
		return new com.github.ness.api.Violation(infraction.getCheck().getCheckName(), "");
	}
	
	static class ViolationTriggerForAction implements ViolationTrigger {
		
		private final com.github.ness.api.ViolationAction action;
		
		ViolationTriggerForAction(com.github.ness.api.ViolationAction action) {
			this.action = action;
		}

		@Override
		public void trigger(AnticheatPlayer player, Infraction infraction) {
			action.actOn(player.getPlayer(), violationFromInfraction(infraction), infraction.getCount());
		}
		
	}
	
}
