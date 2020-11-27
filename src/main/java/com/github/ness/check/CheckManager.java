package com.github.ness.check;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.ness.NessAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;

import lombok.Getter;

public class CheckManager implements Listener {

    private Map<UUID, NessPlayer> nessPlayers = Collections.synchronizedMap(new HashMap<UUID, NessPlayer>());
    private final boolean devMode = true;
    @Getter
    private final Set<CheckFactory> checkList = Collections.synchronizedSet(new HashSet<CheckFactory>());

    @Getter
    private final NessAnticheat ness;
    private static final Logger logger = NessLogger.getLogger(CheckManager.class);

    public CheckManager(NessAnticheat nessAnticheat) {
        this.ness = nessAnticheat;

    }

    public void initialize() {
        final List<String> checks = CheckManager.this.getNess().getPlugin().getConfig().getStringList("enabled-checks");
        final ChecksPackage[] packs = ChecksPackage.values();
        checks.addAll(Arrays.asList(ChecksPackage.REQUIRED_CHECKS));
        logger.finer("Checks: " + checks);
        checkList.clear();
        ness.getExecutor().execute(new Runnable() {

            @Override
            public void run() {
                for (String s : checks) {
                    boolean founded = false;
                    for (ChecksPackage pack : packs) {
                        try {
                            Class<?> clazz = Class.forName("com.github.ness.check." + pack.prefix() + "." + s);
                            CheckFactory factory = new CheckFactory(clazz);
                            CheckManager.this.checkList.add(factory); //
                            logger.fine("CheckFactory with name: " + s + " was loaded correctly!");
                            founded = true;
                        } catch (Exception e) {
                            if (!(e instanceof ClassNotFoundException)) {
                                throw new IllegalStateException("There was an error while loading CheckFactory " + s
                                        + " (Is the Constructor public?)", e);
                            }
                        }
                    }
                    if (!founded) {
                        logger.warning("CheckFactory with name: " + s + " wasn't found!");
                    }
                }
            }
        });

    }

    public Object onEvent(ReceivedPacketEvent event) {
        for (NessPlayer np : nessPlayers.values()) {
            for (Check c : np.getChecks()) {
                if (c.player().isNot(event.getNessPlayer().getBukkitPlayer())) {
                    return null;
                }
                try {
                    if (event.getPacket().isFlying() || event.getPacket().isPosition()
                            || event.getPacket().isRotation()) {
                        c.onFlying((FlyingEvent) event);
                    }
                    double x = 0, y = 0, z = 0, yaw = 0, pitch = 0; //TODO Test new change
                    if (event.getPacket().isPosition()) {
                        FlyingEvent e = (FlyingEvent) event;
                        x = e.getX();
                        y = e.getY();
                        z = e.getZ();
                    }
                    if (event.getPacket().isRotation()) {
                        FlyingEvent e = (FlyingEvent) event;
                        yaw = e.getYaw();
                        pitch = e.getPitch();
                    }
                    if (event.getPacket().isPosition() || event.getPacket().isRotation()) {
                        np.setFromLoc(np.getToLoc());
                        np.setToLoc(new ImmutableLoc(np.getFromLoc().getWorld(), x, y, z, (float) yaw, pitch));
                    }
                    if (event.getPacket().isUseEntity()) {
                        c.onUseEntity((UseEntityEvent) event);
                    }
                    c.onEveryPacket(event);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "There was an exception while executing the check " + c.getCheckName(),
                            ex);
                }
            }
        }
        return event;
    }

    public void makeNessPlayer(Player player) {
        NessPlayer nessPlayer = new NessPlayer(player, devMode, this.getNess().getMaterialAccess());
        ness.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (CheckFactory c : checkList) {
                    try {
                        Check check = c.makeEqualCheck(nessPlayer);
                        nessPlayer.addCheck(check);
                        logger.finer("Adding Check: " + check.getCheckName() + " to: "
                                + nessPlayer.getBukkitPlayer().getName());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | SecurityException e) {
                        logger.log(Level.SEVERE,
                                "There was an exception while enabling the check: " + c.getClazz().getName(), e);
                    }
                }
            }
        });
        nessPlayers.put(player.getUniqueId(), nessPlayer);
    }

    public void removeNessPlayer(NessPlayer np) {
        logger.finer("Removing the NessPlayer object with name" + np.getBukkitPlayer().getName());
        nessPlayers.remove(np.getBukkitPlayer().getUniqueId());
    }

    public void removeNessPlayer(Player p) {
        logger.finer("Removing the NessPlayer object with name" + p.getName());
        nessPlayers.remove(p.getUniqueId());
    }

    public NessPlayer getNessPlayer(UUID uuid) {
        return nessPlayers.get(uuid);
    }

    public Object reload() {
        // TODO Auto-generated method stub
        return null;
    }
}
