package com.github.ness.blockgetter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class MaterialAccessImpl implements MaterialAccess {

    @Override
    public Material getMaterial(Block block) {
        return block.getType();
    }

    @Override
    public Material getMaterial(ItemStack itemStack) {
        return itemStack.getType();
    }

	@Override
	public Material getMaterial(Location loc) {
        return loc.getBlock().getType();

	}

}