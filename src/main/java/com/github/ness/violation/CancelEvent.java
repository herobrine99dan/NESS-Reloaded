package com.github.ness.violation;

import com.github.ness.check.dragdown.SetBackType;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

public interface CancelEvent {

	@ConfDefault.DefaultInteger(3)
	int violations();

	@ConfKey("set-back")
	@ConfDefault.DefaultString("SIMPLE_SETBACK")
	SetBackType setBack();

}
