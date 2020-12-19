package com.github.ness.reflect;

import java.lang.reflect.Field;
import java.util.Objects;

final class FieldDescriptionTypeAndOffset<T> implements FieldDescription<T> {

	private final Class<T> type;
	private final int memberOffset;

	FieldDescriptionTypeAndOffset(Class<T> type, int memberOffset) {
		this.type = Objects.requireNonNull(type);
		this.memberOffset = memberOffset;
	}

	@Override
	public boolean matches(Field field) {
		return field.getType().equals(type);
	}

	@Override
	public int memberOffset() {
		return memberOffset;
	}

	@Override
	public FieldDescription<T> withOffset(int memberOffset) {
		if (memberOffset == memberOffset()) {
			return this;
		}
		return new FieldDescriptionTypeAndOffset<>(type, memberOffset);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FieldDescriptionTypeAndOffset<?> that = (FieldDescriptionTypeAndOffset<?>) o;
		return memberOffset == that.memberOffset && type.equals(that.type);
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + memberOffset;
		return result;
	}

	@Override
	public String toString() {
		return "FieldDescriptionTypeAndOffset{" +
				"type=" + type +
				", memberOffset=" + memberOffset +
				'}';
	}
}
