package com.github.ness.check.required;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.TimeUnit;

public class TeleportEvent extends AbstractCheck<PlayerTeleportEvent> {

	public static final CheckInfo<PlayerTeleportEvent> checkInfo = CheckInfo
			.eventOnly(PlayerTeleportEvent.class);

	public TeleportEvent(CheckFactory<?> factory, NessPlayer player) {
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
