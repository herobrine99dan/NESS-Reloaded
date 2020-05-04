package com.github.ness.protocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;

public class NPC1_12 {
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
		WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();
		Random r = new Random();
		packet.setEntityID(r.nextInt());
		packet.setOptionalSpeedX(0.2);
		packet.setOptionalSpeedY(0.04);
		packet.setOptionalSpeedZ(0.1);
		packet.setPitch(50);
		packet.setYaw(162);
		packet.setX(loc.getX());
		packet.setY(loc.getY());
		packet.setZ(loc.getZ());
		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
		npc.setAbsorptionHearts(r.nextInt(10)+1);
		npc.setHealth(r.nextInt(18)+1);
		npc.triggerHealthUpdate();
		fakeplayers.putIfAbsent(p.getName(), npc.displayName);
		npc.inventory.armor.set(0,
				CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.CHAINMAIL_CHESTPLATE)));
		ItemStack armor = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		EnumItemSlot CHESTPLATE_SLOT = EnumItemSlot.CHEST;
		//TinyProtocol protocol = NESS.main.protocol;
		ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
		try {
			WrapperPlayServerEntityMetadata pacchetto = new WrapperPlayServerEntityMetadata();
			pacchetto.setEntityID(npc.getId());
			pacchetto.setMetadata(Arrays.asList(new WrappedWatchableObject(npc.getDataWatcher())));
			pacchetto.sendPacket(p);
		}catch(Exception e) {}
		try {
			WrapperPlayServerPlayerInfo pacchetto = new WrapperPlayServerPlayerInfo();
            pacchetto.setAction(PlayerInfoAction.ADD_PLAYER);
			pacchetto.sendPacket(p);
		}catch(Exception e) {}
		//protocol.sendPacket(p, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
		protocol.sendPacket(p, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, npc));
		protocol.sendPacket(p, new PacketPlayOutNamedEntitySpawn(npc));
		protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));
		armor = new ItemStack(Material.GOLD_HELMET);
		CHESTPLATE_SLOT = EnumItemSlot.HEAD;
		protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));
		armor = new ItemStack(Material.GOLD_SWORD);
		CHESTPLATE_SLOT = EnumItemSlot.MAINHAND;
		protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));
		return npc;
	}


	private double RandomDouble(double high, double low) {
		return Math.random() * (high - low) + low;
	}
}
