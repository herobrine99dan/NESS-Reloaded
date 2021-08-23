package com.github.ness.violation;

import java.util.Arrays;
import java.util.Collection;

import com.github.ness.violation.ViolationTriggerSection.ExecuteCommand;
import com.github.ness.violation.ViolationTriggerSection.NotifyStaff;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

public interface ViolationHandling {

	@ConfKey("notify-staff")
	@ConfComments("Notify staff members of potential cheating")
	@SubSection
	NotifyStaff notifyStaff();
	
	@ConfKey("execute-command")
	@ConfComments("Execute a command. Can be used to kick or ban")
	@SubSection
	ExecuteCommand executeCommand();
	
	/*@ConfKey("cancel")
	@ConfComments({"Cancels the event involved with the cheat.",
		"For example, the speed check will cancel movement."})
	@SubSection
	CancelEvent cancelEvent();*/
	
	default Collection<ViolationTriggerSection> getTriggerSections() {
		return Arrays.asList(notifyStaff(), executeCommand());
	}
	
}
