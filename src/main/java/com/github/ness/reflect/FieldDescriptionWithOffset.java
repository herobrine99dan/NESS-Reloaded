package com.github.ness.reflect;

import java.lang.reflect.Field;
import java.util.Objects;

final class FieldDescriptionWithOffset<T> implements FieldDescription<T> {

	private final FieldDescription<T> delegate;
	private final int offset;

	FieldDescriptionWithOffset(FieldDescription<T> delegate, int offset) {
		Objects.requireNonNull(delegate);
		this.delegate = (delegate instanceof FieldDescriptionWithOffset)
				? ((FieldDescriptionWithOffset<T>) delegate).delegate
				: delegate;
		this.offset = offset;
	}

	@Override
	public boolean matches(Field field) {
		return delegate.matches(field);
	}

	@Override
	public int memberOffset() {
		return offset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delegate.hashCode();
		result = prime * result + offset;
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof FieldDescriptionWithOffset)) {
			return false;
		}
		FieldDescriptionWithOffset<?> other = (FieldDescriptionWithOffset<?>) object;
		return delegate.equals(other.delegate) && offset == other.offset;
	}

	@Override
	public String toString() {
		return "FieldDescriptionWithOffset [delegate=" + delegate + ", offset=" + offset + "]";
	}

}
