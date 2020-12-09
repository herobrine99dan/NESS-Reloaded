package com.github.ness.reflect;

/**
 * Unchecked reflection related exception
 * 
 * @author A248
 *
 */
public class ReflectionException extends RuntimeException {

	private static final long serialVersionUID = -844751191058942455L;

	/**
	 * Creates the exception from a message
	 * 
	 * @param message the message
	 */
	public ReflectionException(String message) {
		super(message);
	}

	/**
	 * Creates the exception from a cause
	 * 
	 * @param cause the cause
	 */
	public ReflectionException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates the exception from a message and cause
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

}
