package com.github.ness.check.combat.autoclick;

public class HardLimitSerialiser extends IntPairSerialiser<HardLimitEntry> {

	@Override
	HardLimitEntry fromInts(int value1, int value2) {
		return new HardLimitEntry(value1, value2);
	}

	@Override
	int[] toInts(HardLimitEntry value) {
		return new int[] {value.maxCps(), value.retentionSecs()};
	}

	@Override
	public Class<HardLimitEntry> getTargetClass() {
		return HardLimitEntry.class;
	}
	
}
