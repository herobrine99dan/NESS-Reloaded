package com.github.ness.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Objects;

class FieldInvokerImpl<T> implements FieldInvoker<T> {

	private transient final Lookup lookup;
	private final Field field;

	FieldInvokerImpl(Lookup lookup, Field field) {
		this.lookup = lookup;
		this.field = Objects.requireNonNull(field, "field");
	}

	@Override
	public void set(Object object, T value) {
		try {
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			throw new ReflectionException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Object object) {
		try {
			return (T) field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			throw new ReflectionException(ex);
		}
	}

	@Override
	public MethodHandle unreflectGetter() {
		try {
			return lookup.unreflectGetter(field);
		} catch (IllegalAccessException ex) {
			throw new ReflectionException(ex);
		}
	}

	@Override
	public int hashCode() {
		return 31 + field.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this == object || object instanceof FieldInvokerImpl
				&& field.equals(((FieldInvokerImpl<?>) object).field);
	}

}
