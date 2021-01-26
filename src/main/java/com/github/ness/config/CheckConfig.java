package com.github.ness.config;

import com.github.ness.violation.ViolationHandling;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

public interface CheckConfig {

	@ConfKey("check-specific-violation-handling")
	@ConfComments("Any triggers enabled below will run ONLY for this check")
	@SubSection
	ViolationHandling violationHandling();

}
