package com.github.ness.reflect;

import java.lang.reflect.Field;
import java.util.Objects;

final class FieldDescriptionForName<T> implements FieldDescription<T> {

	private final String name;

	FieldDescriptionForName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public boolean matches(Field field) {
		return field.getName().equals(name);
	}

	@Override
	public int memberOffset() {
		return 0;
	}

	@Override
	public int hashCode() {
		return 31 + name.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this == object || object instanceof FieldDescriptionForName
				&& name.equals(((FieldDescriptionForName<?>) object).name);
	}

	@Override
	public String toString() {
		return "FieldDescriptionForName [name=" + name + "]";
	}

}
