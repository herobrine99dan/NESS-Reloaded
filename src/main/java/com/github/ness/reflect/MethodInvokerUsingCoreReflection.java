package com.github.ness.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

class MethodInvokerUsingCoreReflection<R> implements MethodInvoker<R> {

	private final Lookup lookup;
	private final Method method;

	MethodInvokerUsingCoreReflection(Lookup lookup, Method method) {
		this.lookup = Objects.requireNonNull(lookup);
		this.method = Objects.requireNonNull(method);
	}

	@SuppressWarnings("unchecked")
	@Override
	public R invoke(Object object, Object... arguments) {
		try {
			return (R) method.invoke(object, arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new ReflectionException(ex);
		}
	}

	@Override
	public MethodHandle unreflect() {
		try {
			return lookup.unreflect(method);
		} catch (IllegalAccessException ex) {
			throw new ReflectionException(ex);
		}
	}

	@Override
	public int hashCode() {
		return 31 + method.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this == object || object instanceof MethodInvokerUsingCoreReflection
				&& method.equals(((MethodInvokerUsingCoreReflection<?>) object).method);
	}
}
