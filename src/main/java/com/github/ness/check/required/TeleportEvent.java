package com.github.ness.check.required;

import java.time.Duration;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class TeleportEvent extends ListeningCheck<PlayerTeleportEvent> {

	public static final ListeningCheckInfo<PlayerTeleportEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(PlayerTeleportEvent.class, Duration.ofMillis(1500));

	public TeleportEvent(ListeningCheckFactory<?, PlayerTeleportEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkAsyncPeriodic() {
		player().setTeleported(false);
	}

	protected void checkEvent(PlayerTeleportEvent e) {
		Location result = e.getTo().clone();
		if (e.getTo().getPitch() == Math.round(e.getTo().getPitch())) {
			if (e.getTo().getPitch() > 89) {
				result.setPitch(e.getTo().getPitch() - 0.01f);
			} else {
				result.setPitch(e.getTo().getPitch() + 0.01f);
			}
		} else if (e.getTo().getYaw() == Math.round(e.getTo().getYaw())) {
			if (e.getTo().getYaw() > 360) {
				result.setYaw(e.getTo().getYaw() - 0.01f);
			} else {
				result.setYaw(e.getTo().getYaw() + 0.01f);
			}
		}
		e.setTo(result);
		NessPlayer nessPlayer = this.player();
		if (!nessPlayer.hasSetback) {
			nessPlayer.setTeleported(true);
		}
		nessPlayer.hasSetback = false;
	}

}
