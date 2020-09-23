package com.github.ness.check.combat;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.MathUtils;
import com.github.ness.utility.Utility;

public class Killaura extends ListeningCheck<EntityDamageByEntityEvent> {

	double maxYaw;
	double minAngle;
	double maxReach;

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(EntityDamageByEntityEvent.class, Duration.ofMillis(70));

	public Killaura(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxYaw = this.ness().getNessConfig().getCheck(this.getClass()).getDouble("maxyaw", 357);
		this.minAngle = this.ness().getNessConfig().getCheck(this.getClass()).getDouble("minangle", -0.2);
		this.maxReach = this.ness().getNessConfig().getCheck(this.getClass()).getDouble("maxreach", 3.4);
	}

	@Override
	protected void checkAsyncPeriodic() {
		this.player().attackedEntities.clear();
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager()))
			return;
		Check(e);
		Check1(e);
		Check2(e);
		Check3(e);
		Check4(e);
		Check5(e);
		Check6(e);
	}

	public void Check(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getDamager();
			Entity entity = e.getEntity();
			NessPlayer np = player();
			double range = Math.hypot(np.getMovementValues().getTo().getX() - entity.getLocation().getX(),
					np.getMovementValues().getTo().getZ() - entity.getLocation().getZ());
			double maxReach = this.maxReach;
			if (player.getGameMode().equals(GameMode.CREATIVE)) {
				maxReach = 5.5D;
			}
			if (Utility.specificBlockNear(e.getDamager().getLocation(), "water") || MathUtils
					.yawTo180F(np.getMovementValues().getTo().getYaw() - entity.getLocation().getYaw()) <= 90) {
				maxReach += 0.4D;
			}
			maxReach += (Utility.getPing(player) / 100) / 15;
			if ((range > maxReach && range < 6.5D)
					|| Utility.getDistance3D(player.getLocation(), entity.getLocation()) > 5) {
				this.punish(e, player, "Reach: " + range);
			}
		}
	}

	public void Check1(EntityDamageByEntityEvent e) {
			Player p = (Player) e.getDamager();
			final Location loc = p.getLocation();
			Bukkit.getScheduler().runTaskLater(this.ness(), () -> {
				Location loc1 = p.getLocation();
				float grade = loc.getYaw() - loc1.getYaw();
				if (Math.round(grade) > maxYaw) {
					punish(e, p, "HighYaw " + grade);
				}
			}, 2L);
	}

	public void Check2(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.getLocation().getPitch() == Math.round(player.getLocation().getPitch())) {
				punish(event, player, "PerfectAngle");
			} else if (player.getLocation().getYaw() == Math.round(player.getLocation().getYaw())) {
				punish(event, player, "PerfectAngle1");
			} else if (player.isDead()) {
				punish(event, player, "Impossible");
			}
		}
	}

	public void Check3(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (!p.hasLineOfSight(e.getEntity())) {
				Block b = p.getTargetBlock(null, 5);
				if (b.getType().isSolid() && b.getType().isOccluding()) {
					punish(e, p, "WallHit");
				}
			}
		}
	}

	public void Check4(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity().getEntityId() == e.getDamager().getEntityId()) {
				punish(e, (Player) e.getDamager(), "SelfHit");
			}
		}
	}

	public void Check5(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity() instanceof LivingEntity && Bukkit.getVersion().contains("1.8")) {
				NessPlayer nessPlayer = player();
				nessPlayer.attackedEntities.add(e.getEntity().getEntityId());
				if (nessPlayer.attackedEntities.size() > 2) {
					punish(e, (Player) e.getDamager(), "MultiAura Entities: " + nessPlayer.attackedEntities.size());
				}
			}
		}
	}

	public void Check6(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity() instanceof LivingEntity) {
				NessPlayer nessPlayer = player();
				double angle = Utility.getAngle((Player) e.getDamager(), e.getEntity().getLocation(),
						nessPlayer.getMovementValues().getTo().getDirectionVector());
				if (angle < -0.4) {
					punish(e, (Player) e.getDamager(), "HitBox");
				}
			}
		}
	}

	private void punish(EntityDamageByEntityEvent e, Player p, String module) {
		if(player().setViolation(new Violation("Killaura", module))) e.setCancelled(true);
	}

}
