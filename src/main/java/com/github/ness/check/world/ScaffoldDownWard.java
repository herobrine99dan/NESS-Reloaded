package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class ScaffoldDownWard extends ListeningCheck<PlayerInteractEvent> {

	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos
			.forEvent(PlayerInteractEvent.class);

	public ScaffoldDownWard(ListeningCheckFactory<?, PlayerInteractEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	/**
	 * @author Frap
	 * Check from
	 * https://github.com/freppp/ThotPatrol/blob/master/src/main/java/me/frep/thotpatrol/checks/player/scaffold/ScaffoldA.java
	 */
	protected void checkEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block below = p.getLocation().subtract(0, 1, 0).getBlock();
            if (e.getClickedBlock().equals(below) && e.getBlockFace().equals(BlockFace.DOWN)) {
            	this.flagEvent(e);
            }
        }
	}
}
