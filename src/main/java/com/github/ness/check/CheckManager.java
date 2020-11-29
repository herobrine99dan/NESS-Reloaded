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
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.NessEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.SendedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;

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
        checkList.clear();
        this.loadChecks(this.getNess().getNessConfig().getConfig().getBoolean("checkmanager.async-initialization", false));
    }

    public void loadChecks(boolean async) {
        final List<String> checks = this.getNess().getNessConfig().getEnabledChecks();
        final ChecksPackage[] packs = ChecksPackage.values();
        checks.addAll(Arrays.asList(ChecksPackage.REQUIRED_CHECKS));
        logger.finer("Checks: " + checks);
        Runnable runnable = new Runnable() {
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
        };
        if (async) {
            ness.getExecutor().execute(runnable);
        } else {
            runnable.run();
        }
    }

    public Object onEvent(NessEvent event) {
        for (NessPlayer nessPlayer : nessPlayers.values()) {
            for (Check c : nessPlayer.getChecks()) {
                if (c.player().isNot(event.getNessPlayer().getBukkitPlayer())) {
                    return null;
                }
                try {
                    if (event instanceof ReceivedPacketEvent) {
                        ReceivedPacketEvent packetEvent = (ReceivedPacketEvent) event;
                        if (packetEvent.getPacket().isFlying() || packetEvent.getPacket().isPosition()
                                || packetEvent.getPacket().isRotation()) {
                            c.onFlying((FlyingEvent) event);
                        }
                        double x = 0, y = 0, z = 0, yaw = 0, pitch = 0; // TODO Test new change
                        if (packetEvent.getPacket().isPosition()) {
                            FlyingEvent e = (FlyingEvent) event;
                            x = e.getX();
                            y = e.getY();
                            z = e.getZ();
                        }
                        if (packetEvent.getPacket().isRotation()) {
                            FlyingEvent e = (FlyingEvent) event;
                            yaw = e.getYaw();
                            pitch = e.getPitch();
                        }
                        if (packetEvent.getPacket().isPosition() || packetEvent.getPacket().isRotation()) {
                            nessPlayer.setFromLoc(nessPlayer.getToLoc());
                            nessPlayer.setToLoc(
                                    new ImmutableLoc(nessPlayer.getFromLoc().getWorld(), x, y, z, (float) yaw, pitch, true));
                        }
                        if (packetEvent.getPacket().isUseEntity()) {
                            c.onUseEntity((UseEntityEvent) event);
                        }
                        c.onEveryPacket(packetEvent);
                    } else if (event instanceof NessBukkitEvent) {
                        c.onBukkitEvent((NessBukkitEvent) event);
                    } else if (event instanceof SendedPacketEvent) {
                        c.onEveryPacket((SendedPacketEvent) event);
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "There was an exception while executing the check " + c.getCheckName(),
                            ex);
                }
            }
            nessPlayer.getChecksInitialized().set(true);
        }
        return event;
    }

    public void makeNessPlayer(Player player) {
        NessPlayer nessPlayer = new NessPlayer(player, devMode, this.getNess().getMaterialAccess(),
                player.getEntityId());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (CheckFactory c : checkList) {
                    try {
                        Check check = c.makeEqualCheck(nessPlayer, CheckManager.this);
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
        };
        if(this.getNess().getNessConfig().getConfig().getBoolean("async-nessplayer-initialization", true)) {
        ness.getExecutor().execute(runnable);
        } else {
            runnable.run();
        }
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

    public void reload() {
        this.getNess().getNessConfig().reloadConfiguration(ness);
        this.initialize();
    }
}
