package com.github.ness.reflect;

import java.lang.reflect.Method;
import java.util.Objects;

final class MethodDescriptionWithOffset<R> implements MethodDescription<R> {

	private final MethodDescription<R> delegate;
	private final int offset;

	MethodDescriptionWithOffset(MethodDescription<R> delegate, int offset) {
		Objects.requireNonNull(delegate);
		this.delegate = (delegate instanceof MethodDescriptionWithOffset)
				? ((MethodDescriptionWithOffset<R>) delegate).delegate
				: delegate;
		this.offset = offset;
	}

	@Override
	public boolean matches(Method method) {
		return delegate.matches(method);
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
		if (!(object instanceof MethodDescriptionWithOffset)) {
			return false;
		}
		MethodDescriptionWithOffset<?> other = (MethodDescriptionWithOffset<?>) object;
		return delegate.equals(other.delegate) && offset == other.offset;
	}

	@Override
	public String toString() {
		return "MethodDescriptionWithOffset [delegate=" + delegate + ", offset=" + offset + "]";
	}

}
