package com.github.ness.api;

import java.util.Objects;

/**
 * Immutable infraction
 * 
 * @author A248
 *
 */
public final class Infraction {

	private final AnticheatCheck check;
	private final int count;
	
	private Infraction(AnticheatCheck check, int count) {
		Objects.requireNonNull(check, "check");
		this.check = check;
		if (count <= 0) {
			throw new IllegalArgumentException("Count must be positive");
		}
		this.count = count;
	}
	
	/**
	 * Creates from a check and amount of violations for that check
	 * 
	 * @param check the check
	 * @param count the violation count
	 * @return the infraction
	 * @throws NullPointerException if {@code check} is null
	 * @throws IllegalArgumentException if {@count} is negative or zero
	 */
	public static Infraction of(AnticheatCheck check, int count) {
		return new Infraction(check, count);
	}

	/**
	 * Gets the check name
	 * 
	 * @return the check name
	 */
	public AnticheatCheck getCheck() {
		return check;
	}

	/**
	 * Gets the violation count
	 * 
	 * @return the violation count
	 */
	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return "Infraction [check=" + check + ", count=" + count + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + check.hashCode();
		result = prime * result + count;
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Infraction)) {
			return false;
		}
		Infraction other = (Infraction) object;
		return count == other.count && check.equals(other.check);
	}
	
}
