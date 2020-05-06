package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.github.ness.CheckManager;

public class FastEat extends AbstractCheck<FoodLevelChangeEvent> {
	
	public FastEat(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(FoodLevelChangeEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(FoodLevelChangeEvent e) {
       Check(e);
	}

	public void Check(FoodLevelChangeEvent event) {

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
