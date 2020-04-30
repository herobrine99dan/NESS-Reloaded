package com.github.ness;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantHack {

	@SuppressWarnings("deprecation")
	protected static void Check(InventoryClickEvent event) {
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
			return;
		}
		final ItemStack item = event.getCurrentItem();
		if (!item.hasItemMeta()) {
			return;
		}
		final ItemMeta meta = item.getItemMeta();
		final Map<Enchantment, Integer> enchants = (Map<Enchantment, Integer>) meta.getEnchants();
		Player p = (Player) event.getWhoClicked();
		if (!(p.getGameMode().getValue() == 1)) {
			for (final Enchantment enchant : enchants.keySet()) {
				if (meta.getEnchantLevel(enchant) >= 100) {
					event.setResult(Event.Result.DENY);
					event.setCancelled(true);
					event.setCurrentItem(new ItemStack(Material.AIR));
				}
			}
		}

	}

}
