package com.github.ness.reflect;

import java.lang.reflect.Field;
import java.util.Objects;

final class FieldDescriptionPlusType<T> implements FieldDescription<T> {

	private final FieldDescriptionForName<?> delegate;
	private final Class<T> type;

	FieldDescriptionPlusType(FieldDescriptionForName<?> delegate, Class<T> type) {
		this.delegate = Objects.requireNonNull(delegate);
		this.type = Objects.requireNonNull(type);
	}

	@Override
	public boolean matches(Field field) {
		return field.getType().equals(type) && delegate.matches(field);
	}

	@Override
	public int memberOffset() {
		return delegate.memberOffset();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delegate.hashCode();
		result = prime * result + type.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof FieldDescriptionPlusType)) {
			return false;
		}
		FieldDescriptionPlusType<?> other = (FieldDescriptionPlusType<?>) object;
		return type.equals(other.type) && delegate.equals(other.delegate);
	}

	@Override
	public String toString() {
		return "FieldDescriptionPlusType [delegate=" + delegate + ", type=" + type + "]";
	}

}
