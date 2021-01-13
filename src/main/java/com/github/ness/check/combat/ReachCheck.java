package com.github.ness.check.combat;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
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
	}
	
	private Location getEyeLocation(LivingEntity player) {
		final Location eye = player.getLocation();
		eye.setY(eye.getY() + player.getEyeHeight());
		return eye;
	}

	public void Check2(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getDamager();
		Ray ray = Ray.from(player);

		double dist = AABB.from((Player) e.getEntity(), this.ness()).collidesD(ray, 0, 10);
		if (dist != -1) {
			if (dist > 3.05) {
				player().sendDevMessage("Reach Dist: " + (float) dist);
			}
		}
	}
}
