package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.PlayerManager;

import com.github.ness.CheckManager;
import com.github.ness.Violation;

public class AutoClicker extends AbstractCheck<PlayerInteractEvent>{
	
	public AutoClicker(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerInteractEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerInteractEvent e) {
       Check(e);
	}

	@SuppressWarnings("unchecked")
	public void Check(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Block target = player.getTargetBlock((Set<Material>) null, 5);
		if ((target.getType() == Material.AIR || target.getType().isSolid())
				&& target.getType() != Material.SLIME_BLOCK) {
			PlayerManager.addAction("clicks", player);
			List<Double> clicks = new ArrayList<Double>();
			if (PlayerManager.getInfo("clickTimes", (OfflinePlayer) player) != null) {
				clicks = (List<Double>) PlayerManager.getInfo("clickTimes", (OfflinePlayer) player);
			}
			clicks.add(Double.valueOf(System.currentTimeMillis()));
			PlayerManager.setInfo("clickTimes", (OfflinePlayer) player, clicks);
			double lastClick = 0.0;
			double lastTime = 0.0;
			int times = 0;
			for (int i = 0; i < clicks.size(); ++i) {
				final double d = clicks.get(i);
				if (d - lastClick == lastTime && lastTime < 200.0) {
					++times;
				}
				if (System.currentTimeMillis() - d > 20000.0) {
					clicks.remove(i);
				}
				lastTime = d - lastClick;
				lastClick = d;
			}
			if (times > 50) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("AutoClicker"));
			}
		}
	}

}
