package com.github.ness.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.function.Function;

public class CoreReflection implements Reflection {

	private final MethodHandles.Lookup lookup = MethodHandles.lookup();

	@Override
	public <T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description) {
		return new FieldInvokerUsingCoreReflection<>(lookup, getMember(clazz, description, Class::getDeclaredFields));
	}

	@Override
	public <R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description) {
		return new MethodInvokerUsingCoreReflection<>(lookup, getMember(clazz, description, Class::getDeclaredMethods));
	}

	private <M extends AccessibleObject & Member> M getMember(Class<?> clazz, MemberDescription<M> description,
			Function<Class<?>, M[]> getDeclaredMembers) {
		int memberIndex = description.memberOffset();
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
		throw MemberNotFoundException.INSTANCE;
	}

}
