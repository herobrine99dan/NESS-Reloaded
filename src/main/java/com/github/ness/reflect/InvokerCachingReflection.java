package com.github.ness.reflect;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

public class InvokerCachingReflection implements Reflection {

	private final Reflection delegate;
	private final LoadingCache<Class<?>, Container> cache;

	public InvokerCachingReflection(Reflection delegate) {
		this.delegate = Objects.requireNonNull(delegate);
		cache = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).build(Container::new);
	}

	@Override
	public <T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description) {
		return cache.get(clazz).getField(description);
	}

	@Override
	public <R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description) {
		return cache.get(clazz).getMethod(description);
	}

	private class Container {

		private final Class<?> clazz;
		private final ConcurrentMap<FieldDescription<?>, FieldInvoker<?>> fields = new ConcurrentHashMap<>();
		private final ConcurrentMap<MethodDescription<?>, MethodInvoker<?>> methods = new ConcurrentHashMap<>();

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
