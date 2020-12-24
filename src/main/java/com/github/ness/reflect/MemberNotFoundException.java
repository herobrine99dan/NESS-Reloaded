package com.github.ness.reflect;

public final class MemberNotFoundException extends RuntimeException {

	public static final MemberNotFoundException INSTANCE = new MemberNotFoundException();

	private MemberNotFoundException() {
		super("Member not found", null, false, false);
	}

}
