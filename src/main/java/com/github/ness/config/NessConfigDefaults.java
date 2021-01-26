package com.github.ness.config;

import com.github.ness.violation.ViolationHandling;
import com.github.ness.violation.ViolationTriggerSection;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NessConfigDefaults {

	public static Map<String, CheckConfig> violationHandlingPerCheck() {
		Map<String, CheckConfig> map = new HashMap<>();
		map.put("AutoClicker", new CheckConfig() {

			@Override
			public ViolationHandling violationHandling() {
				return new ViolationHandling() {
					@Override
					public ViolationTriggerSection.NotifyStaff notifyStaff() {
						return new ViolationTriggerSection.NotifyStaff() {
							@Override
							public int violations() {
								return -1;
							}

							@Override
							public String notification() {
								return "";
							}

							@Override
							public String discordWebHook() {
								return "";
							}

							@Override
							public String discordTitle() {
								return "";
							}

							@Override
							public String discordDescription() {
								return "";
							}

							@Override
							public Color discordColor() {
								return Color.RED;
							}

							@Override
							public boolean bungeecord() {
								return false;
							}
						};
					}

					@Override
					public ViolationTriggerSection.ExecuteCommand executeCommand() {
						return new ViolationTriggerSection.ExecuteCommand() {
							@Override
							public int violations() {
								return 6;
							}

							@Override
							public String command() {
								return "&c%PLAYER% &7was flagged for AutoClicker. Details: %DETAILS%";
							}
						};
					}

					@Override
					public ViolationTriggerSection.CancelEvent cancelEvent() {
						return new ViolationTriggerSection.CancelEvent() {
							@Override
							public int violations() {
								return 2;
							}

							@Override
							public String dragDown() {
								return "";
							}
						};
					}
				};
			}
		});
		return Collections.unmodifiableMap(map);
	}
}
