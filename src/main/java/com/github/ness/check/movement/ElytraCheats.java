package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.MathUtils;

public class ElytraCheats extends ListeningCheck<PlayerMoveEvent> {
	private float lastXDiff, lastYDiff, lastZDiff;
	private int glideTicks;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public ElytraCheats(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (p.isGliding()) {
			glideTicks++;
		} else {
			glideTicks = 0;
		}
		if(glideTicks > 2) {
		Vector predictedMotion = computeMotion();
		float xzDiff = (float) this.player().getMovementValues().getXZDiff();
		float yDiff = (float) this.player().getMovementValues().getyDiff();
		float xzPredicted = (float) Math.hypot(predictedMotion.getX(), predictedMotion.getZ());
		float resultXZ = (float) Math.abs(xzDiff - xzPredicted);
		float resultY = (float) Math.abs(yDiff - predictedMotion.getY());
		Vector firework = new Vector(0,0,0);
		for (Entity ent : player().getBukkitPlayer().getNearbyEntities(1, 1, 1)) {
			if (ent instanceof Firework && ent.getLocation().distanceSquared(event.getFrom()) == 0.0) {
				firework.add(fireworkAdder(new Vector(lastXDiff, lastYDiff, lastZDiff)));
			}
		}
		if(firework.length() > 0) this.player().sendDevMessage("shitty firework: " + firework);
		if(resultY > 0.001 || resultXZ > 0.03) {
			this.player().sendDevMessage("resultXZ: " + resultXZ + "resultY: " + resultY);
		}
		this.lastXDiff = (float) this.player().getMovementValues().getxDiff();
		this.lastYDiff = (float) this.player().getMovementValues().getyDiff();
		this.lastZDiff = (float) this.player().getMovementValues().getzDiff();
		}
	}
	
	private Vector fireworkAdder(Vector motion) {
		//From EntityFireworkRocket.onUpdate() (MCP 1.12.2)
		Vector direction = this.player().getMovementValues().getDirection();
        double d0 = 1.5D;
        double d1 = 0.1D;
        double motionX = direction.getX() * d1 + (direction.getX() * d0 - motion.getX()) * 0.5D;
        double motionY = direction.getY() * d1 + (direction.getY() * d0 - motion.getY()) * 0.5D;
        double motionZ = direction.getZ() * d1 + (direction.getZ() * d0 - motion.getZ()) * 0.5D;
        return new Vector(motionX, motionY, motionZ);
	}

	private Vector computeMotion() {
		//From EntityLivingBase.moveEntityWithHeading(strafe, forward) (MCP 1.12.2)
		NessPlayer nessPlayer = this.player();
		Vector direction = this.player().getMovementValues().getDirection();
		double motionX = lastXDiff;
		double motionY = lastYDiff;
		double motionZ = lastZDiff;
		float pitchRadians = (float) Math.toRadians(nessPlayer.getMovementValues().getTo().getPitch());
		double directionHypot = Math.hypot(direction.getX(), direction.getZ());
		double xzDist = this.player().getMovementValues().getXZDiff();
		float speedV = (float) MathUtils.cos(pitchRadians);
		speedV = (float) ((speedV * speedV) * Math.min(1.0D, direction.length() / 0.4D));
		motionY += -0.08f + speedV * 0.06f;
		if (motionY < 0.0D && directionHypot > 0.0D) {
			double d2 = motionY * -0.1D * speedV;
			motionY += d2;
			motionX += direction.getX() * d2 / directionHypot;
			motionZ += direction.getZ() * d2 / directionHypot;
		}
		if (pitchRadians < 0.0F) {
			double speed = xzDist * -MathUtils.sin(pitchRadians) * 0.04D;
			motionY += speed * 3.2D;
			motionX -= direction.getX() * speed / directionHypot;
			motionZ -= direction.getZ() * speed / directionHypot;
		}
		if (directionHypot > 0.0D) {
			motionX += (direction.getX() / directionHypot * xzDist - motionX) * 0.1D;
			motionZ += (direction.getZ() / directionHypot * xzDist - motionZ) * 0.1D;
		}
		motionX *= 0.9900000095367432D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9900000095367432D;
		return new Vector(motionX, motionY, motionZ);
	}

}
