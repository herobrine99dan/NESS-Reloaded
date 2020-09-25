package com.github.ness;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import com.github.ness.api.AnticheatCheck;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.NESSApi;
import com.github.ness.api.InfractionTrigger;
import com.github.ness.violation.ViolationMigratorUtil;

import org.bukkit.entity.Player;

final class NESSApiImpl implements NESSApi {

	private final NESSAnticheat ness;

	NESSApiImpl(NESSAnticheat ness) {
		this.ness = ness;
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void addViolationAction(com.github.ness.api.ViolationAction action) {
		addInfractionTrigger(ViolationMigratorUtil.triggerForAction(action));
	}

	@Override
	public void addInfractionTrigger(InfractionTrigger trigger) {
		ness.getViolationManager().addTrigger(trigger);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void flagHack(com.github.ness.api.Violation violation, Player player) {
		LoggerHolder.logger.warning(
				"Caller is extremely discouraged from using the deprecated flagHack(Violation, Player) method");
		AnticheatPlayer anticheatPlayer = getPlayer(player.getUniqueId());
		if (anticheatPlayer != null) {
			for (AnticheatCheck check : getAllChecks()) {
				if (check.getCheckName().equals(violation.getCheck())) {
					check.flagHack(anticheatPlayer);
					return;
				}
			}

		}
	}
	
	private static class LoggerHolder {
		static final Logger logger = NessLogger.getLogger(NESSApiImpl.class);
	}

	@Override
	public Collection<? extends AnticheatCheck> getAllChecks() {
		return ness.getCheckManager().getAllChecks();
	}

	@Override
	public Collection<? extends AnticheatPlayer> getAllPlayers() {
		return ness.getCheckManager().getAllPlayers();
	}

	@Override
	public AnticheatPlayer getPlayer(UUID uuid) {
		return ness.getCheckManager().getPlayer(uuid);
	}

}
