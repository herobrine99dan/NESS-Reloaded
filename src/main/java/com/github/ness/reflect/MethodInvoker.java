package com.github.ness.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * Reflective invoker for a method
 * 
 * @author A248
 *
 * @param <R> the return type of the method
 */
public interface MethodInvoker<R> {

	R invoke(Object object, Object...arguments);

	/**
	 * Obtains a method handle which may be used to more efficiently invoke this method
	 * 
	 * @return a method handle for the method
	 */
	MethodHandle unreflect();

	/**
	 * Obtains the method
	 *
	 * @return the method
	 */
	Method reflect();

	/**
	 * Whether this method invoker is equal to another object. Should be implemented using {@link #reflect()}
	 * such that any 2 method invokers are equal if they reflect to the same method
	 *
	 * @param object the object to determine equality with
	 * @return true if equal, false otherwise
	 */
	@Override
	boolean equals(Object object);

}
