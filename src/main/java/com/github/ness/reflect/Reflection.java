package com.github.ness.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for obtaining reflective objects
 * 
 * @author A248
 *
 */
public class Reflection {

	private final MethodHandles.Lookup lookup = MethodHandles.lookup();

	private List<Class<?>> getHierarchy(Class<?> clazz) {
		List<Class<?>> hierarchy = new ArrayList<>();
		Class<?> currentClass = clazz;
		while (!currentClass.equals(Object.class)) {
			hierarchy.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		return hierarchy;
	}

	private ReflectionException notFound(Class<?> clazz, MemberDescription<?> description, int memberIndex) {
		throw new ReflectionException("No member found in " + clazz.getName() + " for description "
				+ description + " and member index " + memberIndex);
	}

	/**
	 * Finds a method matching the given description. May locate inherited methods and non public methods. <br>
	 * <br>
	 * The first {@code memberIndex} methods matching the given method description will be skipped.
	 * If zero, the first matching method is returned. Methods in subclasses are searched first.
	 * 
	 * @param <R> the method return type
	 * @param clazz the class in which to search
	 * @param description the method description
	 * @param memberIndex the amount of matching methods to initially skip
	 * @return the method invoker
	 * @throws ReflectionException if not found
	 */
	public <R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description, int memberIndex) {
		for (Class<?> clazzToSearch : getHierarchy(clazz)) {
			for (Method method : clazzToSearch.getDeclaredMethods()) {
				if (!description.matches(method)) {
					continue;
				}
				if (memberIndex-- <= 0) {
					method.setAccessible(true);
					return new MethodInvokerImpl<>(lookup, method);
				}
			}
		}
		throw notFound(clazz, description, memberIndex);
	}

	/**
	 * Finds a method matching the given description. May locate inherited and non public fields. <br>
	 * <br>
	 * The first {@code memberIndex} fields matching the given field description will be skipped.
	 * If zero, the first matching field is returned. Fields in subclasses are searched first.
	 * 
	 * @param <T> the field type
	 * @param clazz the class in which to search
	 * @param description the field description
	 * @param memberIndex the amount of matching fields to initially skip
	 * @return the field invoker
	 * @throws ReflectionException if not found
	 */
	public <T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description, int memberIndex) {
		for (Class<?> clazzToSearch : getHierarchy(clazz)) {
			for (Field field : clazzToSearch.getDeclaredFields()) {
				if (!description.matches(field)) {
					continue;
				}
				if (memberIndex-- <= 0) {
					field.setAccessible(true);
					return new FieldInvokerImpl<>(lookup, field);
				}
			}
		}
		throw notFound(clazz, description, memberIndex);
	}

}
