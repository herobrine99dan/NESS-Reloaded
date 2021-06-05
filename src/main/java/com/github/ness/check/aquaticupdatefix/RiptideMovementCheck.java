package com.github.ness.check.aquaticupdatefix;

import java.time.Duration;

import org.bukkit.event.player.PlayerRiptideEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;

public class RiptideMovementCheck extends ListeningCheck<PlayerRiptideEvent> {

	public static final ListeningCheckInfo<PlayerRiptideEvent> checkInfo = CheckInfos
			.forEventWithTask(PlayerRiptideEvent.class, PeriodicTaskInfo.syncTask(Duration.ofMillis(50)));

	public RiptideMovementCheck(ListeningCheckFactory<?, PlayerRiptideEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerRiptideEvent event) {
		this.player().getAcquaticUpdateFixes().updateRiptideEvent();

	}

	@Override
	protected void checkSyncPeriodic() {
		this.player().getAcquaticUpdateFixes().setRiptiding(this.player().getBukkitPlayer().isRiptiding());
		//if(this.player().getAcquaticUpdateFixes().isRiptiding()) this.player().sendDevMessage("Is Riptiding: " + this.player().getAcquaticUpdateFixes().isRiptiding());
	}

}
