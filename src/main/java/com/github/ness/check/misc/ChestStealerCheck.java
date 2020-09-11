package com.github.ness.check.misc;

import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

public class ChestStealerCheck extends AbstractCheck<InventoryClickEvent> {
	
	/**
	 * @author MatuloM
	 * @param manager
	 */

	public ChestStealerCheck(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(InventoryClickEvent.class, 500, TimeUnit.MILLISECONDS));
	}

	@Override
	protected void checkAsyncPeriodic(NessPlayer player) {
		if (player.movedInvItemsLastCount == player.movedInvItems) {
			player.setViolation(new Violation("ChestStealer", "movedInventoryItems: " + player.movedInvItems), null);
		}
		player.movedInvItemsLastCount = player.movedInvItems;
		player.movedInvItems = 0;
	}

	@Override
	protected void checkEvent(InventoryClickEvent e) {
		NessPlayer nessPlayer = this.getNessPlayer((Player) e.getWhoClicked());
		Inventory i1 = e.getWhoClicked().getInventory();
		Inventory i2 = e.getClickedInventory();
		if (!i1.equals(i2)) {
			nessPlayer.movedInvItems++;
			if (nessPlayer.movedInvItems > 5) {
				nessPlayer.setViolation(new Violation("ChestStealer", "movedInventoryItems: " + nessPlayer.movedInvItems), null);
			}
		}
	}

}