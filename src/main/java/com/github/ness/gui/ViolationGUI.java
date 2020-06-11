package com.github.ness.gui;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.ness.NESSAnticheat;
import com.github.ness.utility.IconMenu;

public class ViolationGUI {

	private CommandSender sender;
	private String violationsStringBuilderResult;

	public ViolationGUI(CommandSender send) {
		this.sender = send;
	}

	public void createGUI() {
		Player p = (Player) sender;
		IconMenu menu = new IconMenu("§c§lViolation Manager", 9, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {
				event.getPlayer().sendMessage("You have chosen " + event.getName());
				event.setWillClose(true);
			}
		}, NESSAnticheat.main);
		int i = 0;
		for (Player cheater : Bukkit.getOnlinePlayers()) {
			i++;
			menu.setOption(i, getPlayerHead(cheater), cheater.getName(), "Cheats: " + getViolationsString(cheater));
		}
		menu.open(p);
	}

	private String getViolationsString(Player target) {
		violationsStringBuilderResult = "";
		NESSAnticheat.main.getViolationManager()
				.getCopyOfViolationMap(NESSAnticheat.main.getCheckManager().getPlayer(target))
				.thenAccept((violations) -> {
					for (Map.Entry<String, Integer> entry : violations.entrySet()) {
						violationsStringBuilderResult = violationsStringBuilderResult + "," +entry.getKey();
					}
				});
		return violationsStringBuilderResult;
	}

	public ItemStack getPlayerHead(Player player) {
		ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
		playerheadmeta.setOwner(player.getName());
		playerheadmeta.setDisplayName(player.getName());
		playerhead.setItemMeta(playerheadmeta);
		return playerhead;
	}
}
