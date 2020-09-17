package com.github.ness.utility;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Objects;

/**
 * Unchecked wrapper for {@link ReflectiveOperationException}
 * 
 * @author A248
 *
 */
public class UncheckedReflectiveOperationException extends RuntimeException {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -1017060326340536945L;
	
	/**
	 * Creates the exception from a cause
	 * 
	 * @param cause the cause
	 */
	public UncheckedReflectiveOperationException(ReflectiveOperationException cause) {
		super(Objects.requireNonNull(cause, "cause"));
	}
	
	/**
	 * Creates the exception from a message and cause
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public UncheckedReflectiveOperationException(String message, ReflectiveOperationException cause) {
		super(message, Objects.requireNonNull(cause, "cause"));
	}
	
	@Override
	public synchronized ReflectiveOperationException getCause() {
		return (ReflectiveOperationException) super.getCause();
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		Throwable cause = super.getCause();
		if (!(cause instanceof ReflectiveOperationException)) {
			throw new InvalidObjectException("Cause must be a ReflectiveOperationException");
		}
	}

}
