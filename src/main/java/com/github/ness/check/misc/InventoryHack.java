package com.github.ness.check.misc;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class InventoryHack extends ListeningCheck<InventoryClickEvent> {

	public static final ListeningCheckInfo<InventoryClickEvent> checkInfo = CheckInfos
			.forEvent(InventoryClickEvent.class);

	public InventoryHack(ListeningCheckFactory<?, InventoryClickEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(InventoryClickEvent e) {
		if (player().isNot(e.getWhoClicked()))
			return;
		Check(e);
	}

	/**
	 * Check for Impossible InventoryHack or big Distance
	 *
	 * @param e
	 */
	public void Check(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		NessPlayer nessPlayer = player();
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) < 1500
				|| nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500 || Utility.hasflybypass(player)
				|| this.player().isTeleported()) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 90
				|| nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKBROKED) < 90) {
			flagEvent(e);
			return;
		}
		if (player.isSprinting() || player.isSneaking() || player.isBlocking() || player.isSleeping()
				|| player.isConversing()) {
			flagEvent(e);
			return;
		}
		final Location from = player.getLocation();

		runTaskLater(() -> {
			Location to = player.getLocation();
			double distance = (Math.abs(to.getX() - from.getX())) + (Math.abs(to.getZ() - from.getZ()));
			if (distance > 0.15) {
				flagEvent(e);
			}
		}, durationOfTicks(2));
	}
}
