package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class Spider extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public Spider(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double lastDTG;

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = player().getMovementValues();
		Player player = e.getPlayer();
		Location to = e.getTo();
		Location from = e.getFrom();
		double dTG = values.getdTG();
		String dTGString = Double.toString(dTG);
		if (to.getY() > from.getY()) {
			String diff = Double.toString(Math.abs(dTG - lastDTG));
			if (player.getLocation().getY() % 0.5D != 0.0D && !player.isFlying()
					&& !to.clone().add(0, -1, 0).getBlock().getType().isSolid() && dTGString.contains("99999999")
					|| dTGString.contains("00000000") || diff.contains("000000")
					|| diff.startsWith("0.286") && this.player().milliSecondTimeDifference(PlayerAction.ATTACK) > 500) {
				if (!values.isAroundLiquids()) {
					this.flagEvent(e);
				}
			}
			lastDTG = dTG;
		}
	}

}
