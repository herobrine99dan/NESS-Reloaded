package com.github.ness.check.misc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

public class InventoryHack extends ListeningCheck<InventoryClickEvent> {

    double maxdist;
	public static final ListeningCheckInfo<InventoryClickEvent> checkInfo = CheckInfos
			.forEvent(InventoryClickEvent.class);

	public InventoryHack(ListeningCheckFactory<?, InventoryClickEvent> factory, NessPlayer player) {
		super(factory, player);
        this.maxdist = this.ness().getNessConfig().getCheck(this.getClass()).getDouble("maxdist", 0.1);
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
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (Utility.hasflybypass(player)) {
                return;
            }
            if (player.isSprinting() || player.isSneaking() || player.isBlocking() || player.isSleeping()
                    || player.isConversing()) {
            	if(player().setViolation(new Violation("InventoryHack", "Impossible"))) e.setCancelled(true);
            } else {
                final Location from = player.getLocation();
                Bukkit.getScheduler().runTaskLater(this.ness(), () -> {
                    Location to = player.getLocation();
                    double distance = (Math.abs(to.getX() - from.getX())) + (Math.abs(to.getZ() - from.getZ()));
                    if (distance > maxdist) {
                    	if(player().setViolation(new Violation("InventoryHack", "Dist: " + distance))) e.setCancelled(true);

                    }
                }, 2L);
            }
        }
    }
}
