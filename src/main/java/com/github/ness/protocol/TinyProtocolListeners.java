package com.github.ness.protocol;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.ness.NESSAnticheat;
import com.github.ness.check.BadPackets;
import com.github.ness.check.PingSpoof;

import io.netty.channel.Channel;

public class TinyProtocolListeners extends TinyProtocol {

	public TinyProtocolListeners(Plugin plugin) {
		super(plugin);
	}

	public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
		if (sender == null || packet == null || NESSAnticheat.main == null) {
			return packet;
		} /*
			 * if (packetType == PacketType.Play.Client.FLYING) { new
			 * PingSpoof().Check(sender, packet); }else if(packetType ==
			 * PacketType.Play.Client.POSITION) { new BadPackets().Check(sender,packet); }
			 */
		// System.out.println("Pacchetto: " + packet.toString());
		String packetname = packet.toString().substring(0, packet.toString().indexOf("@"))
				.replace("net.minecraft.server.v1_12_R1", "");
		if (!packetname.toLowerCase().endsWith("flying")) {
			sender.sendMessage("Packet: " + packetname);
		}
		if (packetname.toLowerCase().contains("position")) {
			try {
				double x = (double) getFieldValue(packet, "x");
				double y = (double) getFieldValue(packet, "y");
				double z = (double) getFieldValue(packet, "z");
				double yaw = (float) getFieldValue(packet, "yaw");
				double pitch = (float) getFieldValue(packet, "pitch");
				sender.sendMessage("Location: " + "x:" + x + " y: " + y + " z:" + z);
				sender.sendMessage("Yaw: " + yaw);
				sender.sendMessage("Pitch: " + pitch);
			} catch (Exception e) {
			}
			BadPackets.Check(sender, packet);
		} else if (packetname.toLowerCase().contains("flying")) {
			PingSpoof.Check(sender, packet);
		}
		return packet;
	}
	
	public double getFieldValue(Object clazz,String value) {
			try {
				Field field = clazz.getClass().getField(value);
				return field.getDouble(clazz);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				return -101010.0;
			}
		
	}
	
}
