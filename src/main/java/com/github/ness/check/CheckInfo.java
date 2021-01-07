package com.github.ness.check;

import java.util.Objects;

/**
 * Determines how a check should be orchestrated
 * 
 * @author A248
 */
public class CheckInfo extends BaseCheckInfo {

	private final PeriodicTaskInfo taskInfo;

	CheckInfo(PeriodicTaskInfo taskInfo) {
		this.taskInfo = Objects.requireNonNull(taskInfo);
	}
	
	CheckInfo() {
		this(PeriodicTaskInfo.none());
	}

	PeriodicTaskInfo taskInfo() {
		return taskInfo;
	}

}
