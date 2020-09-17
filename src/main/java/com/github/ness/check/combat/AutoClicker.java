package com.github.ness.check.combat;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class AutoClicker extends AbstractCheck<PlayerInteractEvent> {

	int maxCPS;

	public static final CheckInfo<PlayerInteractEvent> checkInfo = CheckInfo.eventWithAsyncPeriodic(PlayerInteractEvent.class, 1, TimeUnit.SECONDS);
	private int CPS; // For AutoClicker
	public AutoClicker(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.maxCPS = this.ness().getNessConfig().getCheck(AutoClicker.class).getInt("maxCPS", 18);
		this.CPS = 0;
	}

	@Override
	protected void checkEvent(PlayerInteractEvent e) {
		check(e);
	}

	private void check(PlayerInteractEvent e) {
		Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			NessPlayer player = this.player();
			CPS++;
			if (CPS > maxCPS && !e.getPlayer().getTargetBlock(null, 5).getType().name().contains("grass")) {
				player.setViolation(new Violation("AutoClicker", "MaxCPS: " + CPS), e);
			}
		}
	}

	@Override
	protected void checkAsyncPeriodic() {
		CPS = 0;
	}
}
