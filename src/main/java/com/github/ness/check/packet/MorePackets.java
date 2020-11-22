package com.github.ness.check.packet;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class MorePackets extends ListeningCheck<ReceivedPacketEvent> {

	private int maxPackets;
	private int serverCrasherMaxPackets;

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(ReceivedPacketEvent.class, Duration.ofSeconds(1));
	int normalPacketsCounter; // For MorePackets

	public MorePackets(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxPackets = this.ness().getMainConfig().getCheckSection().morePackets().maxPackets();
		this.serverCrasherMaxPackets = this.ness().getMainConfig().getCheckSection().morePackets()
				.serverCrasherMaxPackets();
		normalPacketsCounter = -5;
	}

	public interface Config {
		@DefaultInteger(80)
		int maxPackets();

		@DefaultInteger(230)
		@ConfComments({
				"NESS Reloaded can async kick players (using Netty, NESS Reloaded can disable the autoRead config option)",
				"This feature is experimental, to disable set this to -1, else change this number to something bigger (A normal PLayer sends at most 100 packets per second" })
		int serverCrasherMaxPackets();
	}

	@Override
	protected void checkAsyncPeriodic() {
		normalPacketsCounter = 0;
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		int ping = Utility.getPing(e.getNessPlayer().getBukkitPlayer());
		int maxPackets = this.maxPackets + ((ping / 100) * 6);
		NessPlayer np = e.getNessPlayer();
		if (np == null) {
			return;
		}
		if (np.getMovementValues().isInsideVehicle()) {
			return;
		}
		if (normalPacketsCounter++ > maxPackets && np.milliSecondTimeDifference(PlayerAction.JOIN) > 5000) {
			if (normalPacketsCounter > serverCrasherMaxPackets) {
				e.setCancelled(true);
				np.kickThreadSafe();
				return;
				// np.kickThreadSafe();
			} else {
				flagEvent(e);
			}
		}
	}
}
