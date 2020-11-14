package com.github.ness.check.combat.autoclick;

final class DeviationEntry {

	private final int deviationPercentage;
	private final int sampleCount;
	
	DeviationEntry(int deviationPercentage, int sampleCount) {
		this.deviationPercentage = deviationPercentage;
		this.sampleCount = sampleCount;
	}
	
	int deviationPercentage() {
		return deviationPercentage;
	}
	
	int sampleCount() {
		return sampleCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviationPercentage;
		result = prime * result + sampleCount;
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof DeviationEntry)) {
			return false;
		}
		DeviationEntry other = (DeviationEntry) object;
		return deviationPercentage == other.deviationPercentage && sampleCount == other.sampleCount;
	}

	@Override
	public String toString() {
		return "DeviationEntry [deviationPercentage=" + deviationPercentage + ", sampleCount=" + sampleCount + "]";
	}
	
}
