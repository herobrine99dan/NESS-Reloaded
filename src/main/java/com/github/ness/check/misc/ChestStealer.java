package com.github.ness.check.misc;

import java.time.Duration;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class ChestStealer extends ListeningCheck<InventoryClickEvent> {

	/**
	 * @author MatuloM
	 */

	public static final ListeningCheckInfo<InventoryClickEvent> checkInfo = CheckInfos.
			forEventWithAsyncPeriodic(InventoryClickEvent.class, Duration.ofMillis(500));
	private long moveInvItemsLastTime;
	private int movedInvItems;

	public ChestStealer(ListeningCheckFactory<?, InventoryClickEvent> factory, NessPlayer player) {
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
				if(player().setViolation(new Violation("ChestStealer", "movedInventoryItems: " + movedInvItems))) e.setCancelled(true);
				movedInvItems = 0;
			}
			final long now = System.currentTimeMillis();
			final long result = now - moveInvItemsLastTime;
			if (result < 80) {
				if(player().setViolation(new Violation("ChestStealer", "timeBetweenMovedItems: " + result))) e.setCancelled(true);
			}
			moveInvItemsLastTime = System.currentTimeMillis();
		}
	}

}
