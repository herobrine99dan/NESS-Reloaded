package com.github.ness.check.combat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;

public class KillauraKeepSprint extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEvent(ReceivedPacketEvent.class);

	private double lastDeltaXZ;
	private int bufferViolation;

	public KillauraKeepSprint(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
		lastDeltaXZ = 0;
	}

	/**
	 * Created on 10/24/2020 Package me.frep.vulcan.check.impl.movement.aim by frep
	 * (https://github.com/freppp/)
	 */
	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		if (!e.getPacket().getName().toLowerCase().contains("position") || e.getNessPlayer().isTeleported()) {
			return;
		}
		if (e.getNessPlayer().getLastEntityAttacked() == null) {
			return;
		}
		NessPlayer player = e.getNessPlayer();
		MovementValues values = player.getMovementValues();
		final double deltaXZ = Math.hypot(values.xDiff, values.yDiff);

		final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

		final long swingDelay = player.milliSecondTimeDifference(PlayerAction.ATTACK);

		final boolean sprinting = values.isSprinting();

		final boolean validTarget = Bukkit.getEntity(player.getLastEntityAttacked()) instanceof Player;
		if (swingDelay < 150) {
			player.sendDevMessage(Boolean.toString(acceleration < .0025) + " " + Boolean.toString(deltaXZ > .22));
		}
		final boolean invalid = acceleration < .0025 && sprinting && deltaXZ > .22 && swingDelay < 150 && validTarget;
		if (invalid) {
			if (++bufferViolation > 4) {
				this.flagEvent(e);
			}
		} else if (bufferViolation > 0) {
			bufferViolation--;
		}
		this.lastDeltaXZ = deltaXZ;
	}

}
