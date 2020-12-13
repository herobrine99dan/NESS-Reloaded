package com.github.ness.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.Test;

public class ReflectionFieldsTest extends ReflectionTest {

	private FieldInvoker<?> getField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
		return new FieldInvokerUsingCoreReflection<>(MethodHandles.lookup(), clazz.getDeclaredField(fieldName));
	}

	@Test
	public void publicFields() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "referenceField"),
				reflection.getField(Superclass.class, MemberDescriptions.forField("referenceField", Object.class)));
		assertThrows(ReflectionException.class, () -> {
			reflection.getField(Superclass.class, MemberDescriptions
					.forField("referenceField", Object.class).withOffset(randomPositiveIndex()));
		});
	}

	@Test
	public void locatePrivateFields() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "privateReferenceField"), reflection.getField(Superclass.class,
				MemberDescriptions.forField("privateReferenceField", Boolean.class)));
	}

	@Test
	public void possiblyAmbiguousPrimitiveAndBoxedField() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "primitiveField"),
				reflection.getField(Superclass.class, MemberDescriptions.forField("primitiveField", int.class)));
		assertThrows(ReflectionException.class, () -> {
			reflection.getField(Superclass.class, MemberDescriptions
					.forField("primitiveField", int.class).withOffset(randomPositiveIndex()));
		});
		assertEquals(
				getField(Superclass.class, "possiblyAmbiguousBoxedField"), reflection.getField(Superclass.class,
				MemberDescriptions.forField("possiblyAmbiguousBoxedField", Integer.class)));
		assertThrows(ReflectionException.class, () -> {
			reflection.getField(Superclass.class, MemberDescriptions
					.forField("possiblyAmbiguousBoxedField", Integer.class).withOffset(randomPositiveIndex()));
		});
	}

	@Test
	public void inheritedFields() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "referenceField"),
				reflection.getField(Subclass.class, MemberDescriptions.forField("referenceField", Object.class)));
	}

	@Test
	public void inheritedFieldWithSameType() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Subclass.class, "fieldWithSameType"),
				reflection.getField(Subclass.class, MemberDescriptions.forField("fieldWithSameType", Integer.class)));
	}

	@Test
	public void privateFieldSameName() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Subclass.class, "privateFieldWithSameName"), reflection.getField(Subclass.class,
				MemberDescriptions.forField("privateFieldWithSameName", Object.class)));
		assertEquals(
				getField(Superclass.class, "privateFieldWithSameName"), reflection.getField(Subclass.class,
				MemberDescriptions.forField("privateFieldWithSameName", Object.class).withOffset(1)));
	}
}
