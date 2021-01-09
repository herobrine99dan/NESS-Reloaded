package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class Aimbot extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private double lastYaw;
	private double lastPitch;
	private int buffer;
	private double equalRotationsBuffer;
	private double patternBuffer;

	public Aimbot(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		lastYaw = 0;
		lastPitch = 0;
	}

	@Override
	protected void checkPacket(Packet packet) {
		if (!packet.isPacketType(this.packetTypeRegistry().playInFlying()) || player().isTeleported()) {
			return;
		}
		PlayInFlying wrapper = packet.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		if (!wrapper.hasLook()) {
			return;
		}
		Check1(packet);
		Check2(packet);
		Check3(packet);
		Check4(packet);
		lastYaw = (float) player().getMovementValues().getYawDiff();
		lastPitch = (float) player().getMovementValues().getPitchDiff();
	}

	private void Check1(Packet e) {
		NessPlayer np = player();
		if (np.getSensitivity() == 0) {
			return;
		}
		if (np.getMovementValues().getYawDiff() < 1) {
			return;
		}
		double firstvar = ((np.getSensitivity() + 1) / 200) * 0.6F + 0.2F;
		float secondvar = (float) (Math.pow(firstvar, 3f));
		double yawResult = np.getMovementValues().getYawDiff() - lastYaw;
		double pitchResult = np.getMovementValues().getPitchDiff() - lastPitch;
		float mouseDeltaX = (float) yawResult / (secondvar * 1.2f);
		float mouseDeltaY = (float) pitchResult / (secondvar * 1.2f);
		// float x = (float) (thirdvar - Math.floor(thirdvar));
		np.sendDevMessage("mouseDeltaX: " + mouseDeltaX + " mouseDeltaY: " + mouseDeltaY);
	}

	/**
	 * Check for some Aimbot Pattern
	 */
	private void Check2(Packet e) {
		NessPlayer player = player();
		float yawChange = (float) Math.abs(player.getMovementValues().getYawDiff());
		float pitchChange = (float) Math.abs(player.getMovementValues().getPitchDiff());
		if (yawChange >= 0.1 && yawChange % 0.1f == 0.0f) {
			flag(" PerfectAura");
		} else if (pitchChange >= 0.1 && pitchChange % 0.1f == 0.0f) {
			flag(" PerfectAura1");
		} else if (pitchChange < 0.01 && yawChange > 4f && player.getMovementValues().getTo().getPitch() != 90.0F
				&& player.getMovementValues().getFrom().getPitch() != 90.0F) {
			if(++patternBuffer > 7) {
			this.flagEvent(e, "PerfectAura2");
			}
		} else if(patternBuffer > 0) {
			patternBuffer -= 0.5;
		}
	}

	/**
	 * @author Tecnio This check comes from AntiHaxerman
	 *         (https://github.com/Tecnio/AntiHaxerman/blob/master/src/main/java/me/tecnio/antihaxerman/check/impl/aim/AimE.java)
	 * @param e
	 */
	private void Check3(Packet e) {
		NessPlayer player = player();
		if (player.milliSecondTimeDifference(PlayerAction.ATTACK) < 100) {
			if (player.getMovementValues().getYawDiff() % .25 == 0.0 && player.getMovementValues().getYawDiff() > 0) {
				if (++buffer > 2) {
					flag();
				}
			} else if (buffer > 0) {
				buffer--;
			}
		}
	}

	private void Check4(Packet e) {
		NessPlayer player = player();
		if (lastYaw == player.getMovementValues().getYawDiff() && player.getMovementValues().getYawDiff() != 0.0) {
			if (++equalRotationsBuffer > 2) {
				this.flag("EqualsRotations");
			}
		} else if (lastPitch == player.getMovementValues().getPitchDiff() && player.getMovementValues().getPitchDiff() != 0.0) {
			if (++equalRotationsBuffer > 2) {
				this.flag("EqualsRotations");
			}
		} else if (equalRotationsBuffer > 0) {
			equalRotationsBuffer -= 0.25;
		}
	}
}