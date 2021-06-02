package com.github.ness.check.aquaticupdatefix;

import org.bukkit.event.player.PlayerRiptideEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class RiptideMovementCheck extends ListeningCheck<PlayerRiptideEvent> {

	public static final ListeningCheckInfo<PlayerRiptideEvent> checkInfo = CheckInfos.forEvent(PlayerRiptideEvent.class);

	public RiptideMovementCheck(ListeningCheckFactory<?, PlayerRiptideEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerRiptideEvent event) {
		this.player().getAcquaticUpdateFixes().updateRiptideEvent();
		event.getPlayer().sendMessage("You are riptiding!");
	}

}
