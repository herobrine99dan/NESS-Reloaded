package com.github.ness.check.combat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.MathUtils;

public class Killaura extends Check {

    double maxYaw = 360;
    double minAngle = -0.2;
    double maxReach = 3.4;

    public Killaura(NessPlayer nessPlayer) {
        super(Killaura.class, nessPlayer, true, 70);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void checkAsyncPeriodic() {
        this.player().getAttackedEntities().clear();
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
        Check(e);
        Check1(e);
        Check2(e);
        Check3(e);
        Check4(e);
        Check5(e);
        Check6(e);
    }

    public void Check(UseEntityEvent eventt) {
        /*Player player = (Player) eventt.getDamager();
        Entity entity = eventt.getEntity();
        NessPlayer np = player();
        double range = Math.hypot(np.getMovementValues().getTo().getX() - entity.getLocation().getX(),
                np.getMovementValues().getTo().getZ() - entity.getLocation().getZ());
        double maxReach = this.maxReach;
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            maxReach = 5.5D;
        }
        if (Utility.specificBlockNear(eventt.getDamager().getLocation(), "water")
                || MathUtils.yawTo180F(np.getMovementValues().getTo().getYaw() - entity.getLocation().getYaw()) <= 90) {
            maxReach += 0.4D;
        }
        maxReach += (Utility.getPing(player) / 100) / 15;
        if ((range > maxReach && range < 6.5D)
                || Utility.getDistance3D(player.getLocation(), entity.getLocation()) > 5) {
            punish(eventt, "Reach: " + range);
        }*/
    }

    public void Check1(UseEntityEvent event) {
        final ImmutableLoc loc = event.getNessPlayer().getMovementValues().getTo();

        runTaskLater(() -> {
            Location loc1 = event.getNessPlayer().getBukkitPlayer().getLocation();
            float grade = Math.abs(loc.getYaw() - loc1.getYaw());
            if (Math.round(grade) > maxYaw) {
                flag(event, "HighYaw " + grade);
            }
        }, durationOfTicks(2));
    }

    public void Check2(UseEntityEvent event) {
        if (event.getNessPlayer().getMovementValues().getTo().getPitch() == Math
                .round(event.getNessPlayer().getMovementValues().getTo().getPitch())) {
            flag("PerfectAngle", event);
        } else if (event.getNessPlayer().getMovementValues().getTo().getYaw() == Math
                .round(event.getNessPlayer().getMovementValues().getTo().getYaw())) {
            flag(event, "PerfectAngle1");
        } else
            runTaskLater(() -> {
                Player player = event.getNessPlayer().getBukkitPlayer();
                if (player.isDead()) {
                    flag(event, "Impossible");
                }
            }, durationOfTicks(2));
    }

    public void Check3(UseEntityEvent event) {
        runTaskLater(() -> {
            Player player = (Player) event.getNessPlayer().getBukkitPlayer();
            if (player.hasLineOfSight(event.getPacket().getEntity())) {
                return;
            }
            Block b = player.getTargetBlock(null, 5);
            if (b.getType().isSolid() && b.getType().isOccluding()) {
                flag(event, "WallHit");
            }
        }, durationOfTicks(2));

    }

    public void Check4(UseEntityEvent event) {
        if (event.getAction().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                && event.getNessPlayer().getEntityId() == event.getEntityId()) {
            flag(event, "SelfHit");
        }
    }

    public void Check5(UseEntityEvent eventt) {
        if (!Bukkit.getVersion().contains("1.8")) {
            return;
        }
        NessPlayer nessPlayer = player();
        nessPlayer.addEntityToAttackedEntities(eventt.getEntityId());
        if (nessPlayer.getAttackedEntities().size() > 2) {
            flag(eventt, "MultiAura Entities: " + nessPlayer.getAttackedEntities().size());
        }
    }

    public void Check6(UseEntityEvent event) {
        /*if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        NessPlayer nessPlayer = player();
        Player player = (Player) event.getDamager();
        float angle = (float) Utility.getAngle(player, event.getEntity().getLocation(), makeDirection(nessPlayer));
        if (angle < -0.4) {
            punish(event, "HitBox");
        } else {
            final float vectorAngle = player.getLocation().toVector().angle(event.getEntity().getLocation().toVector());
            nessPlayer.sendDevMessage("Angle: " + angle + " VectorAngle: " + vectorAngle);
        }*/
    }

}
