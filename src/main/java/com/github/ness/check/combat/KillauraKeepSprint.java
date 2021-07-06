package com.github.ness.check.combat;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class KillauraKeepSprint extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private double lastDeltaXZ;
	private int bufferViolation;

	public KillauraKeepSprint(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		lastDeltaXZ = 0;
	}

	/**
	 * Created on 10/24/2020 Package me.frep.vulcan.check.impl.movement.aim by frep
	 * (https://github.com/freppp/)
	 */
	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		NessPlayer player = player();
		if (player.isHasSetback() || player.isTeleported()) {
			return;
		}
		if (player.getLastEntityAttacked() == null) {
			return;
		}
		MovementValues values = player.getMovementValues();
		final double deltaXZ = values.getXZDiff();

		final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

		final long swingDelay = player.milliSecondTimeDifference(PlayerAction.ATTACK);

		final boolean sprinting = player.getSprinting().get();
		LivingEntity entity = getEntityById(e.getPlayer().getWorld(), player.getLastEntityAttacked());
		if (entity == null) { // The Entity can be dead.
			return;
		}
		final boolean validTarget = entity instanceof Player;
		final boolean invalid = acceleration < .0025 && sprinting && deltaXZ > .22 && swingDelay < 150 && validTarget;
		if (invalid) {
			if (++bufferViolation > 2) {
				this.player().sendDevMessage("invalid: " + invalid);
				this.flagEvent(e);
			}
		} else if (bufferViolation > 0) {
			bufferViolation -= 0.5;
		}
		this.lastDeltaXZ = deltaXZ;
	}

	private LivingEntity getEntityById(World w, UUID uuid) {
		for (LivingEntity l : w.getLivingEntities()) {
			if (l.getUniqueId().equals(uuid)) {
				return l;
			}
		}
		return null;
	}

}
