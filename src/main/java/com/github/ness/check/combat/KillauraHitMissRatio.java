package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInUseEntity;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class KillauraHitMissRatio extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();
	private float hits, swings;
	private final int maxSwings;
	private final float minPercentage;
	private int lastEntityID;

	public KillauraHitMissRatio(final PacketCheckFactory<?> factory, final NessPlayer nessPlayer) {
		super(factory, nessPlayer);
		this.maxSwings = this.ness().getMainConfig().getCheckSection().killauraHitMissRatio().maxSwings();
		this.minPercentage = (float) this.ness().getMainConfig().getCheckSection().killauraHitMissRatio()
				.minPercentage();

	}

	public interface Config {
		@DefaultInteger(60)
		int maxSwings();

		@DefaultDouble(95.0)
		double minPercentage();

	}

	@Override
	protected void checkPacket(Packet packet) {
		MovementValues values = this.player().getMovementValues();
		if (packet.isPacketType(packetTypeRegistry().playInUseEntity())) {
			PlayInUseEntity entityAction = packet.toPacketWrapper(packetTypeRegistry().playInUseEntity());
			int entityID = entityAction.getEntityID();
			if (entityAction.isAttack()) { //Check if the packet is an attack packet
				if (values.getXZDiff() > 0.1) { //Check if the player is moving enough
					this.player().sendDevMessage("adding hit: " + hits);
					if(lastEntityID == entityID) { //Check if the player is attacking repeatedly the same entity
						hits++; //TODO Check if the entity that gets damage is moving and isn't in a corner
					}
				}
			}
			lastEntityID = entityID;
		} else if (packet.isPacketType(packetTypeRegistry().playInArmAnimation())) {
			swings++;
			if (swings >= maxSwings) {
				float percentage = (hits / (swings)) * 100;
				this.player().sendDevMessage("percentage: " + percentage);
				if (percentage >= minPercentage) {
					this.flag("percentage: " + percentage);
				}
				hits = 0;
				swings = 0;
			}
		}
	}
}
