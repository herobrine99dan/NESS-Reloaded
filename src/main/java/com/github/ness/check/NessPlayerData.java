package com.github.ness.check;

import java.util.Objects;
import java.util.Set;

import com.github.ness.NessPlayer;

final class NessPlayerData {

	private final NessPlayer nessPlayer;
	private final Set<Check> checks;
	
	NessPlayerData(NessPlayer nessPlayer, Set<Check> checks) {
		this.nessPlayer = Objects.requireNonNull(nessPlayer);
		this.checks = Objects.requireNonNull(checks);
	}
	
	NessPlayer getNessPlayer() {
		return nessPlayer;
	}
	
	Set<Check> getChecks() {
		return checks;
	}

	@Override
	public String toString() {
		return "NessPlayerData [nessPlayer=" + nessPlayer + ", checks=" + checks + "]";
	}
	
}
