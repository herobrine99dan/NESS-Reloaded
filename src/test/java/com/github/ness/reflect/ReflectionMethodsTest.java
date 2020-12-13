package com.github.ness.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.Test;

public class ReflectionMethodsTest extends ReflectionTest {

	private MethodInvoker<?> getMethod(Class<?> clazz, String methodName, Class<?>...parameterTypes)
			throws NoSuchMethodException, SecurityException {
		return new MethodInvokerUsingCoreReflection<>(MethodHandles.lookup(), clazz.getDeclaredMethod(methodName, parameterTypes));
	}

	@Test
	public void publicMethods() throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "newObject"),
				reflection.getMethod(Superclass.class, MemberDescriptions.forMethod(Object.class, "newObject")));
		assertThrows(ReflectionException.class, () -> {
			reflection.getMethod(Superclass.class, MemberDescriptions
					.forMethod(Object.class, "newObject").withOffset(randomPositiveIndex()));
		});
	}

	@Test
	public void privateMethods() throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "lengthOf", String.class),
				reflection.getMethod(Superclass.class, MemberDescriptions.forMethod(int.class, "lengthOf", String.class)));
	}

	@Test
	public void inheritedMethods() throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "newObject"),
				reflection.getMethod(Subclass.class, MemberDescriptions.forMethod(Object.class, "newObject")));
	}

}
