package com.github.ness.api;

import java.util.Objects;

/**
 * The result of calling {@link AnticheatCheck#flagHack(AnticheatPlayer)}
 * 
 * @author A248
 *
 */
public final class FlagResult {

	private final ResultType resultType;
	private final Infraction infraction;
	
	private FlagResult(ResultType resultType, Infraction infraction) {
		this.resultType = resultType;
		this.infraction = infraction;
	}
	
	/**
	 * Gets the result type of this flag result
	 * 
	 * @return the result type
	 */
	public ResultType getResultType() {
		return resultType;
	}
	
	/**
	 * Gets the infraction after the flag has been applied. If {@link #getResultType()} != {@link ResultType#SUCCESS},
	 * this returns {@code null}
	 * 
	 * @return the infraction as a result of flagging or {@code null} if the result type is a failure
	 */
	public Infraction getInfraction() {
		return infraction;
	}
	
	/**
	 * Gets a result indicating the player is not being tracked by the check in question
	 * 
	 * @return the flag result
	 */
	public static FlagResult notTracking() {
		return new FlagResult(ResultType.NOT_TRACKING, null);
	}
	
	/**
	 * Gets a result indicating the event was cancelled
	 * 
	 * @return the flag result
	 */
	public static FlagResult eventCancelled() {
		return new FlagResult(ResultType.EVENT_CANCELLED, null);
	}
	
	/**
	 * Creates a successful result from an infraction
	 * 
	 * @param infraction the infraction
	 * @return the flag result
	 * @throws NullPointerException if {@code infraction} is null
	 */
	public static FlagResult success(Infraction infraction) {
		Objects.requireNonNull(infraction, "infraction");
		return new FlagResult(ResultType.SUCCESS, infraction);
	}
	
	/**
	 * Possible result state types
	 *
	 */
	public enum ResultType {
		
		/**
		 * The player is not being tracked by the check
		 * 
		 */
		NOT_TRACKING,
		/**
		 * The event ({@link PlayerFlagEvent}) was cancelled
		 * 
		 */
		EVENT_CANCELLED,
		/**
		 * A successful flag
		 * 
		 */
		SUCCESS
		
	}
	
}
