package com.github.ness.check.aquaticupdatefix;

import java.time.Duration;

import org.bukkit.event.player.PlayerRiptideEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PeriodicTaskInfo;

public class RiptideMovementCheck extends Check {

	public static final CheckInfo checkInfo = CheckInfos.withTask(PeriodicTaskInfo.syncTask(Duration.ofMillis(50)));

	public RiptideMovementCheck(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}
	
	@Override
	protected void checkSyncPeriodic() {
		this.player().getAcquaticUpdateFixes().setRiptiding(this.player().getBukkitPlayer().isRiptiding());
	}

}
