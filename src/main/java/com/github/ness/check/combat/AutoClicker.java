package com.github.ness.check.combat;

import com.github.ness.check.CheckManager;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class AutoClicker extends AbstractCheck<PlayerInteractEvent> {

	int maxCPS;

	public static final CheckInfo<PlayerInteractEvent> checkInfo = CheckInfo.eventOnly(PlayerInteractEvent.class);

	public AutoClicker(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.maxCPS = this.manager.getNess().getNessConfig().getCheck(AutoClicker.class).getInt("maxCPS", 18);
	}

	@Override
	protected void checkEvent(PlayerInteractEvent e) {
		check(e);
	}

	private void check(PlayerInteractEvent e) {
		Action action = e.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			NessPlayer player = this.player();
			player.CPS++;
			if (player.CPS > maxCPS && !e.getPlayer().getTargetBlock(null, 5).getType().name().contains("grass")) {
				player.setViolation(new Violation("AutoClicker", "MaxCPS: " + player.CPS), e);
			}
		}
	}

	@Override
	protected void checkAsyncPeriodic() {
		player().CPS = 0;
	}
}
