package com.github.ness.reflect;

import java.util.Arrays;

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
	 * @throws MemberNotFoundException if not found
	 */
	<T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description);

	/**
	 * Finds a method matching any of the given descriptions, in order. This is a convenience method
	 * to attempt to find a field matching any one of the descriptions.
	 *
	 * @param <T> the field type
	 * @param clazz the class in which to search
	 * @param descriptions the field descriptions to try
	 * @return the field invoker
	 * @throws MemberNotFoundException if not found
	 */
	default <T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T>...descriptions) {
		for (FieldDescription<T> description : descriptions) {
			try {
				return getField(clazz, description);
			} catch (MemberNotFoundException ignored) {}
		}
		throw new MemberNotFoundException(
				"Finding field in " + clazz.getName() + " for descriptions " + Arrays.toString(descriptions));
	}

	/**
	 * Finds a method matching the given description. May locate inherited methods and non public methods.
	 * 
	 * @param <R> the method return type
	 * @param clazz the class in which to search
	 * @param description the method description
	 * @return the method invoker
	 * @throws MemberNotFoundException if not found
	 */
	<R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description);

	/**
	 * Finds a method matching any of the given descriptions. This is a convenience method
	 * to attempt to find a method matching any one of the descriptions.
	 *
	 * @param <R> the method return type
	 * @param clazz the class in which to search
	 * @param descriptions the method descriptions to try
	 * @return the method invoker
	 * @throws MemberNotFoundException if not found
	 */
	default <R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R>...descriptions) {
		for (MethodDescription<R> description : descriptions) {
			try {
				return getMethod(clazz, description);
			} catch (MemberNotFoundException ignored) {}
		}
		throw new MemberNotFoundException(
				"Finding method in " + clazz.getName() + " for descriptions " + Arrays.toString(descriptions));
	}

}
