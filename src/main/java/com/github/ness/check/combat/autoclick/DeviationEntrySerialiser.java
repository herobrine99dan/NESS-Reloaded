package com.github.ness.check.combat.autoclick;

public class DeviationEntrySerialiser extends IntPairSerialiser<DeviationEntry> {

	@Override
	DeviationEntry fromInts(int value1, int value2) {
		return new DeviationEntry(value1, value2);
	}

	@Override
	int[] toInts(DeviationEntry value) {
		return new int[] {value.deviationPercentage(), value.sampleCount()};
	}

	@Override
	public Class<DeviationEntry> getTargetClass() {
		return DeviationEntry.class;
	}
	
}
