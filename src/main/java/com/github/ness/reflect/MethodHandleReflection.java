package com.github.ness.reflect;

import java.util.Objects;

public class MethodHandleReflection implements Reflection {

	private final Reflection delegate;

	public MethodHandleReflection(Reflection delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	@Override
	public <T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description) {
		return new FieldInvokerWithCachedMethodHandle<>(delegate.getField(clazz, description));
	}

	@Override
	public <R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description) {
		return new MethodInvokerUsingMethodHandle<>(delegate.getMethod(clazz, description));
	}
}
