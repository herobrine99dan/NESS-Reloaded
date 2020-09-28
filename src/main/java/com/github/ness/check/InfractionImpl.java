package com.github.ness.check;

import java.util.Objects;

import com.github.ness.NessPlayer;
import com.github.ness.api.AnticheatCheck;
import com.github.ness.api.Infraction;

public final class InfractionImpl implements Infraction {

	private final Check check;
	private final int count;
	private transient final String details;
	
	InfractionImpl(Check check, int count, String details) {
		this.check = Objects.requireNonNull(check, "check");
		if (count <= 0) {
			throw new IllegalArgumentException("Count must be positive");
		}
		this.count = count;
		this.details = Objects.requireNonNull(details, "details");
	}

	/**
	 * Gets the check
	 * 
	 * @return the check
	 */
	@Override
	public AnticheatCheck getCheck() {
		return check.getFactory();
	}
	
	@Override
	public NessPlayer getPlayer() {
		return check.player();
	}

	/**
	 * Gets the violation count
	 * 
	 * @return the violation count
	 */
	@Override
	public int getCount() {
		return count;
	}
	
	public String getDetails() {
		return details;
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
		if (!(object instanceof InfractionImpl)) {
			return false;
		}
		InfractionImpl other = (InfractionImpl) object;
		return count == other.count && check.equals(other.check);
	}
	
}
