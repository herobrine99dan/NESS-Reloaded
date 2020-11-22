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
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.MathUtils;
import com.github.ness.utility.Utility;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class Killaura extends ListeningCheck<EntityDamageByEntityEvent> {

	double maxYaw;
	double minAngle;
	double maxReach;

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(EntityDamageByEntityEvent.class, Duration.ofMillis(70));
	
	public Killaura(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxYaw = this.ness().getMainConfig().getCheckSection().killaura().maxYaw();
		this.minAngle = this.ness().getMainConfig().getCheckSection().killaura().minAngle();
		this.maxReach = this.ness().getMainConfig().getCheckSection().killaura().maxReach();
	}
	
	public interface Config {
		@DefaultInteger(360)
		double maxYaw();
		@DefaultDouble(-0.2)
		double minAngle();
		@DefaultDouble(3.4)
		double maxReach();
	}

	@Override
	protected void checkAsyncPeriodic() {
		this.player().getAttackedEntities().clear();
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

	public void Check(EntityDamageByEntityEvent eventt) {
		Player player = (Player) eventt.getDamager();
		Entity entity = eventt.getEntity();
		NessPlayer np = player();
		double range = Math.hypot(np.getMovementValues().getTo().getX() - entity.getLocation().getX(),
				np.getMovementValues().getTo().getZ() - entity.getLocation().getZ());
		double maxReach = this.maxReach;
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			maxReach = 5.5D;
		}
		if (Utility.specificBlockNear(eventt.getDamager().getLocation(), "water") || MathUtils
				.yawTo180F(np.getMovementValues().getTo().getYaw() - entity.getLocation().getYaw()) <= 90) {
			maxReach += 0.4D;
		}
		maxReach += (Utility.getPing(player) / 100) / 15;
		if ((range > maxReach && range < 6.5D)
				|| Utility.getDistance3D(player.getLocation(), entity.getLocation()) > 5) {
			punish(eventt, "Reach: " + range);
		}
	}

	public void Check1(EntityDamageByEntityEvent event) {
		Player player = (Player) event.getDamager();
		final Location loc = player.getLocation();

		runTaskLater(() -> {
			Location loc1 = player.getLocation();
			float grade = loc.getYaw() - loc1.getYaw();
			if (Math.round(grade) > maxYaw) {
				punish(event, "HighYaw " + grade);
			}
		}, durationOfTicks(2));
	}

	public void Check2(EntityDamageByEntityEvent event) {
		Player player = (Player) event.getDamager();
		if (player.getLocation().getPitch() == Math.round(player.getLocation().getPitch())) {
			punish(event, "PerfectAngle");
		} else if (player.getLocation().getYaw() == Math.round(player.getLocation().getYaw())) {
			punish(event, "PerfectAngle1");
		} else if (player.isDead()) {
			punish(event, "Impossible");
		}
	}

	public void Check3(EntityDamageByEntityEvent event) {
		Player player = (Player) event.getDamager();
		if (player.hasLineOfSight(event.getEntity())) {
			return;
		}
		Block b = player.getTargetBlock(null, 5);
		if (b.getType().isSolid() && b.getType().isOccluding()) {
			punish(event, "WallHit");
		}
	}

	public void Check4(EntityDamageByEntityEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
				&& event.getEntity().getEntityId() == event.getDamager().getEntityId()) {
			punish(event, "SelfHit");
		}
	}

	public void Check5(EntityDamageByEntityEvent eventt) {
		if (!(eventt.getEntity() instanceof LivingEntity)) {
			return;
		}
		if (!Bukkit.getVersion().contains("1.8")) {
			return;
		}
		NessPlayer nessPlayer = player();
		nessPlayer.addEntityToAttackedEntities(eventt.getEntity().getEntityId());
		if (nessPlayer.getAttackedEntities().size() > 2) {
			punish(eventt, "MultiAura Entities: " + nessPlayer.getAttackedEntities().size());
		}
	}

	public void Check6(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		NessPlayer nessPlayer = player();
		double angle = Utility.getAngle((Player) event.getDamager(), event.getEntity().getLocation(),
				nessPlayer.getMovementValues().getTo().getDirectionVector());
		if (angle < -0.4) {
			punish(event, "HitBox");
		}
	}

	private void punish(EntityDamageByEntityEvent event, String module) {
		flagEvent(event, module);
	}

}
