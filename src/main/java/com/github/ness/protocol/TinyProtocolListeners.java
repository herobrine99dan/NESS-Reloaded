package com.github.ness.protocol;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.ness.NESSAnticheat;
import com.github.ness.check.BadPackets;
import com.github.ness.check.PingSpoof;
import com.github.ness.nms.MovementPacketEvent;

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
			callPacketEvent(new Location(sender.getWorld(),getMethodValue(packet,"a"),getMethodValue(packet,"b"),getMethodValue(packet,"c")),sender);
			sender.sendMessage("Location1: " + "x:" + getMethodValue(packet,"a") + " y: " + getMethodValue(packet,"b") + " z:" + getMethodValue(packet,"c"));
			BadPackets.Check(sender, packet);
		} else if (packetname.toLowerCase().contains("flying")) {
			PingSpoof.Check(sender, packet);
		}
		return packet;
	}
	
	public void callPacketEvent(Location loc,Player sender) {
		MovementPacketEvent.execute(loc,sender);
	}
	
	public double getMethodValue(Object clazz,String value) {
		if(Bukkit.getVersion().contains("1.8")) {
	        try {
	            Method m = clazz.getClass().getMethod(value);
				return (double) m.invoke(clazz);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				return -101010.0;
			}
		}else {
	        try {
	            Method m = clazz.getClass().getMethod(value,double.class);
				return (double) m.invoke(clazz,0.0);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				return -101010.0;
			}
		}
	}

	public double getFieldValue(Object clazz, String value) {
		try {
			Field field = clazz.getClass().getDeclaredField(value);
			field.setAccessible(true);
			return field.getDouble(clazz);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return -101010.0;
		}

	}

}
