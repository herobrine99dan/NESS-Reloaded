package com.github.ness.violation;

import java.util.Arrays;
import java.util.Collection;

import com.github.ness.violation.ViolationTriggerSection.ExecuteCommand;
import com.github.ness.violation.ViolationTriggerSection.NotifyStaff;

import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

public interface ViolationHandling {

	@ConfKey("notify-staff")
	@SubSection
	NotifyStaff notifyStaff();
	
	@ConfKey("execute-command")
	@SubSection
	ExecuteCommand executeCommand();
	
	default Collection<ViolationTriggerSection> getTriggerSections() {
		return Arrays.asList(notifyStaff(), executeCommand());
	}
	
}
