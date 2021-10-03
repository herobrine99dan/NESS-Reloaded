package com.github.ness.check.packet;

import java.time.Duration;

import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.check.PeriodicTaskInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.Packet;

import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class Freecam extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPacketsWithTask(PeriodicTaskInfo.syncTask(Duration.ofMillis(50)));

	private long lastPosition;
	private int maxDelay;

	public Freecam(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.maxDelay = this.ness().getMainConfig().getCheckSection().freecam().maxDelay();
		lastPosition = 0;
	}

	public interface Config {
		@DefaultInteger(1100)
		int maxDelay();
	}

	@Override
	protected void checkSyncPeriodic() {
		if ((System.nanoTime() - lastPosition) / 1e+6 > maxDelay
				&& player().milliSecondTimeDifference(PlayerAction.JOIN) > 3000) {
			player().getBukkitPlayer().teleport(player().getBukkitPlayer().getLocation().clone().add(0, 0.00001, 0),
						TeleportCause.PLUGIN);
			this.lastPosition = System.nanoTime();
		}
	}

	@Override
	protected void checkPacket(Packet packet) {
		this.lastPosition = System.nanoTime();
	}
}
