package com.github.ness.nms;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.check.KillauraBotCheck;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public class NMS_1_12_R1 implements NMSHandler {

	@Override
	public NPC createFakePlayer(Player target, Location location) {
		return new NPC(target, location) {

			@Override
			void completeSpawn() {
				WorldServer worldServer = ((CraftWorld) getTargetPlayer().getWorld()).getHandle();
				EntityPlayer npc;
				GameProfile gameProfile = new GameProfile(getUuid(), getName());
				changeSkin(gameProfile);
				npc = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), worldServer,
						gameProfile, new PlayerInteractManager(worldServer));
				Location loc = getLocation();
				npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
				Random r = ThreadLocalRandom.current();
				npc.setAbsorptionHearts(r.nextInt(10) + 1);
				npc.setHealth(r.nextInt(18) + 1);
				npc.triggerHealthUpdate();
				PlayerConnection connection = ((CraftPlayer) getTargetPlayer()).getHandle().playerConnection;
				connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
				connection.sendPacket(
						new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
				connection.sendPacket(
						new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, npc));
				connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
				if (armor != null) {
					assert armor.length == 4;
					if (armor[0] != null && armor[0].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.HEAD,
								CraftItemStack.asNMSCopy(armor[0])));
					}
					if (armor[1] != null && armor[1].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.CHEST,
								CraftItemStack.asNMSCopy(armor[1])));
					}
					if (armor[2] != null && armor[2].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.LEGS,
								CraftItemStack.asNMSCopy(armor[2])));
					}
					if (armor[3] != null && armor[3].getType() != Material.AIR) {
						connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.FEET,
								CraftItemStack.asNMSCopy(armor[3])));
					}
				}
				KillauraBotCheck.npclist.putIfAbsent(target.getName(), Integer.toString(npc.getId()));
				Bukkit.getScheduler().scheduleSyncDelayedTask(NESSAnticheat.main, () -> {
					NESSAnticheat.main.protocol.sendPacket(target, new PacketPlayOutPlayerInfo(
							PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] { npc }));
					NESSAnticheat.main.protocol.sendPacket(target,
							new PacketPlayOutEntityDestroy(new int[] { npc.getId() }));
					KillauraBotCheck.npclist.remove(target.getName());
				}, 15L);
			}

		};
	}

	private void changeSkin(GameProfile profile) {
        Random r = new Random();
        String texture = "";
        String signature = "";
        if(r.nextBoolean()) {
        	signature = "\"fY/TLUP8nlX/sEu6bISFRe435QLmQ9lN5zdnHxDMeAdajUcjKEEtsqOWeDnhM8QjmijqcVdNkx15xurS53lD/QkSwJ/g8K5BWf9Qaxuz1nq/VZQ6k9bffBHLqrwcUqEBT+9iQtN9l7O4GQFg77mlFVYvrif7/N1kY6Abn51QvpftCWXGgZD1UztIOk5nc/mGahTrMDuCWYjJGadehrmTkq/hGI0gud/afMmhNDaGLqzeq3d3oLQ0Qyn3ovksXf2bj4RmXkviGgCC3fsF1djYTrUV8Kd2hRA1wDvmBYzH0mjSFvna47vEdj+W9hsJJoCaZE1WP7nEqXuO/apO7dbcMxavzwIr4pnNEq+FEpmpd9BSO28FnYb63u6rYixg6DWc1aY3rME0epn6lx8QC46/uhLZxZhw7SkaYsFctEP/R1maaCu+xxWp42W8x7JN3b7jJ8d8IOyn8folRF5IM0aXT05EOAZeut0v+FQw1orS6zlJ8UuIAwgNG1iHGaRj/AQOK8LIhsqnOvNfuVqGi8riAvVmOq/O0uftTX81PDCfNc04dm1q8czWLDuoBSjuVWeHrBeMoXQn5r7CapJzgzcyLJHw4no9uBG7lTWluxsOeDsTd4urDqQwuG7vwtu9wrRb/FyDGwc3hE/6LDfM9jjc88HkkP8zyxqPxcOzPNQ6vCU=";
        	texture = "ewogICJ0aW1lc3RhbXAiIDogMTU4ODk0NTA2MTk4NCwKICAicHJvZmlsZUlkIiA6ICJlZDVmMTJjZDYwMDc0NWQ5YTRiOTk0MDUyNGRkYWVjZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBMjQ4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE1NTg4YjUxZjU4NTI5NGQ0Y2I5N2Y0ZGU0NjE3MzNjNDRkMDM3ZWRjNjQ2ODlhNGQ1MjcxZjE5MmY3NzNhOTQiCiAgICB9CiAgfQp9";
        }else {
        	texture = "ewogICJ0aW1lc3RhbXAiIDogMTU4ODk0NTEyMjg0NiwKICAicHJvZmlsZUlkIiA6ICI1YjM1NjI3OTIwMTI0Y2RhOTJiNGYzNTY0NWFkNzE0OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJoZXJvYnJpbmU5OWRhbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mOTYyNDZiNTE5YjgzMTk2MmQ3ZTVmMmMwMWNkMDQ5ZGE4ZTE2MDlkNjg2MmUxMTk3ZTVlMDUwYjg3ODI4NzlkIgogICAgfQogIH0KfQ==";
        	signature = "HsLtwsdBZhST9bw+fqgEqQESBkVRK9/8x/e5K0sDfKvkQAmJ05u3SeNQsSv8NACiDhonjugbvcBSuiBgLVQnB9TuFXYMnkonJ09qDXWQIjalwgzdt2KhrO09VcKQOIl9sBTo7OLHZ5aXTC/2x7aSxmkS5hF4PKpz7eP2lUHx5nl9iX1/7fCxTjzK4hpgB3WIZHotaC8T+HvSycxRzCrgoaRdfUuLVhLAOHhCDpCySr3Ur/DXzVWBKOnfXCtxkESxFTmiP/ka5Lsq+WbTZNGo0sNCE1+wEcq+0TjpUpREPRLtbz3nk8v/y7781Ut/9DzNtOFGGeRm0Gu8M4XRRefjlLA4dZF4AeJGkHgEG/zxgbme4swBUblRT53fGR4mA4IPiSvLZbsS3oaAFQ9aY+e3Gw6xMtglF46hy5dDbIT7dPMNHzXINN/og6xBj+sUlpIv2GlDeeuL1pyuwTdbyJuEwd8f7ppVqFyEwM8VpsaPdFlEnAFg5ugUx7ySaMc2gVAJy0FxpUN2jgU0yXpcVAqhK48O1v9VzenCt/je0VNsMEItUQhO7cog6MyoODa/KNEcaYWuVaboTcvAthu2IdoMrgE8fOxRNGvMZ4y9sB9LSGXyzj7CKKEylZ7C5su6xBZf3mYss2lsuuVsEhsPjnNeIkpYeneQ+kSBDmrmY+MxJiY=";
        }
        
		profile.getProperties().put("textures", new Property("textures", texture, signature));

	}

}
