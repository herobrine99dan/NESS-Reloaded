package com.github.ness.api;

/**
 * Old violation object
 * 
 * @author A248
 *
 * @deprecated {@link Infraction} is closest replacement for this class
 */
@Deprecated
public class Violation {

	private final String check;
	private final String details;

	public Violation(String check, String details) {
		this.check = check;
		this.details = details;
	}

	public String getCheck() {
		return check;
	}

	public String getDetails() {
		return details;
	}

}
