package com.github.ness.check;

import java.util.Set;

import com.github.ness.NessPlayer;

final class NessPlayerData {

	private final NessPlayer nessPlayer;
	private final Set<Check> checks;
	
	NessPlayerData(NessPlayer nessPlayer, Set<Check> checks) {
		this.nessPlayer = nessPlayer;
		this.checks = checks;
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
