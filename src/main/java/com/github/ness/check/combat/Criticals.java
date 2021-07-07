package com.github.ness.check.combat;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class Criticals extends ListeningCheck<EntityDamageByEntityEvent> {

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEvent(EntityDamageByEntityEvent.class);

	public Criticals(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager()))
			return;
		check(e);
	}

	private void check(EntityDamageByEntityEvent event) {
		NessPlayer nessPlayer = player();
		Player player = (Player) event.getDamager();
		MovementValues values = player().getMovementValues();
		/*
		 * if (!nessPlayer.isOnGroundPacket() &&
		 * !values.getHelper().hasflybypass(nessPlayer) && !values.isAroundLiquids() &&
		 * !Utility.hasVehicleNear(player) && !values.isAroundWeb()) { if
		 * (nessPlayer.getMovementValues().getTo().getY() % 1.0D == 0.0D &&
		 * player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid
		 * ()) { flagEvent(event); } }
		 */
		if(getMaterialName(values.getTo()).contains("WEB") || getMaterialName(values.getFrom()).contains("WEB")) {
			return;
		}
		if (values.isNearLiquid() || values.getHelper().hasflybypass(nessPlayer)) {
			return;
		}
		if (!player.isOnGround() && this.player().getMovementValues().getTo().getY() % 1.0 == 0.0) {
			flagEvent(event);
		}
	}
}
