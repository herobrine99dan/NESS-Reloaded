package com.github.ness.check.misc;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.CheckManager;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

public class TestCheck extends AbstractCheck<EntityDamageByEntityEvent> {

	public TestCheck(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent event) {
	}

}
