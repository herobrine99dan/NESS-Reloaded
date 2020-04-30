package com.github.ness.combat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.MSG;
import com.github.ness.NESS;
import com.github.ness.PlayerManager;
import com.github.ness.WarnHacks;

public class FastEat {

	@SuppressWarnings("deprecation")
	public
	static void Check(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			final ItemStack hand = player.getItemInHand();
			if (hand == null || hand.getType() == Material.AIR) {
				return;
			}
			if (!FastEat.isFood(hand.getType())) {
				return;
			}
			if (player.getFoodLevel() >= event.getFoodLevel()) {
				return;
			}
			if (player.hasPotionEffect(PotionEffectType.SATURATION)) {
				return;
			}
			if (PlayerManager.timeSince("lastAte", player) < 1630.0) {
				WarnHacks.warnHacks(player, "FastEat",
						(int) Math.min(1650.0 - PlayerManager.timeSince("lastAte", player), 100.0), -1.0, 4,"Vanilla",false);
				if (NESS.main.devMode) {
					MSG.tell((CommandSender) player,
							"&9Dev> &7Food delay: " + PlayerManager.timeSince("lastAte", player));
				}
			}
			PlayerManager.addAction("foodTicks", player);
			PlayerManager.setInfo("lastAte", (OfflinePlayer) player, System.currentTimeMillis());
		}
	}

	private static boolean isFood(final Material mat) {
		final List<Material> food = new ArrayList<Material>();
		food.add(Material.APPLE);
		food.add(Material.MUSHROOM_SOUP);
		food.add(Material.BREAD);
		food.add(Material.RAW_BEEF);
		food.add(Material.COOKED_BEEF);
		food.add(Material.GOLDEN_APPLE);
		food.add(Material.RAW_FISH);
		food.add(Material.COOKED_FISH);
		food.add(Material.COOKIE);
		food.add(Material.RAW_BEEF);
		food.add(Material.COOKED_BEEF);
		food.add(Material.RAW_CHICKEN);
		food.add(Material.COOKED_CHICKEN);
		food.add(Material.ROTTEN_FLESH);
		food.add(Material.SPIDER_EYE);
		food.add(Material.CARROT_ITEM);
		food.add(Material.POTATO_ITEM);
		food.add(Material.BAKED_POTATO);
		food.add(Material.POISONOUS_POTATO);
		food.add(Material.PUMPKIN_PIE);
		food.add(Material.RABBIT);
		food.add(Material.COOKED_RABBIT);
		food.add(Material.RABBIT_STEW);
		food.add(Material.MUTTON);
		food.add(Material.COOKED_MUTTON);
		food.add(Material.MELON);
		return food.contains(mat);
	}

}
