package com.github.ness;

import java.util.logging.Logger;

import com.github.ness.api.ChecksManager;
import com.github.ness.api.InfractionManager;
import com.github.ness.api.NESSApi;
import com.github.ness.api.PlayersManager;

@SuppressWarnings("deprecation")
final class NessApiImpl implements NESSApi {

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
	
	private static class LoggerHolder {
		static final Logger logger = NessLogger.getLogger(NessApiImpl.class);
	}

}
