package com.github.ness.check.misc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class GhostBlockFixer extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public GhostBlockFixer(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = this.player().getMovementValues();
		float xzDiff = (float) values.getXZDiff();
		float yDiff = (float) values.getyDiff();

		Player player = event.getPlayer();
		int range = 2;
		for(int x = -range; x <= range; x++) {
			for(int y = -range; y <= range; y++) {
				for(int z = -range; z <= range; z++) {
					Location block = new Location(player.getWorld(), x, y, z);
					player.sendBlockChange(block, block.getBlock().getType(), block.getBlock().getData());
				}
			}
		}
	}
}
