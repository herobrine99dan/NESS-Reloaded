package com.github.ness.check.misc;

import java.util.concurrent.TimeUnit;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class ChestStealerCheck extends AbstractCheck<InventoryClickEvent> {

	/**
	 * @author MatuloM
	 */

	public static final CheckInfo<InventoryClickEvent> checkInfo = CheckInfo
			.eventWithAsyncPeriodic(InventoryClickEvent.class, 500, TimeUnit.MILLISECONDS);
	private long moveInvItemsLastTime;
	private int movedInvItems;

	public ChestStealerCheck(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkAsyncPeriodic() {
		/*
		 * if (player.movedInvItemsLastCount == player.movedInvItems) {
		 * player.setViolation(new Violation("ChestStealer", "movedInventoryItems: " +
		 * player.movedInvItems), null); } // BAD CHECK. player.movedInvItemsLastCount =
		 * player.movedInvItems;
		 */
		movedInvItems = 0;
	}

	@Override
	protected void checkEvent(InventoryClickEvent e) {
		NessPlayer nessPlayer = player();
		if (player().isNot(e.getWhoClicked()))
			return;
		final Inventory i1 = e.getWhoClicked().getInventory();
		final Inventory i2 = e.getInventory();
		if(nessPlayer.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if (i1 != i2 && e.getCurrentItem().getType() != Material.AIR) {
			movedInvItems++;
			if (movedInvItems > 4) {
				nessPlayer.setViolation(
						new Violation("ChestStealer", "movedInventoryItems: " + movedInvItems), null);
				movedInvItems = 0;
			}
			final long now = System.currentTimeMillis();
			final long result = now - moveInvItemsLastTime;
			if (result < 80) {
				nessPlayer.setViolation(new Violation("ChestStealer", "timeBetweenMovedItems: " + result), null);
			}
			moveInvItemsLastTime = System.currentTimeMillis();
		}
	}

}
