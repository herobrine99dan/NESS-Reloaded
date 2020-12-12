package com.github.ness.check.misc;

import java.time.Duration;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.github.ness.NessPlayer;
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
	private byte lastItemSlot = 0;
	private Material lastItemType = Material.AIR;

	public ChestStealer(ListeningCheckFactory<?, InventoryClickEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkAsyncPeriodic() {
		movedInvItems = 0;
	}

	@Override
	protected void checkEvent(InventoryClickEvent e) {
		NessPlayer nessPlayer = player();
		if (player().isNot(e.getWhoClicked()))
			return;
		final Inventory i1 = e.getInventory();
		if(nessPlayer.getBukkitPlayer().getGameMode().equals(GameMode.CREATIVE) || e.getCurrentItem() == null) {
			return;
		}
		final Material itemType = e.getCurrentItem().getType();
		if (!i1.getType().equals(InventoryType.CRAFTING) && itemType != Material.AIR) {
			//if (i1.getType().equals(InventoryType.CHEST)) {
			if (e.getRawSlot() < e.getInventory().getSize() + 26 && lastItemSlot != e.getRawSlot()) {
				if (!lastItemType.equals(itemType)) {
					movedInvItems++;
					if (movedInvItems > 4) {
						flagEvent(e, " movedInventoryItems: " + movedInvItems);
						movedInvItems = 0;
					}
					final long now = System.currentTimeMillis();
					final long result = now - moveInvItemsLastTime;
					if (result < 60) {
						flagEvent(e, " timeBetweenMovedItems: " + result);
					}
					moveInvItemsLastTime = System.currentTimeMillis();
				}
				lastItemType = itemType;
				lastItemSlot = (byte) e.getRawSlot();
			} else {
				lastItemType = Material.AIR;
			}
		}
	}

}
