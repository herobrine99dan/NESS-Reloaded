package com.github.ness.check;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.ness.NessPlayer;

public class CheckManager {
	
	private Map<UUID, NessPlayer> nessPlayers = Collections.synchronizedMap(new HashMap<UUID, NessPlayer>());
	private final boolean devMode = true;
	
	public void makeNessPlayer(Player player) {
		nessPlayers.put(player.getUniqueId(), new NessPlayer(player, devMode));
	}
	
	public void removeNessPlayer(NessPlayer np) {
		nessPlayers.remove(np.getBukkitPlayer().getUniqueId());
	}
	
	public void removeNessPlayer(Player p) {
		nessPlayers.remove(p.getUniqueId());
	}
	
	public NessPlayer getNessPlayer(Player player) {
		return nessPlayers.get(player.getUniqueId());
	}
	
	public NessPlayer getNessPlayer(UUID uuid) {
		return nessPlayers.get(uuid);
	}
	
}
