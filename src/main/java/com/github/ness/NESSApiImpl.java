package com.github.ness;

import java.util.logging.Logger;

import com.github.ness.api.AnticheatCheck;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.ChecksManager;
import com.github.ness.api.InfractionManager;
import com.github.ness.api.NESSApi;
import com.github.ness.api.PlayersManager;
import com.github.ness.violation.ViolationMigratorUtil;

import org.bukkit.entity.Player;

final class NESSApiImpl implements NESSApi {

	private final NESSAnticheat ness;

	NESSApiImpl(NESSAnticheat ness) {
		this.ness = ness;
	}
	
	@Override
	public InfractionManager getInfractionManager() {
		return ness.getViolationManager();
	}
	
	@Override
	public ChecksManager getChecksManager() {
		return ness.getCheckManager();
	}
	
	@Override
	public PlayersManager getPlayersManager() {
		return ness.getCheckManager().getPlayersManager();
	}
	
	// Deprecated API

	@Deprecated
	@Override
	public void addViolationAction(com.github.ness.api.ViolationAction action) {
		getInfractionManager().addTrigger(ViolationMigratorUtil.triggerForAction(action));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void flagHack(com.github.ness.api.Violation violation, Player player) {
		LoggerHolder.logger.warning(
				"Caller is extremely discouraged from using the deprecated flagHack(Violation, Player) method");
		AnticheatPlayer anticheatPlayer = getPlayersManager().getPlayer(player.getUniqueId());
		if (anticheatPlayer != null) {
			for (AnticheatCheck check : getChecksManager().getAllChecks()) {
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

}
