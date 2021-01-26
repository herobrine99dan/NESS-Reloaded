package com.github.ness.check.dragdown;

public enum DragDownEnum {
	DRAGDOWNDOWN(new DragDownDown()), DRAGDOWNLIMIT(new DragDownLimit()), DRAGDOWNVELOCITY(new DragDownVelocity()),
	SAFESETBACK(new SafeSetBack()), SIMPLESETBACK(new SimpleSetBack());

	private DragDownEnum(SetBack setBack) {
		this.setBack = setBack;
	}

	private final SetBack setBack;

	public SetBack getSetBack() {
		return setBack;
	}

}
