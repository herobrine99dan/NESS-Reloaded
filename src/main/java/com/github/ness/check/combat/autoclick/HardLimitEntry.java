package com.github.ness.check.combat.autoclick;

final class HardLimitEntry {

	private final int maxCps;
	private final int retentionSecs;

	HardLimitEntry(int maxCps, int retentionSecs) {
		this.maxCps = maxCps;
		this.retentionSecs = retentionSecs;
	}
	
	int maxCps() {
		return maxCps;
	}
	
	int retentionSecs() {
		return retentionSecs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxCps;
		result = prime * result + retentionSecs;
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof HardLimitEntry)) {
			return false;
		}
		HardLimitEntry other = (HardLimitEntry) object;
		return maxCps == other.maxCps && retentionSecs == other.retentionSecs;
	}
	
}
