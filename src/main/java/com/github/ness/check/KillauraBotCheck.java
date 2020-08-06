package com.github.ness.check;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.ness.CheckManager;
import com.github.ness.nms.NPC;
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
			NPC npc = manager.getNess().getNmsHandler().createFakePlayer(p, loc);
			ItemStack[] armor = new ItemStack[] { new ItemStack(Material.GOLD_HELMET),
					new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS),
					new ItemStack(Material.DIAMOND_BOOTS) };
			npc.setArmor(armor);
			npc.spawn();
		}
	}

	private static Location determinateLocation(Player p) {
		Location loc = p.getLocation();
		String direction = Utility.determinateDirection(loc.getYaw());
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
