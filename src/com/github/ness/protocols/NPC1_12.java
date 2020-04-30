package com.github.ness.protocols;

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

import com.github.ness.NESS;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
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
		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
		Random r = new Random();
		npc.setAbsorptionHearts(r.nextInt(10)+1);
		npc.setHealth(r.nextInt(18)+1);
		npc.triggerHealthUpdate();
		fakeplayers.putIfAbsent(p.getName(), npc.displayName);
		npc.inventory.armor.set(0,
				CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.CHAINMAIL_CHESTPLATE)));
		ItemStack armor = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		EnumItemSlot CHESTPLATE_SLOT = EnumItemSlot.CHEST;
		TinyProtocol protocol = NESS.main.protocol;
		protocol.sendPacket(p, new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
		protocol.sendPacket(p, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
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

	public void setArmor(Player p, Material helmet, Material chestplate, Material leggings, Material boots) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			return;
		}
		npc.inventory.armor.set(0,
				CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(chestplate)));
		TinyProtocol protocol = NESS.main.protocol;
		ItemStack armor = new ItemStack(chestplate);
		EnumItemSlot CHESTPLATE_SLOT = EnumItemSlot.CHEST;
		protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));
		armor = new ItemStack(helmet);
		
		CHESTPLATE_SLOT = EnumItemSlot.HEAD;
		protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));
		armor = new ItemStack(leggings);
		
		CHESTPLATE_SLOT = EnumItemSlot.LEGS;
		protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));
		armor = new ItemStack(boots);
		
		CHESTPLATE_SLOT = EnumItemSlot.FEET;
		protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));
		NESS.main.protocol.sendPacket(p, new PacketPlayOutAnimation((Entity) npc, 0));
	}
	
	public void setItem(Player p, Material item) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			return;
		}
		ItemStack armor = new ItemStack(item);
		EnumItemSlot CHESTPLATE_SLOT = EnumItemSlot.MAINHAND;
		NESS.main.protocol.sendPacket(p,
				new PacketPlayOutEntityEquipment(npc.getId(), CHESTPLATE_SLOT, CraftItemStack.asNMSCopy(armor)));

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

	@SuppressWarnings("deprecation")
	public void hide(Player p) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			return;
		}
		NESS.main.protocol.sendPacket(p, new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc ));
		CraftPlayer botc = new CraftPlayer((CraftServer) Bukkit.getServer(), npc);
		botc.hidePlayer(p);
	}

	@SuppressWarnings("deprecation")
	public void show(Player p) {
		String uuid = fakeplayers.getOrDefault(p.getName(), null);
		EntityPlayer npc = (EntityPlayer) searchEntityByName(p.getWorld().getEntities(),uuid);
		if (npc == null) {
			return;
		}
		NESS.main.protocol.sendPacket(p, new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc ));
		CraftPlayer botc = new CraftPlayer((CraftServer) Bukkit.getServer(), npc);
		botc.showPlayer(p);
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
