package com.github.ness;

import org.bukkit.entity.Player;

import com.github.ness.api.NESSApi;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NESSApiImpl implements NESSApi {

	private final NESSAnticheat ness;
	
	@Override
	public void addViolationAction(ViolationAction action) {
		ness.getViolationManager().addAction(action);
	}

	@Override
	public void flagHack(Violation violation, Player player) {
		this.ness.getCheckManager().getPlayer(player).setViolation(violation);
	}

}
