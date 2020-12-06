package com.github.ness.reflect;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ReflectionTest {

	final Reflection reflection = new Reflection();

	int randomPositiveIndex() {
		return 1 + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
	}

}
