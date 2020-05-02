package com.github.ness.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.protocol.TinyProtocol;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;

public class NPC1_8 {
	public static HashMap<String, String> fakeplayers = new HashMap<String, String>();
	/*
	 * PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
	 * connection.sendPacket(new
	 * PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
	 * connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
	 */
	
	public static EntityPlayer searchEntityByName(List<org.bukkit.entity.Entity> list,String name) {
		for(org.bukkit.entity.Entity e: list) {
			if(e.getName().equals(name) && e.getType().equals(EntityType.PLAYER)) {
				if(e instanceof EntityPlayer) {
					return (EntityPlayer) e;
				}
			}
		}
		return null;
	}

	public static EntityPlayer spawn(String name, UUID uuid, Player p,Location loc) {
		EntityPlayer npc;
		npc = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(),
				((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(), new GameProfile(uuid, name),
				new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle()));
		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
		Random r = new Random();
		npc.setAbsorptionHearts(r.nextInt(10)+1);
		npc.setHealth(r.nextInt(18)+1);
		npc.triggerHealthUpdate();
		TinyProtocol protocol = NESS.main.protocol;
		protocol.sendPacket(p, new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
		protocol.sendPacket(p, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
		protocol.sendPacket(p, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, npc));
		protocol.sendPacket(p, new PacketPlayOutNamedEntitySpawn(npc));
		return npc;
	}

	public void setHealth(Player p, float health) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			return;
		}
		npc.setHealth(health);
		npc.triggerHealthUpdate();
	}

	public void teleport(Player p, Location loc) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			if(NESS.main.devMode) {
				p.sendMessage("NPC annullato!");
			}
			return;
		}
		npc.setLocation(loc.getX(), loc.getY() + RandomDouble(0.4D, 0.0D), loc.getZ(), loc.getYaw(), loc.getPitch());
		NESS.main.protocol.sendPacket(p, new PacketPlayOutEntityTeleport((Entity) npc));
		NESS.main.protocol.sendPacket(p, new PacketPlayOutEntityHeadRotation((Entity) npc, (byte) (int) loc.getYaw()));
	}

	public void setMetadata(Player p) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			return;
		}
		NESS.main.protocol.sendPacket(p, new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
	}
	
	public void destroy(Player p) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			return;
		}
		NESS.main.protocol.sendPacket(p, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
		NESS.main.protocol.sendPacket(p, new PacketPlayOutEntityDestroy(npc.getId()));
		fakeplayers.remove(p.getName());
	}

	private double RandomDouble(double high, double low) {
		return Math.random() * (high - low) + low;
	}
}
