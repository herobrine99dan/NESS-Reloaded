package com.github.ness;

import java.awt.Point;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ness.api.Violation;
import com.github.ness.api.impl.PlayerViolationEvent;
import com.github.ness.check.AbstractCheck;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.DiscordWebhook;
import com.github.ness.utility.Utility;

import lombok.Getter;
import lombok.Setter;

public class NessPlayer implements AutoCloseable {

    /**
     * Used by ViolationManager to count violations of specific checks. <br>
     * This map is synchronized and thread safe
     */
    public final Map<String, Integer> checkViolationCounts = new ConcurrentHashMap<>();
    /**
     * Player's current violation, package visibility for ViolationManager to use
     */
    public final AtomicReference<Violation> violation = new AtomicReference<>();
    
    /**
     * Player UUID
     */
    private final UUID uuid;
    /**
     * Bukkit Player corresponding to this NESSPlayer
     */
    @Getter
    private final Player player;
    // Used for AutoClick check
    @Getter
    private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();
    @Getter
    private final boolean devMode;
    public double lastStrafeAngle; // For the Beta NewOldStrafe Check
    public int blockPlace; // For FastPlace Check
    public int normalPacketsCounter; // For MorePackets
    public int CPS; // For AutoClicker
    public long lastPacketTime; // Used in BadPackets
    public long movementPackets; // Used in BadPackets
    public double lastStrafeDist; // Used in Strafe
    public float lastStairDist; // Used in BadPackets
    public int noGround; // Used in NoGround Check
    public long pingspooftimer; // For PingSpoof
    public long oldpingspooftimer; // For PingSpoof
    public float lastYaw; // For Aimbot
    public double distanceFromGround; // Updated from OldMovementsCheck
    public double flyYSum; // The sum beetween positive y values
    public double sensitivity; // The Player Sensitivity
    public float lastPacketsPerTicks; // Used in BadPackets
    final public Map<PlayerAction, Object> actionTime;
    public List<Point> mouseRecordValues;
    public ImmutableLoc safeLocation;
    public int airTicks;
    public double lastSpeedDist;
    public ImmutableLoc velocity;
    public Set<Integer> attackedEntities;
    public ImmutableLoc lastEntityAttackedLoc;
    public boolean hasSetback;
    public long moveInvItemsLastTime;
    public int movedInvItems;
    // Used for Aimbot check
    public List<Float> pitchDiff;
    public double lastGCD = 0;
    public double lastSpeedPredictionDist; // For Speed Prediction
    // Used in OldMovementChecks
    public boolean lastSpeedPredictionOnGround; // For Speed Prediction
    long setBackTicks;
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
    private final Set<AbstractCheck<?>> checksActivated;

    public NessPlayer(Player player, boolean devMode) {
    	uuid = player.getUniqueId();
        this.player = player;
        this.teleported = false;
        this.lastPacketTime = 0;
        checksActivated = new HashSet<AbstractCheck<?>>();
        this.blockPlace = 0;
        this.lastEntityAttackedLoc = new ImmutableLoc(player.getWorld().getName(), 0, 0, 0, 0, 0);
        this.CPS = 0;
        this.setBackTicks = 0;
        this.mouseRecordValues = new ArrayList<Point>();
        this.actionTime = Collections.synchronizedMap(new EnumMap<>(PlayerAction.class));
        this.pitchDiff = new ArrayList<Float>();
        this.normalPacketsCounter = 0;
        this.sensitivity = 0;
        this.devMode = devMode;
        this.attackedEntities = new HashSet<Integer>();
        this.movementValues = new MovementValues(player,
                new ImmutableLoc(player.getWorld().getName(), 0d, 0d, 0d, 0f, 0d),
                new ImmutableLoc(player.getWorld().getName(), 0d, 0d, 0d, 0f, 0d));
    }
    
