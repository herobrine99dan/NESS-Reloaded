package com.github.ness.check.combat;

import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;
import com.github.ness.utility.raytracer.rays.AABB;
import com.github.ness.utility.raytracer.rays.Ray;

public class ReachCheck extends ListeningCheck<EntityDamageByEntityEvent> {

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEvent(EntityDamageByEntityEvent.class);

	private int buffer = 0;

	public ReachCheck(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent evt) {
		if (player().isNot(evt.getDamager()))
			return;
		Check2(evt);
	}

	/**
	 * Created on 10/24/2020 Package me.frep.vulcan.check.impl.movement.aim by frep
	 * (https://github.com/freppp/)
	 */
	protected void Check1(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player d = (Player) e.getDamager();
		LivingEntity p = (LivingEntity) e.getEntity();
		MovementValues values = player().getMovementValues();
		if (d.getAllowFlight() || d.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		double yDist = Math.abs(Utility.getEyeLocation(d).getY() - Utility.getEyeLocation(p).getY()) > 0.5
				? Math.abs(Utility.getEyeLocation(d).getY() - Utility.getEyeLocation(p).getY())
				: 0;
		double yawDiff = Math.abs(180 - Math.abs(d.getLocation().getYaw() - p.getLocation().getYaw()));
		double reach = (Utility.getEyeLocation(d).distance(p.getEyeLocation()) - yDist) - 0.32;
		if (reach > 6.5)
			return;
		double maxReach = 3.1;
		double speed = values.getXZDiff();
		// int ping = getThotPatrol().getLag().getPing(d);
		// int ping2 = getThotPatrol().getLag().getPing(p);
		if (!Utility.isMathematicallyOnGround(values.getTo().getY())) {
			maxReach += .23;
		}
		maxReach += yawDiff > 100 && yDist < 0.1 ? yawDiff * 0.01 : yawDiff * 0.001;
		maxReach += speed * .58;
		maxReach += yDist * .75;
		// maxReach += ((ping + ping2) / 2) * 0.0034;
		for (PotionEffect effect : d.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.SPEED)) {
				int level = effect.getAmplifier() + 1;
				maxReach += level * .125;
			}
		}
		if (reach > maxReach) {
			buffer++;		if (buffer > 3) {
			this.flagEvent(e, "maxReach: " + maxReach);
		}
		} else if (buffer > 0) {
			buffer--;
		}
	}

	public void Check2(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getDamager();
		Ray ray = Ray.from(player);

		double dist = AABB.from((Player) e.getEntity()).collidesD(ray, 0, 10);
		if (dist != -1) {
			if (dist > 3.05) {
				player().sendDevMessage("Reach Dist: " + (float) dist);
			}
		}
	}
}
