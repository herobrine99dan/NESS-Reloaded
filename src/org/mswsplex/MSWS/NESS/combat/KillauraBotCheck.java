package org.mswsplex.MSWS.NESS.combat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.PlayerManager;
import org.mswsplex.MSWS.NESS.Utilities;
import org.mswsplex.MSWS.NESS.Utility;
import org.mswsplex.MSWS.NESS.WarnHacks;
import org.mswsplex.MSWS.NESS.protocols.NPC1_12;
import org.mswsplex.MSWS.NESS.protocols.NPC1_8;

//import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class KillauraBotCheck {
	public static HashMap<String, String> npclist = new HashMap<String, String>();

	public static void Check(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			Location loc = determinateLocation(p);
			if(PlayerManager.hasPermissionBypass(p, "Killaura")) {
				return;
			}
			if (Bukkit.getVersion().contains("1.12")) {
				net.minecraft.server.v1_12_R1.EntityPlayer npc = NPC1_12.spawn(Utility.randomString(),
						UUID.randomUUID(), p,loc);
				npclist.putIfAbsent(p.getName(), Integer.toString(npc.getId()));
				Bukkit.getScheduler().scheduleSyncDelayedTask(NESS.main, () -> {
					NESS.main.protocol.sendPacket(p, new net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo(
							net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
					NESS.main.protocol.sendPacket(p, new net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy(npc.getId()));
					npclist.remove(p.getName());
				}, 15L);
				
			} else if (Bukkit.getVersion().contains("1.8.8")) {
				net.minecraft.server.v1_8_R3.EntityPlayer npc = NPC1_8.spawn(Utility.randomString(),
						UUID.randomUUID(), p,loc);
				npclist.putIfAbsent(p.getName(), Integer.toString(npc.getId()));
				Bukkit.getScheduler().scheduleSyncDelayedTask(NESS.main, () -> {
					NESS.main.protocol.sendPacket(p, new net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo(
							net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
							npc));
					NESS.main.protocol.sendPacket(p, new net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy(npc.getId()));
					npclist.remove(p.getName());
				}, 15L);
			}
		}
	}
	
	private static Location determinateLocation(Player p) {
		Location loc = p.getLocation();
		String direction = Utilities.DeterminateDirection(loc.getYaw());
		if(direction.equals("nord")) {
			loc.add(1.7, 0, 0);
		} else if(direction.equals("sud")) {
			loc.add(-1.7, 0, 0);
		} else if(direction.equals("est")) {
			loc.add(0, 0, 1.7);
		} else if(direction.equals("ovest")) {
			loc.add(0, 0, -1.7);
		}
		return loc;
	}

	public static void Check1(PacketEvent event) {
		Player p = event.getPlayer();
		PacketContainer packet = event.getPacket();
		if(p==null) {
			return;
		}
		//WrapperPlayClientUseEntity pac = new WrapperPlayClientUseEntity(packet);
		String id = npclist.getOrDefault(p.getName(), "");
		//if (id.equals(Integer.toString(pac.getTargetID()))) {
			//WarnHacks.warnHacks(p, "Killaura", 1, -1.0D, 2, "KillauraBot", false);
			return;
		//}
	}

}