    public UUID getUUID() {
    	return uuid;
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

    public long nanoTimeDifference(PlayerAction action) {
        return (System.nanoTime() - (Long)this.actionTime.getOrDefault(action, (long) 0)) / 1000_000L;
    }

    public void onClientTick() {
        this.attackedEntities.clear();
    }

    public void updateMovementValue(MovementValues values) {
        if (!Utility.isOnGround(values.getTo().toBukkitLocation().clone())) {
            airTicks++;
        } else {
            airTicks = 0;
        }
        this.movementValues = values;
    }

    /**
     * Gets the player's current, latest violation
     *
     * @return the latest violation
     */
    public Violation getViolation() {
        return violation.get();
    }

    /**
     * /** Used to indicate the player was detected for cheating
     *
     * @param violation the violation
     * @param e         the event (if it is null, this check will not be cancelled)
     */
    public void setViolation(Violation violation, Cancellable e) {
        // Bypass permissions
        if (this.getPlayer().hasPermission("ness.bypass." + violation.getCheck().toLowerCase())
                || this.getPlayer().hasPermission("ness.bypass.*") || this.getPlayer().isOp()) {
            return;
        }
        // Violation event
        PlayerViolationEvent event = new PlayerViolationEvent(this.getPlayer(), this, violation,
                checkViolationCounts.getOrDefault(violation.getCheck(), 0));
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        // Cancel method
        ConfigurationSection cancelsec = NESSAnticheat.main.getNessConfig().getViolationHandling()
                .getConfigurationSection("cancel");
        final boolean cancel = checkViolationCounts.getOrDefault(violation.getCheck(), 0) > cancelsec.getInt("vl", 10)
                && cancelsec.getBoolean("enable", false);
        if (cancel) {
            if (e != null) {
                if (violation.getCheck().equals("Fly") || violation.getCheck().equals("NoFall")) {
                    this.dragDown();
                } else {
                    e.setCancelled(true);
                }
            }
        }
        // Main method body
        this.violation.compareAndSet(null, violation);
        checkViolationCounts.merge(violation.getCheck(), 1, (c1, c2) -> c1 + c2);
        if (isDevMode()) {
            // sendMessage is thread safe
            if (this.getPlayer().hasPermission("ness.notify.developer")) {
                player.sendMessage(
                        "Dev mode violation: Check " + violation.getCheck() + ". Details: " + violation.getDetails());
            }
        }
    }

    /**
     * The new dragDown method will teleport the player down adding his velocity to
     * his location
     */
    public void dragDown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    final long current = System.nanoTime() / 1000_000L;
                    if ((current - setBackTicks) > 40) {
                        final Location block = player.getLocation().clone().add(0, player.getVelocity().getY(), 0);
                        if (!block.getBlock().getType().isSolid()) {
                            hasSetback = true;
                            player.teleport(block, TeleportCause.PLUGIN);
                        } else if (!block.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                            player.teleport(block.add(0, 0.4, 0), TeleportCause.PLUGIN);
                        }
                    }
                    setBackTicks = current;
                    setBackTicks++;
                }
            }
        }.runTask(NESSAnticheat.getInstance());
    }

    /**
     * This method send a webhook with the violation message to Discord
     *
     * @param violation
     * @param violationCount
     */
    public void sendWebhook(Violation violation, int violationCount) {
        final String webhookurl = NESSAnticheat.getInstance().getNessConfig().getDiscordWebHook();
        if (webhookurl == null || webhookurl.isEmpty()) {
            return;
        }
        NESSConfig config = NESSAnticheat.getInstance().getNessConfig();
        new BukkitRunnable() {
            @Override
            public void run() {
                DiscordWebhook webhook = new DiscordWebhook(webhookurl);
                Player hacker = NessPlayer.this.getPlayer();
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle(config.getDiscordTitle())
                        .setDescription(config.getDiscordDescription().replaceFirst("<hacker>", hacker.getName()))
                        .setColor(config.getDiscordColor()).addField("Cheater", hacker.getName(), true)
                        .addField("Cheat", violation.getCheck() + "(module)".replace("module", violation.getDetails()),
                                true)
                        .addField("VL", Integer.toString(violationCount), false));
                // webhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription("Player
                // hacker seems to be use cheat(module)".replace("cheat", hack)
                // .replace("module", module).replace("hacker", hacker.getName())));
                try {
                    webhook.execute();
                } catch (IOException e) {
                }
            }
        }.runTaskAsynchronously(NESSAnticheat.getInstance());
    }

    @Override
    public void close() {
        checkViolationCounts.clear();
    }
}