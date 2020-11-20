package com.github.ness;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.github.ness.api.AnticheatCheck;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.ChecksManager;
import com.github.ness.api.InfractionManager;
import com.github.ness.api.NessApi;
import com.github.ness.api.PlayersManager;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;
import com.github.ness.violation.ViolationMigratorUtil;

@SuppressWarnings("deprecation")
final class NessApiImpl implements NessApi {

	private final NessAnticheat ness;

	NessApiImpl(NessAnticheat ness) {
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
	public void addViolationAction(ViolationAction action) {
		getInfractionManager().addTrigger(ViolationMigratorUtil.triggerForAction(action));
	}

	@Deprecated
	@Override
	public void flagHack(Violation violation, Player player) {
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
		static final Logger logger = NessLogger.getLogger(NessApiImpl.class);
	}

}
