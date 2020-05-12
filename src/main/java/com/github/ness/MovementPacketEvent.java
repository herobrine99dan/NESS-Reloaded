package com.github.ness;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;

public class MovementPacketEvent {
	@Getter
	Location to;
	@Getter
	Location from;
	@Getter
	Player player;

	public MovementPacketEvent(Player sender, Location froml, Location tol) {
		to = tol;
		from = froml;
	}

}
