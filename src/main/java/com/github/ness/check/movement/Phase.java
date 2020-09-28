package com.github.ness.check.movement;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

public class Phase extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);

	public Phase(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Block b = event.getTo().clone().add(0, event.getPlayer().getEyeHeight(), 0).getBlock();
        NessPlayer nessPlayer = this.player();
        if (b.getType().isOccluding() && !event.getPlayer().isInsideVehicle()
                && Utility.groundAround(event.getTo().clone()) && !nessPlayer.isTeleported()
                && nessPlayer.getMovementValues().XZDiff > 0.25) {
        	flagEvent(event);
        	//if(player().setViolation(new Violation("Phase", ""))) event.setCancelled(true);
        }
    }
}
