package com.github.ness.violation;

import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;
import com.github.ness.api.ViolationTrigger;

@SuppressWarnings("deprecation")
public final class ViolationMigratorUtil {

	private ViolationMigratorUtil() {}
	
	/**
	 * Gets a violation from an infraction
	 * 
	 * @param infraction the infraction
	 * @return the deprecated violation
	 */
	public static Violation violationFromInfraction(Infraction infraction) {
		return new com.github.ness.api.Violation(infraction.getCheck().getCheckName(), "");
	}
	
	/**
	 * Gets a violation trigger from an action
	 * 
	 * @param action the violation action
	 * @return the violation trigger
	 */
	public static ViolationTrigger triggerForAction(ViolationAction action) {
		return new ViolationTriggerForAction(action);
	}
	
	private static class ViolationTriggerForAction implements ViolationTrigger {
		
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
