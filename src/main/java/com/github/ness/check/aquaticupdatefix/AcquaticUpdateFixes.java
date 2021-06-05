package com.github.ness.check.aquaticupdatefix;

import com.github.ness.NessPlayer;

public class AcquaticUpdateFixes {

	private final NessPlayer nessPlayer;
	private volatile boolean riptiding;

	public AcquaticUpdateFixes(NessPlayer nessPlayer) {
		this.nessPlayer = nessPlayer;
		this.riptiding = false;
	}

	public NessPlayer getNessPlayer() {
		return nessPlayer;
	}

	public boolean isRiptiding() {
		return riptiding;
	}

	public void setRiptiding(boolean riptiding) {
		this.riptiding = riptiding;
	}

}
