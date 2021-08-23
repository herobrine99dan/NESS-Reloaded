package com.github.ness.check.misc;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.PlayerAction;

public class InventoryHack extends ListeningCheck<InventoryClickEvent> {

	public static final ListeningCheckInfo<InventoryClickEvent> checkInfo = CheckInfos
			.forEvent(InventoryClickEvent.class);

	public InventoryHack(ListeningCheckFactory<?, InventoryClickEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double buffer;

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
		if (e.getAction().name().contains("DROP") || e.getAction() == InventoryAction.COLLECT_TO_CURSOR
				|| e.getAction() == InventoryAction.HOTBAR_SWAP) {
			return;
		}
		final String action = e.getAction().name();
		Player player = (Player) e.getWhoClicked();
		NessPlayer nessPlayer = player();
		if (player.getGameMode().name().contains("CREATIVE")) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500
				|| nessPlayer.getMovementValues().getHelper().hasflybypass(nessPlayer) || nessPlayer.isTeleported()
				|| nessPlayer.isHasSetback()) {
			return;
		}
		boolean inventoryOpened = player.getOpenInventory().getType() != InventoryType.CRAFTING;
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 100
				|| nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKBROKED) < 100) {
			flag("IllegalBlockAction");
			return;
		} else if (nessPlayer.milliSecondTimeDifference(PlayerAction.ANIMATION) < 50) {
			flag("MS: " + nessPlayer.milliSecondTimeDifference(PlayerAction.ANIMATION));
			return;
		} else if (player.isSprinting() || player.isSneaking() || player.isBlocking() || player.isSleeping()
				|| player.isConversing()) {
			flag(" IllegalAction");
			return;
		} else if (!inventoryOpened && e.getRawSlot() == -1) {
			flag();
			return;
		}

		final Location from = player.getLocation();

		runTaskLater(() -> {
			Location to = player.getLocation();
			double distance = Math.hypot(from.getX() - to.getX(), from.getZ() - to.getZ()) - (to.getY() - from.getY());
			if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1000) {
				return;
			}
			if (distance > 0.19) {
				if (++buffer > 1) {
					flag(action);
				}
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
		}, durationOfTicks(2));
	}
}
