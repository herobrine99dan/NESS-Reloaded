package com.github.ness.packets;

import java.util.UUID;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.packets.wrappers.SimplePacket;

public class ReceivedPacketEventInterceptor implements PacketInterceptor {

	private final NessAnticheat ness;

	public ReceivedPacketEventInterceptor(NessAnticheat ness) {
		this.ness = ness;
	}

	@Override
	public boolean shouldDrop(UUID uuid, Object packet) throws Exception {
    	NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(uuid);
    	if (nessPlayer == null) {
    		return true;
    	}
        ReceivedPacketEvent event = new ReceivedPacketEvent(
                nessPlayer,
                getPacketObject(packet));
        ness.getPlugin().getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
	}

    private SimplePacket getPacketObject(Object p) throws IllegalArgumentException, IllegalAccessException {
        SimplePacket packet = new SimplePacket(p);
        packet.process();
        return packet;
    }

}
