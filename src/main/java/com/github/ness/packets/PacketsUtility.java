package com.github.ness.packets;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.ness.packets.wrappers.PacketPlayInPositionLook;
import com.github.ness.packets.wrappers.PacketPlayInUseEntity;
import com.github.ness.packets.wrappers.SimplePacket;

public class PacketsUtility {
	
	/**
	 * Get A SimplePacket Object, which can be a PacketPlayInUseEntity, a
	 * PacketPlayInPositionLook or a simple packet object.
	 * 
	 * @param p
	 * @param packet
	 * @return SimplePacket Object
	 */

	public SimplePacket convertPacket(Player p, Object packet) {
		SimplePacket packetconverted = new SimplePacket(packet);
		String packetname = packet.toString().substring(0, packet.toString().indexOf("@"))
				.replace("net.minecraft.server.", "");
		if (packetname.toLowerCase().contains("useentity")) {
			PacketPlayInUseEntity entitypacket = new PacketPlayInUseEntity(packet);
			p.sendMessage("EntityPacket: " + entitypacket.getAction() + " " + entitypacket.getEntityId());
			packetconverted = entitypacket;
			return packetconverted;
		} else if (packetname.toLowerCase().contains("position")) {
			PacketPlayInPositionLook positionpacket = new PacketPlayInPositionLook(packet);
			p.sendMessage("PositionPacket: " + new Location(p.getWorld(), positionpacket.getX(), positionpacket.getY(),
					positionpacket.getZ(), positionpacket.getYaw(), positionpacket.getPitch()));
			packetconverted = positionpacket;
			return packetconverted;
		} else {
			return packetconverted;
		}
	}

}
