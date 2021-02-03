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
import org.bukkit.util.Vector;

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

	private double angleBuffer = 0;

	public void checkReach(final EntityDamageByEntityEvent event) {
		final Player player = (Player) event.getDamager();
		final Entity entity = event.getEntity();
		final NessPlayer nessPlayer = player();
		// TODO Account for lag
		double maxReach = this.maxReach;
		final Ray ray = Ray.from(nessPlayer);
		final AABB aabb = AABB.from(entity, this.ness(), this.reachExpansion);
		double range = aabb.collidesD(ray, 0, 10);
		final float angle = (float) getAngle(entity);
		final float angle1 = (float) nessPlayer.getMovementValues().getHelper().getAngle(nessPlayer, entity.getLocation());
		angleList.add(angle);
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			maxReach = (5.5 * this.maxReach) / 3;
		}
		nessPlayer.sendDevMessage("Reach: " + range + " Angle: " + angle + " Angle:1 " + angle1);
		if (range > maxReach && range < 6.5D) {
			if (++buffer > 2) {
				flagEvent(event, "Reach: " + range);
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		if (range == -1) {
			if (angleList.size() > 9) {
				double average = MathUtils.average(angleList);
				nessPlayer.sendDevMessage("Hitbox: " + average);
				if (average < maxAngle) {
					if (++angleBuffer > 3) {
						flagEvent(event, "Hitbox");
					}
				} else if (angleBuffer > 0) {
					angleBuffer -= 0.25;
				}
			}
		} else if (angleBuffer > 0) {
			angleBuffer -= 0.5;
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
				flagEvent(event, "HighYaw");
			}
		}, durationOfTicks(2));
	}

	public void Check2(EntityDamageByEntityEvent event) {
		Player player = (Player) event.getDamager();
		if (player.isDead()) {
			flagEvent(event, "Impossible");
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
			flagEvent(event, "WallHit");
		}
	}

	public void Check4(EntityDamageByEntityEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
				&& event.getEntity().getEntityId() == event.getDamager().getEntityId()) {
			flagEvent(event, "SelfHit");
		}
	}

	public void Check5(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		if (!Bukkit.getVersion().contains("1.8")) {
			return;
		}
		NessPlayer nessPlayer = player();
		nessPlayer.addEntityToAttackedEntities(event.getEntity().getEntityId());
		if (nessPlayer.getAttackedEntities().size() > 2) {
			flagEvent(event, "MultiAura Entities: " + nessPlayer.getAttackedEntities().size());
		}
	}
	
	private float getAngle(Entity entity) {
		final Vector playerLookDir = this.player().getBukkitPlayer().getEyeLocation().getDirection();
		final Vector playerEyeLoc = this.player().getBukkitPlayer().getEyeLocation().toVector();
		final Vector playerEntityVec = entity.getLocation().toVector().subtract(playerEyeLoc);
		final float angle = playerLookDir.angle(playerEntityVec);
		return angle;
	}
}
