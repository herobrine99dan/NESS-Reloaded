package com.github.ness.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerConnection;
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

	public static EntityPlayer spawn(String name, UUID uuid, Player p, Location loc) {
		EntityPlayer npc;
		npc = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(),
				((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(), new GameProfile(uuid, name),
				new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle()));
		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
		Random r = new Random();
		npc.setAbsorptionHearts(r.nextInt(10)+1);
		npc.setHealth(r.nextInt(18)+1);
		npc.triggerHealthUpdate();
		fakeplayers.putIfAbsent(p.getName(), npc.displayName);
		npc.inventory.armor.set(0,
				CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.CHAINMAIL_CHESTPLATE)));
		ItemStack armor = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		EnumItemSlot itemSlot = EnumItemSlot.CHEST;
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
		connection.sendPacket( new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, npc));
		connection.sendPacket( new PacketPlayOutNamedEntitySpawn(npc));
		connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), itemSlot, CraftItemStack.asNMSCopy(armor)));
		armor = new ItemStack(Material.GOLD_HELMET);
		itemSlot = EnumItemSlot.HEAD;
		connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), itemSlot, CraftItemStack.asNMSCopy(armor)));
		armor = new ItemStack(Material.GOLD_SWORD);
		itemSlot = EnumItemSlot.MAINHAND;
		connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), itemSlot, CraftItemStack.asNMSCopy(armor)));
		return npc;
	}
}
