package com.github.ness.reflect;

import java.lang.reflect.Field;

public class FieldDescription<T> implements MemberDescription<Field> {

	private final String name;
	private final Class<T> type;

	private FieldDescription(String name, Class<T> type) {
		this.name = name;
		this.type = type;
	}

	public static <T> FieldDescription<T> create(String name, Class<T> type) {
		return new FieldDescription<>(name, type);
	}

	@Override
	public boolean matches(Field field) {
		return field.getName().equals(name) && field.getType().equals(type);
	}

	@Override
	public String toString() {
		return "FieldDescription [name=" + name + ", type=" + type + "]";
	}

}
