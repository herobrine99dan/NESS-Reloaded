package com.github.ness.reflect;

public class Superclass {

	public Object referenceField;
	
	@SuppressWarnings("unused")
	private Boolean privateReferenceField;
	
	int primitiveField;
	
	Integer possiblyAmbiguousBoxedField;

	@SuppressWarnings("unused")
	private Object privateFieldWithSameName;
	
	public Object newObject() {
		return new Object();
	}
	
	@SuppressWarnings("unused")
	private int lengthOf(String input) {
		return input.length();
	}
	
}
