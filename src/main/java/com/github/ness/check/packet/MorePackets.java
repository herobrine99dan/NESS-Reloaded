package com.github.ness.check.packet;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.check.PeriodicTaskInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.Packet;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class MorePackets extends PacketCheck {

	private final int maxPackets;
	private final int serverCrasherMaxPackets;
	private final String kickMessage;

	public static final CheckInfo checkInfo = CheckInfos.forPacketsWithTask(PeriodicTaskInfo.asyncTask(Duration.ofSeconds(1)));

	private volatile int normalPacketsCounter;

	public MorePackets(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		Config config = ness().getMainConfig().getCheckSection().morePackets();
		this.maxPackets = config.maxPackets();
		this.serverCrasherMaxPackets = config.serverCrasherMaxPackets();
		this.kickMessage = config.kickMessage();
		normalPacketsCounter = -5;
	}

	public interface Config {
		@DefaultInteger(85)
		@ConfComments("The threshold at which to flag the player for cheating")
		int maxPackets();

		@DefaultInteger(250)
		@ConfComments({"The threshold at which to kick the player ASAP" })
		int serverCrasherMaxPackets();

		@ConfDefault.DefaultString("Too many packets sent")
		@ConfComments({"The kick message when a player is kicked for too many packets"})
		String kickMessage();

	}

	@Override
	protected void checkAsyncPeriodic() {
		normalPacketsCounter = 0;
	}

	@Override
	protected void checkPacket(Packet packet) {
		//int ping = Utility.getPing(e.getNessPlayer().getBukkitPlayer());
		//int maxPackets = this.maxPackets + ((ping / 100) * 6);
		NessPlayer np = player();
		if (np == null) {
			return;
		}
		if (np.getMovementValues().isInsideVehicle()) {
			return;
		}
		int normalPacketsCounter = this.normalPacketsCounter;
		normalPacketsCounter++;
		this.normalPacketsCounter = normalPacketsCounter;
		if (normalPacketsCounter++ > maxPackets && np.milliSecondTimeDifference(PlayerAction.JOIN) > 5000) {
			if (normalPacketsCounter > serverCrasherMaxPackets) {
				packet.cancel();
				np.kickThreadSafe(kickMessage);
			} else {
				this.flag("");
			}
		}
	}
}
