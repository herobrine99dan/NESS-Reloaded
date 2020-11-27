package com.github.ness.blockgetter;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface MaterialAccess {

    Material getMaterial(Block block);

    Material getMaterial(ItemStack itemStack);
}
