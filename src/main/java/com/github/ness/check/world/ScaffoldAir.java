package com.github.ness.check.world;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class ScaffoldAir extends ListeningCheck<BlockPlaceEvent> {

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public ScaffoldAir(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private int airTicks = 0;
	private double buffer = 0;

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		MovementValues values = player().getMovementValues();
		Player player = e.getPlayer();
		if (!values.getHelper().isMathematicallyOnGround(player.getLocation().getY())) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		if (player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid()
				&& !player.getLocation().clone().subtract(0.0D, 2.0D, 0.0D).getBlock().getType().isSolid()
				&& airTicks < 1 && values.getXZDiff() > 0.2D && ++buffer > 1) {
			this.flag();
		} else if(buffer > 0) {
			buffer -= 0.5;
		}
	}

}