package com.github.ness.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ReflectionFieldsTest extends ReflectionTest {

	private FieldInvoker<?> getField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
		return new FieldInvokerImpl<>(null, clazz.getDeclaredField(fieldName));
	}

	@Test
	public void publicFields() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "referenceField"),
				reflection.getField(Superclass.class, FieldDescription.create("referenceField", Object.class), 0));
		assertThrows(ReflectionException.class, () -> {
			reflection.getField(Superclass.class, FieldDescription.create("referenceField", Object.class),
					randomPositiveIndex());
		});
	}

	@Test
	public void locatePrivateFields() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "privateReferenceField"), reflection.getField(Superclass.class,
				FieldDescription.create("privateReferenceField", Boolean.class), 0));
	}

	@Test
	public void possiblyAmbiguousPrimitiveAndBoxedField() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "primitiveField"),
				reflection.getField(Superclass.class, FieldDescription.create("primitiveField", int.class), 0));
		assertThrows(ReflectionException.class, () -> {
			reflection.getField(Superclass.class, FieldDescription.create("primitiveField", int.class),
					randomPositiveIndex());
		});
		assertEquals(
				getField(Superclass.class, "possiblyAmbiguousBoxedField"), reflection.getField(Superclass.class,
				FieldDescription.create("possiblyAmbiguousBoxedField", Integer.class), 0));
		assertThrows(ReflectionException.class, () -> {
			reflection.getField(Superclass.class, FieldDescription.create("possiblyAmbiguousBoxedField", Integer.class),
					randomPositiveIndex());
		});
	}

	@Test
	public void inheritedFields() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Superclass.class, "referenceField"),
				reflection.getField(Subclass.class, FieldDescription.create("referenceField", Object.class), 0));
	}

	@Test
	public void inheritedFieldWithSameType() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Subclass.class, "fieldWithSameType"),
				reflection.getField(Subclass.class, FieldDescription.create("fieldWithSameType", Integer.class), 0));
	}

	@Test
	public void privateFieldSameName() throws NoSuchFieldException, SecurityException {
		assertEquals(
				getField(Subclass.class, "privateFieldWithSameName"),
				reflection.getField(Subclass.class, FieldDescription.create("privateFieldWithSameName", Object.class), 0));
		assertEquals(
				getField(Superclass.class, "privateFieldWithSameName"),
				reflection.getField(Subclass.class, FieldDescription.create("privateFieldWithSameName", Object.class), 1));
	}
}
