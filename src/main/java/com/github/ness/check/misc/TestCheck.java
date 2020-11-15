package com.github.ness.check.misc;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.wrappers.WrappedInFlyingPacket;
import com.github.ness.packets.wrappers.WrappedInUseEntityPacket;

public class TestCheck extends ListeningCheck<ReceivedPacketEvent> {

	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEvent(ReceivedPacketEvent.class);

	public TestCheck(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent event) {
		NessPlayer player = event.getNessPlayer();

		if (event.getPacket() instanceof WrappedInFlyingPacket) {
			WrappedInFlyingPacket packet = (WrappedInFlyingPacket) event.getPacket();
			if (packet.isLook()) {
				player.sendDevMessage(
						"Look"  + " Yaw: " + packet.getYaw() + " Pitch: " + packet.getPitch());
			} else if (packet.isPos()) {
				player.sendDevMessage("Position" + " X: " + (float) packet.getX() + " Y: "
						+ (float) packet.getY() + " Z: " + (float) packet.getZ());
			} else {
				player.sendDevMessage("Normal " + " Ground: " + packet.isOnGround());
			}
		} else if (event.getPacket() instanceof WrappedInUseEntityPacket) {
			WrappedInUseEntityPacket packet = (WrappedInUseEntityPacket) event.getPacket();
			player.sendDevMessage("UseEntity: Id: " + packet.getEntityId() + " Action: " + packet.getAction());
		}
	}

}
