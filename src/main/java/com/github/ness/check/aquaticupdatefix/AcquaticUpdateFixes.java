package com.github.ness.check.aquaticupdatefix;

import com.github.ness.NessPlayer;

public class AcquaticUpdateFixes {

	private final NessPlayer nessPlayer;
	private volatile boolean riptiding;
	private volatile long lastRiptideEvent;
	private static final int RIPTIDETIME = 500;
	private volatile int tridentTicks;

	public AcquaticUpdateFixes(NessPlayer nessPlayer) {
		this.nessPlayer = nessPlayer;
		this.riptiding = false;
		lastRiptideEvent = 0;
	}

	public NessPlayer getNessPlayer() {
		return nessPlayer;
	}

	public boolean isRiptiding() {
		return riptiding || getDelayFromRiptideEvent() < RIPTIDETIME;
	}

	public void setRiptiding(boolean riptiding) {
		this.riptiding = riptiding;
		if(riptiding) {
			tridentTicks++;
		} else {
			tridentTicks = 0;
		}
	}

	void updateRiptideEvent() {
		lastRiptideEvent = System.nanoTime();
	}
	
	public long getDelayFromRiptideEvent() {
		return (long) ((System.nanoTime() - lastRiptideEvent) / 1e+6);
	}

	public long getLastRiptideEvent() {
		return (long) (lastRiptideEvent / 1e+6);
	}

	public int getTridentTicks() {
		return tridentTicks;
	}

}
