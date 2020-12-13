package com.github.ness.reflect;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedReflection implements Reflection {

	private final Reflection delegate;

	private final ConcurrentMap<Class<?>, Container> cache = new ConcurrentHashMap<>();

	CachedReflection(Reflection delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	@Override
	public <T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description) {
		return cache.computeIfAbsent(clazz, Container::new).getField(description);
	}

	@Override
	public <R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description) {
		return cache.computeIfAbsent(clazz, Container::new).getMethod(description);
	}

	private class Container {

		private final Class<?> clazz;
		final ConcurrentMap<FieldDescription<?>, FieldInvoker<?>> fields = new ConcurrentHashMap<>();
		final ConcurrentMap<MethodDescription<?>, MethodInvoker<?>> methods = new ConcurrentHashMap<>();

		Container(Class<?> clazz) {
			this.clazz = clazz;
		}

		@SuppressWarnings("unchecked")
		<T> FieldInvoker<T> getField(FieldDescription<T> description) {
			return (FieldInvoker<T>) fields.computeIfAbsent(description, (desc) -> delegate.getField(clazz, desc));
		}

		@SuppressWarnings("unchecked")
		<R> MethodInvoker<R> getMethod(MethodDescription<R> description) {
			return (MethodInvoker<R>) methods.computeIfAbsent(description, (desc) -> delegate.getMethod(clazz, desc));
		}

	}

}
