package com.github.ness.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.utility.IconMenu;

public class ViolationGUI {

	private CommandSender sender;

	public ViolationGUI(CommandSender send) {
		this.sender = send;
	}

	public void createGUI() {
		Player p = (Player) sender;
		IconMenu menu = new IconMenu("§c§lViolation Manager", 9, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {
				if (Bukkit.getPlayer(event.getName()) == null) {
					p.sendMessage("This player isn't online!");
					return;
				}
			}
		}, NESSAnticheat.main);
		int i = 0;
		for (Player cheater : Bukkit.getOnlinePlayers()) {
			i++;
			List<String> lore = new ArrayList<String>();
			lore.add("Cheats:");
			NessPlayer np = NESSAnticheat.main.getCheckManager().getPlayer(cheater);
			for (String s : np.checkViolationCounts.keySet()) {
				lore.add(" VL: " + np.checkViolationCounts.getOrDefault(s, 0));
			}
			menu.setOption(i - 1, getPlayerHead(cheater), cheater.getName(), lore);
		}
		menu.open(p);
	}

	public void showViolations(Player p, IconMenu menu) {
		/*
		 * Inventory myInventory = Bukkit.createInventory(null, 9, "§c§lViolations for "
		 * + p.getName()); int i = 0; NessPlayer np =
		 * NESSAnticheat.main.getCheckManager().getPlayer(p); for (String s :
		 * np.checkViolationCounts.keySet()) { i++; ItemStack item = new
		 * ItemStack(Material.REDSTONE_BLOCK, 1); ItemMeta metadata =
		 * item.getItemMeta(); metadata.setDisplayName(s);
		 * metadata.setLore(Arrays.asList("VL: " +
		 * np.checkViolationCounts.getOrDefault(s, 0))); item.setItemMeta(metadata);
		 * myInventory.setItem(i - 1, item); } sender.openInventory(myInventory);
		 */
		NessPlayer np = NESSAnticheat.main.getCheckManager().getPlayer(p);
		int i = 0;
		for (String s : np.checkViolationCounts.keySet()) {
			i++;
			menu.setOption(i - 1, new ItemStack(Material.REDSTONE_BLOCK, 1), s,
					Arrays.asList("VL: " + np.checkViolationCounts.getOrDefault(s, 0)));
		}
	}

	public ItemStack getPlayerHead(Player player) {
		ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
		playerheadmeta.setOwner(player.getName());
		playerheadmeta.setDisplayName(player.getName());
		playerheadmeta.setLocalizedName(player.getName());
		playerhead.setItemMeta(playerheadmeta);
		return playerhead;
	}
}
