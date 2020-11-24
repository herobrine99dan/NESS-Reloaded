package com.github.ness.check;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.packet.Timer;
import com.github.ness.packets.ReceivedPacketEvent;

import lombok.Getter;

public class CheckManager implements Listener {

    private Map<UUID, NessPlayer> nessPlayers = Collections.synchronizedMap(new HashMap<UUID, NessPlayer>());
    private final boolean devMode = true;
    @Getter
    private final Set<Check> checkList = new HashSet<Check>();

    @Getter
    private final NessAnticheat ness;

    public CheckManager(NessAnticheat nessAnticheat) {
        this.ness = nessAnticheat;
        //this.listenToAllEvents();
       // this.checkList.add(new FlyHighDistance(null, this));
        //this.checkList.add(new NoFall(null, this));
        this.checkList.add(new Timer(null, this));
        //this.checkList.add(new Speed(null, this));
        //this.checkList.add(new ScaffoldAngle(null, this));
        //this.checkList.add(new ScaffoldFalseTarget(null, this));
    }

    /*public void listenToAllEvents() {
        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> onEvent(event),
                EventPriority.NORMAL, ness.getPlugin(), false);
        for (HandlerList handler : HandlerList.getHandlerLists()) {
            handler.register(registeredListener);
        }
    }*/

    public Object onEvent(ReceivedPacketEvent event) {
        for (NessPlayer np : nessPlayers.values()) {
            for (Check c : np.getChecks()) {
                c.checkEvent(event);
            }
        }
        return event;
    }

    public void makeNessPlayer(Player player) {
        NessPlayer nessPlayer = new NessPlayer(player, devMode);
        for (Check c : this.checkList) {
            try {
                nessPlayer.addCheck(c.makeEqualCheck(nessPlayer));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | SecurityException e) {
                e.printStackTrace();
            }
            System.out.println("Adding Check: " + c.getCheckName() + " to: " + nessPlayer.getBukkitPlayer().getName());
        }
        nessPlayers.put(player.getUniqueId(), nessPlayer);
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

    public Object reload() {
        // TODO Auto-generated method stub
        return null;
    }
}
