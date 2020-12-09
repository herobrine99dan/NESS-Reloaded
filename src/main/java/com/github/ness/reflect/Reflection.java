package com.github.ness.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.function.Function;

/**
 * Entry point for obtaining reflective objects
 * 
 * @author A248
 *
 */
public class Reflection {

	private final MethodHandles.Lookup lookup = MethodHandles.lookup();

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
		return new MethodInvokerImpl<>(lookup, getMember(clazz, description, memberIndex, Class::getDeclaredMethods));
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
		return new FieldInvokerImpl<>(lookup, getMember(clazz, description, memberIndex, Class::getDeclaredFields));
	}

	private <M extends AccessibleObject & Member> M getMember(Class<?> clazz, MemberDescription<M> description,
			int memberIndex, Function<Class<?>, M[]> getDeclaredMembers) {
		Class<?> currentClass = clazz;
		while (!currentClass.equals(Object.class)) {

			for (M member : getDeclaredMembers.apply(currentClass)) {
				if (!description.matches(member)) {
					continue;
				}
				if (memberIndex-- <= 0) {
					member.setAccessible(true);
					return member;
				}
			}

			currentClass = currentClass.getSuperclass();
		}
		throw new ReflectionException("No member found in " + clazz.getName() + " for description "
				+ description + " and member index " + memberIndex);
	}

}
