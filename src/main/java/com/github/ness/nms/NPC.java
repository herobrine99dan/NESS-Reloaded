package com.github.ness.nms;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.ness.Utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A fake player
 * 
 * @author A248
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class NPC {

	@Getter
	private final UUID uuid = UUID.randomUUID();
	@Getter(AccessLevel.PACKAGE)
	private final String name = Utility.randomString();
	@Getter
	private final Player targetPlayer;
	@Getter
	private final Location location;
	
	ItemStack[] armor;
	private boolean spawned = false;
	
	/**
	 * Sets the armor of the NPC before spawning it. <br>
	 * <br>
	 * Index 0 is the helmet, 1 the chestplate, 2 the leggings,
	 * and 3 the boots. Therefore, <b>the armor array must be length 4.</b> <br>
	 * If no armor is desired in a specific slot, the itemstack at the index
	 * may be <i>null</i>.
	 * 
	 * @param armor the armor
	 * @throws IllegalStateException if already spawned
	 */
	public void setArmor(ItemStack[] armor) {
		if (spawned) {
			throw new IllegalStateException("Already spawned!");
		}
		this.armor = armor;
	}
	
	/**
	 * Spawns the NPC
	 * 
	 */
	public void spawn() {
		spawned = true;
	}
	
	abstract void completeSpawn();
	
}
