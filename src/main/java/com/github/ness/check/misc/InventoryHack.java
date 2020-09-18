package com.github.ness.check.misc;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryHack extends AbstractCheck<InventoryClickEvent> {

    double maxdist;
	public static final CheckInfo<InventoryClickEvent> checkInfo = CheckInfo
			.eventOnly(InventoryClickEvent.class);

	public InventoryHack(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        this.maxdist = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getDouble("maxdist", 0.1);
	}

    @Override
    protected void checkEvent(InventoryClickEvent e) {
        Check(e);
    }

    /**
     * Check for Impossible InventoryHack or big Distance
     *
     * @param e
     */
    public void Check(InventoryClickEvent e) {
		if (player().isNot(e.getWhoClicked()))
			return;
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (Utility.hasflybypass(player)) {
                return;
            }
            if (player.isSprinting() || player.isSneaking() || player.isBlocking() || player.isSleeping()
                    || player.isConversing()) {
            	player().setViolation(new Violation("InventoryHack", "Impossible"), e);
            } else {
                final Location from = player.getLocation();
                Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
                    Location to = player.getLocation();
                    double distance = (Math.abs(to.getX() - from.getX())) + (Math.abs(to.getZ() - from.getZ()));
                    if (distance > maxdist) {
                        player().setViolation(new Violation("InventoryHack", "Dist:" + distance), e);
                    }
                }, 2L);
            }
        }
    }
}
