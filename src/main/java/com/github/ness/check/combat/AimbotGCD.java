package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class AimbotGCD extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);

	private double lastPitch = 0;
	private double lastYaw = 0;
	private double lastYawAcceleration = 0;
	private double lastPitchAcceleration = 0;
	List<Double> pitchAccelerations;

	public AimbotGCD(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.pitchAccelerations = new ArrayList<Double>();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
	}

}
