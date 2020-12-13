package com.github.ness.reflect;

/**
 * Entry point for obtaining reflective objects
 * 
 * @author A248
 *
 */
public interface Reflection {

	/**
	 * Finds a method matching the given description. May locate inherited and non public fields.
	 * 
	 * @param <T> the field type
	 * @param clazz the class in which to search
	 * @param description the field description
	 * @return the field invoker
	 * @throws ReflectionException if not found
	 */
	<T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description);

	/**
	 * Finds a method matching the given description. May locate inherited methods and non public methods.
	 * 
	 * @param <R> the method return type
	 * @param clazz the class in which to search
	 * @param description the method description
	 * @return the method invoker
	 * @throws ReflectionException if not found
	 */
	<R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description);

}
