package com.github.ness.check.combat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import com.github.ness.check.PeriodicTaskInfo;
import com.github.ness.utility.MathUtils;
import com.github.ness.utility.raytracer.rays.AABB;
import com.github.ness.utility.raytracer.rays.Ray;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class Killaura extends ListeningCheck<EntityDamageByEntityEvent> {

	private final double maxYaw;
	private final double maxReach;
	private final double reachExpansion;
	private final double lagAccount;
	private final double maxAngle;
	private double buffer;
	List<Float> angleList;

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEventWithTask(EntityDamageByEntityEvent.class, PeriodicTaskInfo.syncTask(Duration.ofMillis(70)));

	public Killaura(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxYaw = this.ness().getMainConfig().getCheckSection().killaura().maxYaw();
		this.maxReach = this.ness().getMainConfig().getCheckSection().killaura().maxReach();
		this.reachExpansion = this.ness().getMainConfig().getCheckSection().killaura().reachExpansion();
		this.lagAccount = this.ness().getMainConfig().getCheckSection().killaura().lagAccount();
		this.maxAngle = this.ness().getMainConfig().getCheckSection().killaura().mainAngle();
		this.angleList = new ArrayList<Float>();
	}

	public interface Config {
		@DefaultInteger(360)
		double maxYaw();

		@DefaultDouble(0.6)
		double mainAngle();

		@DefaultDouble(-0.2)
		double minAngle();

		@DefaultDouble(4)
		double maxReach();

		@DefaultDouble(0.2)
		double reachExpansion();

		@DefaultDouble(4)
		double lagAccount();
	}

	@Override
	protected void checkSyncPeriodic() {
		this.player().getAttackedEntities().clear();
	}

	@Override
	protected void checkEvent(final EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager()))
			return;
		checkReach(e);
		Check1(e);
		Check2(e);
		Check3(e);
		Check4(e);
		Check5(e);
	}

	public void checkReach(final EntityDamageByEntityEvent event) {
		final Player player = (Player) event.getDamager();
		final Entity entity = event.getEntity();
		final NessPlayer nessPlayer = player();
		// TODO Account for lag
		double maxReach = this.maxReach;
		final Ray ray = Ray.from(nessPlayer);
		final AABB aabb = AABB.from(entity, this.ness(), this.reachExpansion);
		double range = aabb.collidesD(ray, 0, 10);
		angleList.add((float) nessPlayer.getMovementValues().getHelper().getAngle(nessPlayer, entity.getLocation()));
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			maxReach = (5.5 * this.maxReach) / 3;
		}
		nessPlayer.sendDevMessage("Reach: " + range);
		if (range > maxReach && range < 6.5D) {
			if (++buffer > 2) {
				punish(event, "Reach: " + range);
			}
		} else if (range == -1) {
			double average = MathUtils.average(angleList);
			if (average < maxAngle) {
				if (++buffer > 3) {
					punish(event, "Hitbox");
				}
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		if (angleList.size() > 10) {
			angleList.clear();
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
		if ((player.getLocation().getPitch() == Math.round(player.getLocation().getPitch()))
				&& Math.abs(player.getLocation().getPitch()) < 90) {
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
		Material material = b.getType();
		if (b.getType().isSolid() && (material.isOccluding() && !material.name().contains("GLASS"))) {
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

	private void punish(EntityDamageByEntityEvent event, String module) {
		flagEvent(event, module);
	}
}
