package com.github.ness;

import java.time.Duration;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.api.Violation;
import com.github.ness.check.Check;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.NetworkReflection;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

public class NessPlayer {

    private final Queue<Violation> infractions = new ArrayBlockingQueue<>(2);

    /**
     * Player UUID
     */
    private final UUID uuid;
    /**
     * Bukkit Player corresponding to this NESSPlayer
     */
    private final Player player;
    @Getter
    private final boolean devMode;
    @Getter
    @Setter
    private double sensitivity; // The Player Sensitivity
    private final Map<PlayerAction, Long> actionTime = Collections.synchronizedMap(new EnumMap<>(PlayerAction.class));
    @Getter
    @Setter
    private ImmutableLoc lastVelocity;
    @Getter
    private final Set<Integer> attackedEntities = new HashSet<Integer>();

    @Getter
    @Setter
    private boolean hasSetback;

    private long setBackTicks;
    @Getter
    @Setter
    private boolean teleported;
    @Getter
    private volatile MovementValues movementValues;
    @Getter
    @Setter
    private boolean debugMode;
    @Getter
    @Setter
    private boolean mouseRecord; // Is the player recording?
    private long lastWasOnGround = System.nanoTime() - Duration.ofHours(1L).toNanos();
    private long lastWasOnIce = lastWasOnGround;
    @Getter
    @Setter
    private UUID lastEntityAttacked;
    /**
     * This Set contains checks that will be executed for this player
     * 
     * @since 3.0
     */
    @Getter
    private final Set<Check> checks = Collections.synchronizedSet(new HashSet<Check>());;

    public NessPlayer(Player player, boolean devMode) {
        uuid = player.getUniqueId();
        this.player = player;
        this.devMode = devMode;
        this.movementValues = new MovementValues(player,
                new ImmutableLoc(player.getWorld().getName(), 0d, 0d, 0d, 0f, 0d),
                new ImmutableLoc(player.getWorld().getName(), 0d, 0d, 0d, 0f, 0d));
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Player getBukkitPlayer() {
        return player;
    }

    /*
     * Infraction methods
     */

    /**
     * Adds a violation. If this player has too many violations, {@code false} is
     * returned
     * 
     * @param infraction the infraction
     * @return true if the infraction was added, false if the queue size was reached
     */
    public boolean addViolation(Violation violation) {
        return infractions.offer(violation);
    }

    public void addEntityToAttackedEntities(int id) {
        this.attackedEntities.add(id);
    }

    /**
     * Set the Time of an Action for a player
     * 
     * @param action
     */
    public void setPlayerAction(PlayerAction action) {
        actionTime.put(action, System.nanoTime());
    }

    /**
     * Add a Check to the CheckList Set
     * 
     * @param action
     */
    public void addCheck(Check c) {
        this.checks.add(c);
    }

    /*
     * Convenience methods
     */

    public boolean is(Player other) {
        return uuid.equals(other.getUniqueId());
    }

    public boolean is(Entity entity) {
        return entity instanceof Player && is((Player) entity);
    }

    public boolean isNot(Player other) {
        return !is(other);
    }

    public boolean isNot(Entity entity) {
        return !is(entity);
    }

    /*
     * Thread safe disconnection
     */

    /**
     * Disconnects the player just as the server would
     * 
     */
    public void kickThreadSafe() {
        Object networkManager = NetworkReflection.getNetworkManager(getBukkitPlayer());
        NetworkReflection.getChannel(networkManager).config().setAutoRead(false);
        // NetworkReflection.clearPacketQueue(networkManager);
    }

    /*
     * Ground, Ice, and MovementValues
     */

    public long getTimeSinceLastWasOnGround() {
        return (System.nanoTime() - lastWasOnGround) / 1000_000L; // want milliseconds
    }

    public void updateLastWasOnGround() {
        lastWasOnGround = System.nanoTime();
    }

    public long getTimeSinceLastWasOnIce() {
        return (System.nanoTime() - lastWasOnIce) / 1000_000L; // want milliseconds
    }

    public void updateLastWasOnIce() {
        lastWasOnIce = System.nanoTime();
    }

    public long milliSecondTimeDifference(PlayerAction action) {
        return (System.nanoTime() - this.actionTime.getOrDefault(action, (long) 0)) / 1000_000L;
    }

    // This will called everytime a player sends a flying packet
    public void onClientTick() {
        this.attackedEntities.clear();
    }

    public void sendDevMessage(String message) {
        this.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Dev> &7" + message));
    }

    public void updateMovementValue(MovementValues values) {
        this.movementValues = values;
        boolean ice = false;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Material belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -1, z)).getType();
                if (belowSel.name().contains("PISTON") || belowSel.name().contains("ICE")) {
                    ice = true;
                }
                belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -.01, z)).getType();
                if (belowSel.isSolid()) {
                    this.updateLastWasOnGround();
                }
            }
        }
        if (ice) {
            this.updateLastWasOnIce();
        }
    }

    /**
     * Drags down the player
     * 
     */
    public void completeDragDown() {
        //this.checks.iterator().next().manager().getNess().getSyncScheduler().addAction(r);
        if (!player.isOnline()) {
            return;
        }
        final long current = System.nanoTime() / 1000_000L;
        if ((current - setBackTicks) > 40) {
            double ytoAdd = player.getVelocity().getY();
            if (ytoAdd > 0) {
                return;
            }
            final Location block = player.getLocation().clone().add(0, ytoAdd, 0);

            for (int i = 0; i < 10; i++) {
                if (block.getBlock().getType().isSolid()) {
                    block.add(0, 0.1, 0);
                } else {
                    break;
                }
            }
            player.teleport(block, TeleportCause.PLUGIN);
        }
        hasSetback = true;
        setBackTicks = current;
        setBackTicks++;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + uuid.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof NessPlayer)) {
            return false;
        }
        NessPlayer other = (NessPlayer) object;
        return uuid.equals(other.uuid);
    }

}