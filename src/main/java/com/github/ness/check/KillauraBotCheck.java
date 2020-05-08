package com.github.ness.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.github.ness.CheckManager;
import com.github.ness.protocol.NPC1_12;
import com.github.ness.utility.Utilities;
import com.github.ness.utility.Utility;

public class KillauraBotCheck extends AbstractCheck<EntityDamageByEntityEvent> {
	public static HashMap<String, String> npclist = new HashMap<String, String>();

	public KillauraBotCheck(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		 Check(e);
	}

	public void Check(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			Location loc = determinateLocation(p);
			net.minecraft.server.v1_12_R1.EntityPlayer npc = NPC1_12.spawn(Utility.randomString(), UUID.randomUUID(), p,
					loc);
			npclist.putIfAbsent(p.getName(), Integer.toString(npc.getId()));
			Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getNess(), () -> {
				// ProtocolLibrary.getProtocolManager().sendServerPacket(p, new
				// net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo(
				// net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
				// npc));
				try {
					Random r = new Random();
					WrappedGameProfile profile = new WrappedGameProfile(npc.getUniqueID(), npc.getName());
					PlayerInfoData data = new PlayerInfoData(profile, r.nextInt(200),
							EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText("displayname"));
					WrapperPlayServerPlayerInfo pacchetto = new WrapperPlayServerPlayerInfo();
					pacchetto.setAction(PlayerInfoAction.REMOVE_PLAYER);
					pacchetto.setData(Arrays.asList(data));
					pacchetto.sendPacket(p);
				} catch (Exception e) {
				}
				try {
					WrapperPlayServerEntityDestroy pacchetto = new WrapperPlayServerEntityDestroy();
					pacchetto.setEntityIds(new int[npc.getId()]);
					pacchetto.sendPacket(p);
				} catch (Exception e) {
				}
				npclist.remove(p.getName());
			}, 15L);

		}
	}

	private static Location determinateLocation(Player p) {
		Location loc = p.getLocation();
		String direction = Utilities.DeterminateDirection(loc.getYaw());
		if (direction.equals("nord")) {
			loc.add(1.7, 0, 0);
		} else if (direction.equals("sud")) {
			loc.add(-1.7, 0, 0);
		} else if (direction.equals("est")) {
			loc.add(0, 0, 1.7);
		} else if (direction.equals("ovest")) {
			loc.add(0, 0, -1.7);
		}
		return loc;
	}

	public static void Check1(PacketEvent event) {
		Player p = event.getPlayer();
		PacketContainer packet = event.getPacket();
		if (p == null) {
			return;
		}
		// WrapperPlayClientUseEntity pac = new WrapperPlayClientUseEntity(packet);
		String id = npclist.getOrDefault(p.getName(), "");
		// if (id.equals(Integer.toString(pac.getTargetID()))) {
		// WarnHacks.warnHacks(p, "Killaura", 1, -1.0D, 2, "KillauraBot", false);
		return;
		// }
	}

}
