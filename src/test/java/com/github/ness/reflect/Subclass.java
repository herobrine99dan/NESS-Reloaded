package com.github.ness.reflect;

public class Subclass extends Superclass {

	Integer fieldWithSameType;

	@SuppressWarnings("unused")
	private Object privateFieldWithSameName;

	public Object methodInSubclass() {
		return new Object();
	}

}
