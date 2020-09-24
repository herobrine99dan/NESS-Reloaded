package com.github.ness.api;

/**
 * The result of calling {@link AnticheatCheck#flagHack(AnticheatPlayer)}
 * 
 * @author A248
 *
 */
public final class FlagResult {

	private final ResultType resultType;
	private final int violations;
	
	private FlagResult(ResultType resultType, int violations) {
		this.resultType = resultType;
		this.violations = violations;
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
	 * Gets the new violation count of the player after the flag has been applied. If
	 * {@link #getResultType()} != {@link ResultType#SUCCESS}, this returns {@code -1}
	 * 
	 * @return the new violation count after flagging or {@code -1} if the result type is a failure
	 */
	public int getViolations() {
		return violations;
	}
	
	/**
	 * Gets a result indicating the player is not being tracked by the check in question
	 * 
	 * @return the flag result
	 */
	public static FlagResult notTracking() {
		return new FlagResult(ResultType.NOT_TRACKING, -1);
	}
	
	/**
	 * Gets a result indicating the event was cancelled
	 * 
	 * @return the flag result
	 */
	public static FlagResult eventCancelled() {
		return new FlagResult(ResultType.EVENT_CANCELLED, -1);
	}
	
	/**
	 * Creates from a violation count
	 * 
	 * @param violations the violation count
	 * @return the flag result
	 * @throws IllegalArgumentException if {@code violation} is negative
	 */
	public static FlagResult success(int violations) {
		if (violations < 0) {
			throw new IllegalArgumentException("Violations must be positive");
		}
		return new FlagResult(ResultType.SUCCESS, violations);
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
