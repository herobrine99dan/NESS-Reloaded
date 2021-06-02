package com.github.ness.check.combat;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class KillauraQuickHit extends ListeningCheck<EntityDamageByEntityEvent> {
	private Entity lastEntityHitted;
	private Location lastHitLoc;
	private long lastHit = System.nanoTime();
	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
			.forEvent(EntityDamageByEntityEvent.class);

	public KillauraQuickHit(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
		super(factory, player);
	}
	
	@Override
	protected void checkEvent(final EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager()))
			return;
		if (!(e.getEntity() instanceof LivingEntity)) {
			return;
		}
		Entity currentEntity = e.getEntity();
		if (lastEntityHitted != null && lastEntityHitted.equals(currentEntity)) {
			Double dist = currentEntity.getLocation().distance(lastHitLoc);
			long delay = (long) ((System.nanoTime() - lastHit) / 1e+6);
			if (delay <= 100 && dist > .23) {
				this.flagEvent(e, "delay: " + delay);
			}
		}
		lastHit = System.nanoTime();
		lastEntityHitted = currentEntity;
		lastHitLoc = currentEntity.getLocation();
	}
}
