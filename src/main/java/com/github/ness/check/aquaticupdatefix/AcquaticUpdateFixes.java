package com.github.ness.check.aquaticupdatefix;

import com.github.ness.NessPlayer;

public class AcquaticUpdateFixes {

	private final NessPlayer nessPlayer;
	private volatile long riptideEvent;

	public AcquaticUpdateFixes(NessPlayer nessPlayer) {
		this.nessPlayer = nessPlayer;
		this.riptideEvent = 0;
	}

	public void updateRiptideEvent() {
		riptideEvent = System.nanoTime();
	}

	public NessPlayer getNessPlayer() {
		return nessPlayer;
	}

	public long getRiptideEventTime() {
		return (long) ((System.nanoTime() - riptideEvent) / 1e+6);
	}

}
