package com.github.ness.reflect;

/**
 * Factory methods for creating member descriptions
 * 
 * @author A248
 *
 */
public final class MemberDescriptions {

	private MemberDescriptions() {}

	public static <T> FieldDescription<T> forField(Class<T> type, String name) {
		return new FieldDescriptionPlusType<>(
				new FieldDescriptionForName<>(name), type);
	}

	public static FieldDescription<?> forField(String name) {
		return new FieldDescriptionForName<>(name);
	}

	public static <T> FieldDescription<T> forField(Class<T> type, int memberOffset) {
		return new FieldDescriptionTypeAndOffset<>(type, memberOffset);
	}

	public static <R> MethodDescription<R> forMethod(Class<R> returnType, String name, Class<?>... parameterTypes) {
		return new MethodDescriptionPlusReturnType<>(
				new MethodDescriptionSimple<>(name, parameterTypes, false), returnType);
	}

	public static MethodDescription<?> forMethod(String name, Class<?>... parameterTypes) {
		return new MethodDescriptionSimple<>(name, parameterTypes, false);
	}

	public static <R> MethodDescription<R> forStaticMethod(Class<R> returnType, String name,
			Class<?>... parameterTypes) {
		return new MethodDescriptionPlusReturnType<>(
				new MethodDescriptionSimple<>(name, parameterTypes, true), returnType);
	}

	public static MethodDescription<?> forStaticMethod(String name, Class<?>... parameterTypes) {
		return new MethodDescriptionSimple<>(name, parameterTypes, true);
	}

}
