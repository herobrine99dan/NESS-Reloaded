package com.github.ness.check;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.AnticheatCheck;
import com.github.ness.api.ChecksManager;

import lombok.Getter;

public class CheckManager implements ChecksManager {

    private Map<UUID, NessPlayer> nessPlayers = Collections.synchronizedMap(new HashMap<UUID, NessPlayer>());
    private final boolean devMode = true;

    @Getter
    private final NessAnticheat ness;

    public CheckManager(NessAnticheat nessAnticheat) {
        this.ness = nessAnticheat;
    }

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

    @Override
    public Collection<AnticheatCheck> getAllChecks() {
        return null;
    }

}
