package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import com.github.ness.check.MultipleListeningCheckFactory;
import com.github.ness.data.MovementValues;
import org.bukkit.potion.PotionEffect;

public class NoWeb extends MultipleListeningCheck {

    private float xzVelocity;
    private boolean velocityAlreadyUsed = false;
    private float lastYVelocity;
    public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class,
            PlayerVelocityEvent.class);

    public NoWeb(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
        super(factory, player);
    }

    @Override
    protected void checkEvent(Event event) {
        Player player = ((PlayerEvent) event).getPlayer();
        if (event instanceof PlayerVelocityEvent) {
            onVelocity((PlayerVelocityEvent) event);
        }
        if (event instanceof PlayerMoveEvent) {
            onMove((PlayerMoveEvent) event);
        }
    }

    private void onVelocity(PlayerVelocityEvent e) {
        xzVelocity = (float) Math.hypot(e.getVelocity().getX(), e.getVelocity().getZ());
        velocityAlreadyUsed = true;
    }

    private void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        MovementValues values = player().getMovementValues();
        double dist = values.getXZDiff();
        float walkSpeed = player.getWalkSpeed() / 2;
        float speedMultiplier = getEffectMultipliers(player) + 0.03f; //Little offset to prevent false flags
        float maxDist = walkSpeed * speedMultiplier;
        if (velocityAlreadyUsed) {
            velocityAlreadyUsed = false;
            dist -= xzVelocity;
        }
        if (e.getFrom().getBlock().getType().name().contains("WEB") && e.getTo().getBlock().getType().name().contains("WEB") && !player.isFlying()) {
            this.player().sendDevMessage("dist: " + (float) dist + " maxDist: " + maxDist);
            if (dist > maxDist) {
                this.flag("dist: " + (float) dist);
            }
        }
    }

    private float getEffectMultipliers(Player player) {
        float speed = 0;
        float slowness = 0;
        for (PotionEffect pe : player.getActivePotionEffects()) {
            String name = pe.getType().getName();
            if (name.equalsIgnoreCase("SLOW")) { // equalsIgnoreCase allows us better performance (+37%
                // of perfomance)
                slowness = pe.getAmplifier() + 1;
            }
            if (name.equalsIgnoreCase("SPEED")) {
                speed = pe.getAmplifier() + 1;
            }
        }
        float speedSlownessMultiplier = (1 + 0.2f * speed) * (1 - 0.15f * slowness);
        return speedSlownessMultiplier;
    }

}
