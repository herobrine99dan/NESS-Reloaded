package com.github.ness.violation;

import com.github.ness.api.Infraction;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;
import com.github.ness.api.InfractionTrigger;

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
		return new Violation(infraction.getCheck().getCheckName(), "");
	}
	
	/**
	 * Gets a violation trigger from an action
	 * 
	 * @param action the violation action
	 * @return the violation trigger
	 */
	public static InfractionTrigger triggerForAction(ViolationAction action) {
		return new ViolationTriggerForAction(action);
	}
	
	private static class ViolationTriggerForAction implements InfractionTrigger {
		
		private final ViolationAction action;
		
		ViolationTriggerForAction(ViolationAction action) {
			this.action = action;
		}
		
		@Override
		public SynchronisationContext context() {
			return (action.canRunAsync()) ? SynchronisationContext.FORCE_ASYNC : SynchronisationContext.FORCE_SYNC;
		}

		@Override
		public void trigger(Infraction infraction) {
			action.actOn(infraction.getPlayer().getBukkitPlayer(), violationFromInfraction(infraction), infraction.getCount());
		}
		
	}
	
}
