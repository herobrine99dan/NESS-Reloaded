package com.github.ness.check.packet;

import java.time.Duration;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class Freecam extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(ReceivedPacketEvent.class, Duration.ofMillis(50));

	private long lastPosition;
	private int maxDelay;

	public Freecam(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxDelay = this.ness().getMainConfig().getCheckSection().freecam().maxDelay();
		lastPosition = 0;
	}
	
	public interface Config {
		@DefaultInteger(550)
		@ConfComments({ "NESS Reloaded can async kick players (using Netty, NESS Reloaded can disable the autoRead config option)",
			"This feature is experimental, to disable set this to -1, else change this number to something bigger (A normal Player sends at most 100 packets per second" })
		int maxDelay();
	}

	@Override
	protected void checkAsyncPeriodic() {
		if ((System.nanoTime() - lastPosition) / 1e+6 > maxDelay) {
			runTaskLater(() -> {
				player().getBukkitPlayer().teleport(player().getBukkitPlayer().getLocation().clone().add(0, 0.00001, 0),
						TeleportCause.PLUGIN);
			}, durationOfTicks(1));
			this.lastPosition = System.nanoTime();
		}
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		this.lastPosition = System.nanoTime();
	}
}
