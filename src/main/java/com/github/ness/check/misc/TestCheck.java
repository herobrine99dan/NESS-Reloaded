package com.github.ness.check.misc;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableVector;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class TestCheck extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEvent(ReceivedPacketEvent.class);

	public TestCheck(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
	}

}
