package com.github.ness.check.dragdown;

public enum SetBackType {

	SETBACK_DOWN(new DragDownDown()),
	SETBACK_LIMIT(new DragDownLimit()),
	SETBACK_VELOCITY(new DragDownVelocity()),
	SAFE_SETBACK(new SafeSetBack()),
	SIMPLE_SETBACK(new SimpleSetBack());

	private SetBackType(SetBack setBack) {
		this.setBack = setBack;
	}

	private final SetBack setBack;

	public SetBack getSetBack() {
		return setBack;
	}

}
