package com.github.ness.check.combat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class Killaura extends ListeningCheck<EntityDamageByEntityEvent> {

	private final double maxYaw;
	private final double maxReach;
	private final double reachExpansion;
	private final double lagAccount;
	private final int angleListSize;
	private final double maxAngle;
	private final int rayTraceReachBuffer;
	private final int rayTraceHitboxBuffer;
	private double buffer;
	private final boolean useBukkitLocationForRayTrace;
	private List<Float> angleList;

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEventWithTask(EntityDamageByEntityEvent.class, PeriodicTaskInfo.syncTask(Duration.ofMillis(70)));

	public Killaura(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxYaw = this.ness().getMainConfig().getCheckSection().killaura().maxYaw();
		this.maxReach = this.ness().getMainConfig().getCheckSection().killaura().maxReach();
		this.reachExpansion = this.ness().getMainConfig().getCheckSection().killaura().reachExpansion();
		this.lagAccount = this.ness().getMainConfig().getCheckSection().killaura().lagAccount();
		this.maxAngle = this.ness().getMainConfig().getCheckSection().killaura().mainAngle();
		this.angleListSize = this.ness().getMainConfig().getCheckSection().killaura().angleListSize();
		this.rayTraceHitboxBuffer = this.ness().getMainConfig().getCheckSection().killaura().rayTraceHitboxBuffer();
		this.rayTraceReachBuffer = this.ness().getMainConfig().getCheckSection().killaura().rayTraceReachBuffer();
		this.useBukkitLocationForRayTrace = this.ness().getMainConfig().getCheckSection().killaura().useBukkitLocationForRayTrace();
		this.angleList = new ArrayList<Float>();
	}

	public interface Config {
		@DefaultInteger(360)
		double maxYaw();

		@ConfComments("Hitbox is a very aggressive check, this is why I included also a way to check the angle beetween entity and player")
		@DefaultDouble(0.6)
		double mainAngle();
		
		@ConfComments("Using a previous location can sometimes be useful. Enabling this you can remove or add more false flags, i haven't tested this")
		@DefaultBoolean(false)
		boolean useBukkitLocationForRayTrace();
		
		@ConfComments("How many values should the angleList contains?")
		@DefaultInteger(9)
		int angleListSize();
		
		@ConfComments("Choose the correct buffer for Reach check")
		@DefaultInteger(2)
		int rayTraceReachBuffer();
		
		@ConfComments("Choose the correct buffer for Hitbox check")
		@DefaultInteger(3)
		int rayTraceHitboxBuffer();

		@ConfComments("This is the max Reach allowed. The maxReach depends on this value, on lagAccount value and on reachExpansion. The real formula to calculate the maxReach is 'maxReach+reachExpansion= realMaxReach' where maxReach is this config option, reachExpansion is the config option under this and realMaxReach is the realMaxReach calculated by the RayTracer")
		@DefaultDouble(3.05)
		double maxReach();

		@ConfComments("Minecraft adds to the hitbox of the entity an expansion, that is 0.1. Due to precision errors, network errors, calculations errors and sometimes rouding errors, it is suggested to use 0.25 to have less false flags.")
		@DefaultDouble(0.25)
		double reachExpansion();

		@DefaultDouble(4)
		double lagAccount();

		@DefaultDouble(17)
		double anglePatternMaxPrecision();
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
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		final LivingEntity entity = (LivingEntity) event.getEntity();
		final NessPlayer nessPlayer = player();
		// TODO Account for lag
		double maxReach = this.maxReach;
		final Ray ray = Ray.from(nessPlayer, useBukkitLocationForRayTrace);
		final AABB aabb = AABB.from(entity, this.ness(), this.reachExpansion);
		double range = aabb.collidesD(ray, 0, 10);
		final float angle1 = (float) nessPlayer.getMovementValues().getHelper().getAngle(nessPlayer, entity);
		angleList.add(angle1);
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			maxReach = (5.5 * this.maxReach) / 3;
		}
		//nessPlayer.sendDevMessage("Reach: " + range + " Angle:1 " + angle1);
		if (range > maxReach && range < 6.5D) {
			if (++buffer > rayTraceReachBuffer) {
				flagEvent(event, "Reach: " + range);
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		if (range == -1) { //If the RayTrace doesn't find an hitpoint on the entity
			if (angleList.size() > this.angleListSize) {
				double average = MathUtils.average(angleList);
				nessPlayer.sendDevMessage("Hitbox: " + average);
				if (average < maxAngle) {
					if (++angleBuffer > this.rayTraceHitboxBuffer) {
						flagEvent(event, "Hitbox");
					}
				} else if (angleBuffer > 0) {
					angleBuffer -= 0.25;
				}
			}
		} else if (angleBuffer > 0) {
			angleBuffer -= 0.5;
		}
		if (angleList.size() > this.angleListSize) { //Remove memory leaks
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
		if (!Bukkit.getVersion().contains("1.8")) { //In 1.9+ you can attack more entities without cheats!
			return;
		}
		NessPlayer nessPlayer = player();
		nessPlayer.addEntityToAttackedEntities(event.getEntity().getEntityId());
		if (nessPlayer.getAttackedEntities().size() > 2) {
			flagEvent(event, "MultiAura Entities: " + nessPlayer.getAttackedEntities().size());
		}
	}
}
