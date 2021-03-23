package com.github.ness.check.combat;

import java.time.Duration;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;

import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class MaxCPS extends ListeningCheck<PlayerInteractEvent> {

	private final int maxCPS;

	public static final ListeningCheckInfo<PlayerInteractEvent> checkInfo = CheckInfos.forEventWithTask(PlayerInteractEvent.class, PeriodicTaskInfo.asyncTask(Duration.ofSeconds(1)));
	private int CPS; // For AutoClicker

	public MaxCPS(ListeningCheckFactory<?, PlayerInteractEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxCPS = this.ness().getMainConfig().getCheckSection().maxCps().maxCPS();
		this.CPS = 0;
	}

	@Override
	protected void checkEvent(PlayerInteractEvent e) {
		check(e);
	}

	private void check(PlayerInteractEvent e) {
		Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
			CPS++;
			if (CPS > maxCPS && !e.getPlayer().getTargetBlock(null, 5).getType().name().contains("grass")) {
				flagEvent(e, " CPS: " + CPS);
			}
		}
	}

	@Override
	protected void checkSyncPeriodic() {
		CPS = 0;
	}

	public interface Config {
		@DefaultInteger(18)
		int maxCPS();
	}

}
