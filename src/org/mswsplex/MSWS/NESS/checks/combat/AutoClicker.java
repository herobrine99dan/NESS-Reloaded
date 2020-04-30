package org.mswsplex.MSWS.NESS.checks.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mswsplex.MSWS.NESS.PlayerManager;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class AutoClicker {

	@SuppressWarnings("unchecked")
	public
	static void Check(PlayerInteractEvent event) {
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
				WarnHacks.warnHacks(player, "AutoClicker", 5 * (times - 20) + 10, -1.0, 3,"Vanilla",false);
			}
		}
	}

}
