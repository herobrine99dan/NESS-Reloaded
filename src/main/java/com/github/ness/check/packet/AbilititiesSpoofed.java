package com.github.ness.check.packet;

import java.time.Duration;

import org.bukkit.GameMode;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.wrappers.PacketPlayAbilities;

public class AbilititiesSpoofed extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(ReceivedPacketEvent.class, Duration.ofSeconds(1));

	public AbilititiesSpoofed(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		if (e.getPacket().getName().toLowerCase().contains("abilities")) {
			final NessPlayer nessPlayer = e.getNessPlayer();
			MovementValues values = nessPlayer.getMovementValues();
			if (values.getGamemode() == GameMode.CREATIVE || values.getGamemode() == GameMode.SPECTATOR)
				return;
			PacketPlayAbilities abilities = new PacketPlayAbilities(e.getPacket());
			if ((abilities.isAbleFly() || abilities.isFlying()) && !(values.isAbleFly() || values.isFlying())) {
				this.runTaskLater(new Runnable() {
					@Override
					public void run() {
						nessPlayer.getBukkitPlayer().setFlying(false);
					}
				}, this.durationOfTicks(1));
			}
		}
	}

}
