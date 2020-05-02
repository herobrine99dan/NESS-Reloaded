package com.github.ness;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.github.ness.annotation.SyncOnly;

import lombok.Getter;
import lombok.Setter;

public class NessPlayer implements AutoCloseable {

	@Getter
	@Setter
	private volatile Violation violation;
	@Getter
	@Setter
	private boolean moved = false;
	@Getter
	@Setter
	private double anglekillauramachinelearning = 0.0;
	@Getter
	@Setter
	public List<Float> patterns = new ArrayList<Float>();
	@Getter
	@Setter
	double distance = 0.0;
	@Getter
	@Setter
	int onMoveRepeat = 0;
	@Getter
	@Setter
	double YawDelta = 0.0;
	@Getter
	@Setter
	int clicks = 0;
	@Getter
	@Setter
	double oldY = 0;
	@Getter
	@Setter
	int packets =0;
	@Getter
	@Setter
	int drop = 0;
	@Getter
	@Setter
	int blockplace = 0;
	@Getter
	@Setter
	int onmoverepeat = 0;
	@SyncOnly
	private final Player player;

	@Getter
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();

	public NessPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void close() {

	}

}
