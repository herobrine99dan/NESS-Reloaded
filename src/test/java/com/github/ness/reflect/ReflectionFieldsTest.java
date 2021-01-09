package com.github.ness.reflect;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.invoke.MethodHandles;

import static com.github.ness.reflect.RandomIndex.randomPositiveIndex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReflectionFieldsTest {

	private FieldInvoker<?> getField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
		return new FieldInvokerUsingCoreReflection<>(MethodHandles.lookup(), clazz.getDeclaredField(fieldName));
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void publicFields(Reflection reflection) throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "referenceField"),
				reflection.getField(Superclass.class, MemberDescriptions.forField(Object.class, "referenceField")));
		assertThrows(MemberNotFoundException.class, () -> {
			reflection.getField(Superclass.class, MemberDescriptions
					.forField(Object.class, "referenceField").withOffset(randomPositiveIndex()));
		});
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void locatePrivateFields(Reflection reflection) throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "privateReferenceField"), reflection.getField(Superclass.class,
				MemberDescriptions.forField(Boolean.class, "privateReferenceField")));
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void possiblyAmbiguousPrimitiveAndBoxedField(Reflection reflection) throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "primitiveField"),
				reflection.getField(Superclass.class, MemberDescriptions.forField(int.class, "primitiveField")));
		assertThrows(MemberNotFoundException.class, () -> {
			reflection.getField(Superclass.class, MemberDescriptions
					.forField(int.class, "primitiveField").withOffset(randomPositiveIndex()));
		});
		assertEquals(
				getField(Superclass.class, "possiblyAmbiguousBoxedField"), reflection.getField(Superclass.class,
				MemberDescriptions.forField(Integer.class, "possiblyAmbiguousBoxedField")));
		assertThrows(MemberNotFoundException.class, () -> {
			reflection.getField(Superclass.class, MemberDescriptions
					.forField(Integer.class, "possiblyAmbiguousBoxedField").withOffset(randomPositiveIndex()));
		});
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void inheritedFields(Reflection reflection) throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "referenceField"),
				reflection.getField(Subclass.class, MemberDescriptions.forField(Object.class, "referenceField")));
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void inheritedFieldWithSameType(Reflection reflection) throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Subclass.class, "fieldWithSameType"),
				reflection.getField(Subclass.class, MemberDescriptions.forField(Integer.class, "fieldWithSameType")));
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void privateFieldSameName(Reflection reflection) throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Subclass.class, "privateFieldWithSameName"), reflection.getField(Subclass.class,
				MemberDescriptions.forField(Object.class, "privateFieldWithSameName")));
		assertEquals(
				getField(Superclass.class, "privateFieldWithSameName"), reflection.getField(Subclass.class,
				MemberDescriptions.forField(Object.class, "privateFieldWithSameName").withOffset(1)));
	}
}
