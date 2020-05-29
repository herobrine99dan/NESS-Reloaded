package com.github.ness.nms;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.check.KillauraBotCheck;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NMS_1_8_R3 implements NMSHandler {

	@Override
	public NPC createFakePlayer(Player target, Location location) {
		return new NPC(target, location) {

			@Override
			void completeSpawn() {
				WorldServer worldServer = ((CraftWorld) getTargetPlayer().getWorld()).getHandle();
				EntityPlayer npc;
				npc = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), worldServer,
						new GameProfile(getUuid(), getName()), new PlayerInteractManager(worldServer));
				Location loc = getLocation();
				npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
				Random r = ThreadLocalRandom.current();
				npc.setAbsorptionHearts(r.nextInt(10)+1);
				npc.setHealth(r.nextInt(18)+1);
				npc.triggerHealthUpdate();
				PlayerConnection connection = ((CraftPlayer) getTargetPlayer()).getHandle().playerConnection;
				connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
				connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
				connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, npc));
				connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
				if (armor != null) {
					assert armor.length == 4;
					// 1.8 armor slots
					// https://wiki.vg/index.php?title=Protocol&oldid=7407#Entity_Equipment
					if (armor[0] != null && armor[0].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 4, CraftItemStack.asNMSCopy(armor[0])));
					}
					if (armor[1] != null && armor[1].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 3, CraftItemStack.asNMSCopy(armor[1])));
					}
					if (armor[2] != null && armor[2].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 2, CraftItemStack.asNMSCopy(armor[2])));
					}
					if (armor[3] != null && armor[3].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 1, CraftItemStack.asNMSCopy(armor[3])));
					}
				}
				KillauraBotCheck.npclist.putIfAbsent(target.getName(), Integer.toString(npc.getId()));
		        Bukkit.getScheduler().scheduleSyncDelayedTask(NESSAnticheat.main, () -> {
		        	connection.sendPacket(new PacketPlayOutPlayerInfo(
		                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] { npc }));
		        	connection.sendPacket(new PacketPlayOutEntityDestroy(new int[] { npc.getId() }));
		              KillauraBotCheck.npclist.remove(target.getName());
		            },15L);
			}
			
		};
	}

}
