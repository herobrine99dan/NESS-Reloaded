package com.github.ness.check;

public enum ChecksPackage {

	COMBAT("combat"),
	AUTOCLICK("combat.autoclick"),
	MOVEMENT("movement"),
	FLYCHEAT("movement.fly"),
	PACKET("packet"),
	WORLD("world"),
	OldMovementChecks("movement.oldmovementchecks"),
	PREDICTIONS("movement.predictions"),
	MISC("misc"),
	ACQUATICUPDATEFIXES("aquaticupdatefix");

	/**
	 * Class names of checks in the required checks package. <br>
	 * Do not mutate the array
	 */
	static final String[] REQUIRED_CHECKS = { "FrequencyCinematicProcessor", "TeleportEvent" , "OnTickEvent" , "SensitivityUpdater", "OnGroundProcessor"};

	private final String prefix;

	ChecksPackage(String prefix) {
		this.prefix = prefix;
	}

	String prefix() {
		return prefix;
	}

}
