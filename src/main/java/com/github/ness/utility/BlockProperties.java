package com.github.ness.utility;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class BlockProperties {

	private static final Map<Material, BlockConfiguration> blocks = new HashMap<>();

	static {
		BlockProperties.blocks.put(Material.BARRIER, new BlockConfiguration(Double.MAX_VALUE));
		BlockProperties.blocks.put(Material.BEDROCK, new BlockConfiguration(Double.MAX_VALUE));
		BlockProperties.blocks.put(Material.COMMAND, new BlockConfiguration(Double.MAX_VALUE));
		BlockProperties.blocks.put(Material.ENDER_PORTAL, new BlockConfiguration(Double.MAX_VALUE));
		BlockProperties.blocks.put(Material.ENDER_PORTAL_FRAME, new BlockConfiguration(Double.MAX_VALUE));
		BlockProperties.blocks.put(Material.AIR, new BlockConfiguration(Double.MAX_VALUE));
		BlockProperties.blocks.put(Material.LAVA, new BlockConfiguration(100.0));
		BlockProperties.blocks.put(Material.STATIONARY_LAVA, new BlockConfiguration(100.0));
		BlockProperties.blocks.put(Material.WATER, new BlockConfiguration(100.0));
		BlockProperties.blocks.put(Material.STATIONARY_WATER, new BlockConfiguration(100.0));
		BlockProperties.blocks.put(Material.OBSIDIAN, new BlockConfiguration(50.0, ToolType.PICKAXE, BlockType.DIAMOND));
		BlockProperties.blocks.put(Material.ENDER_CHEST, new BlockConfiguration(22.5, ToolType.PICKAXE, BlockType.DIAMOND));
		BlockProperties.blocks.put(Material.ANVIL, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.COAL_BLOCK, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DIAMOND_BLOCK, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.IRON));
		BlockProperties.blocks.put(Material.EMERALD_BLOCK, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.IRON));
		BlockProperties.blocks.put(Material.IRON_BLOCK, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.STONE));
		BlockProperties.blocks.put(Material.REDSTONE_BLOCK, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.ENCHANTMENT_TABLE, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.IRON_FENCE, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.IRON_DOOR_BLOCK, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.IRON_TRAPDOOR, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.MOB_SPAWNER, new BlockConfiguration(5.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WEB, new BlockConfiguration(4.0, ToolType.SHEARS, BlockType.NA));
		BlockProperties.blocks.put(Material.DISPENSER, new BlockConfiguration(3.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DROPPER, new BlockConfiguration(3.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.FURNACE, new BlockConfiguration(3.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BEACON, new BlockConfiguration(3.0, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.GOLD_BLOCK, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.IRON));
		BlockProperties.blocks.put(Material.COAL_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DRAGON_EGG, new BlockConfiguration(3.0, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.DIAMOND_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.IRON));
		BlockProperties.blocks.put(Material.EMERALD_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.IRON));
		BlockProperties.blocks.put(Material.ENDER_STONE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.GOLD_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.IRON));
		BlockProperties.blocks.put(Material.HOPPER, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.IRON_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.STONE));
		BlockProperties.blocks.put(Material.LAPIS_BLOCK, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.STONE));
		BlockProperties.blocks.put(Material.LAPIS_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.STONE));
		BlockProperties.blocks.put(Material.QUARTZ_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.REDSTONE_ORE, new BlockConfiguration(3.0, ToolType.PICKAXE, BlockType.IRON));
		BlockProperties.blocks.put(Material.TRAP_DOOR, new BlockConfiguration(3.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WOODEN_DOOR, new BlockConfiguration(3.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.ACACIA_DOOR, new BlockConfiguration(3.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SPRUCE_DOOR, new BlockConfiguration(3.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BIRCH_DOOR, new BlockConfiguration(3.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.JUNGLE_DOOR, new BlockConfiguration(3.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DARK_OAK_DOOR, new BlockConfiguration(3.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.CHEST, new BlockConfiguration(2.5, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.TRAPPED_CHEST, new BlockConfiguration(2.5, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WORKBENCH, new BlockConfiguration(2.5, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BRICK_STAIRS, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BRICK, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.CAULDRON, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.COBBLESTONE, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.COBBLESTONE_STAIRS, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.COBBLE_WALL, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.FENCE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.ACACIA_FENCE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SPRUCE_FENCE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BIRCH_FENCE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.JUNGLE_FENCE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DARK_OAK_FENCE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.FENCE_GATE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.ACACIA_FENCE_GATE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SPRUCE_FENCE_GATE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BIRCH_FENCE_GATE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.JUNGLE_FENCE_GATE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DARK_OAK_FENCE_GATE, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.JUKEBOX, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.MOSSY_COBBLESTONE, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.NETHER_BRICK, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.NETHER_BRICK_STAIRS, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.NETHER_FENCE, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.STEP, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.LOG, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.LOG_2, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WOOD, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.STONE_SLAB2, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DOUBLE_STONE_SLAB2, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DOUBLE_STEP, new BlockConfiguration(2.0, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WOOD_STEP, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WOOD_DOUBLE_STEP, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WOOD_STAIRS, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.ACACIA_STAIRS, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SPRUCE_WOOD_STAIRS, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BIRCH_WOOD_STAIRS, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.JUNGLE_WOOD_STAIRS, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DARK_OAK_STAIRS, new BlockConfiguration(2.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.STONE, new BlockConfiguration(1.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.PRISMARINE, new BlockConfiguration(1.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SMOOTH_BRICK, new BlockConfiguration(1.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SMOOTH_STAIRS, new BlockConfiguration(1.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.HARD_CLAY, new BlockConfiguration(1.25, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.STAINED_CLAY, new BlockConfiguration(1.25, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.BANNER, new BlockConfiguration(1.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.JACK_O_LANTERN, new BlockConfiguration(1.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.MELON, new BlockConfiguration(1.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SKULL, new BlockConfiguration(1.0, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.PUMPKIN, new BlockConfiguration(1.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SIGN_POST, new BlockConfiguration(1.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WALL_SIGN, new BlockConfiguration(1.0, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.QUARTZ_BLOCK, new BlockConfiguration(0.8, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.NOTE_BLOCK, new BlockConfiguration(0.8, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.QUARTZ_STAIRS, new BlockConfiguration(0.8, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.RED_SANDSTONE, new BlockConfiguration(0.8, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.RED_SANDSTONE_STAIRS, new BlockConfiguration(0.8, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SANDSTONE, new BlockConfiguration(0.8, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SANDSTONE_STAIRS, new BlockConfiguration(0.8, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WOOL, new BlockConfiguration(0.8, ToolType.SHEARS, BlockType.NA));
		BlockProperties.blocks.put(Material.MONSTER_EGG, new BlockConfiguration(0.75, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.RAILS, new BlockConfiguration(0.7, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.ACTIVATOR_RAIL, new BlockConfiguration(0.7, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DETECTOR_RAIL, new BlockConfiguration(0.7, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.POWERED_RAIL, new BlockConfiguration(0.7, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.CLAY, new BlockConfiguration(0.6, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SOIL, new BlockConfiguration(0.6, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.GRASS, new BlockConfiguration(0.6, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.GRAVEL, new BlockConfiguration(0.6, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.MYCEL, new BlockConfiguration(0.6, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SPONGE, new BlockConfiguration(0.6, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.BREWING_STAND, new BlockConfiguration(0.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.STONE_BUTTON, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.CAKE, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.DIRT, new BlockConfiguration(0.5, ToolType.SHOVEL, BlockType.NA));
		BlockProperties.blocks.put(Material.HAY_BLOCK, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.ICE, new BlockConfiguration(0.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.LEVER, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.PACKED_ICE, new BlockConfiguration(0.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.PISTON_BASE, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.PISTON_EXTENSION, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.PISTON_MOVING_PIECE, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.PISTON_STICKY_BASE, new BlockConfiguration(0.5, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.SAND, new BlockConfiguration(0.5, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SOUL_SAND, new BlockConfiguration(0.5, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.GOLD_PLATE, new BlockConfiguration(0.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.IRON_PLATE, new BlockConfiguration(0.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.STONE_PLATE, new BlockConfiguration(0.5, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.WOOD_PLATE, new BlockConfiguration(0.5, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.CACTUS, new BlockConfiguration(0.4, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.LADDER, new BlockConfiguration(0.4, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.NETHERRACK, new BlockConfiguration(0.4, ToolType.PICKAXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.GLASS, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.STAINED_GLASS_PANE, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.STAINED_GLASS, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.THIN_GLASS, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.GLOWSTONE, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.REDSTONE_LAMP_ON, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.REDSTONE_LAMP_OFF, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.SEA_LANTERN, new BlockConfiguration(0.3, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.BED, new BlockConfiguration(0.2, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.COCOA, new BlockConfiguration(0.2, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DAYLIGHT_DETECTOR, new BlockConfiguration(0.2, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.DAYLIGHT_DETECTOR_INVERTED, new BlockConfiguration(0.2, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.HUGE_MUSHROOM_1, new BlockConfiguration(0.2, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.HUGE_MUSHROOM_2, new BlockConfiguration(0.2, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.SNOW_BLOCK, new BlockConfiguration(0.2, ToolType.SHOVEL, BlockType.WOOD));
		BlockProperties.blocks.put(Material.VINE, new BlockConfiguration(0.2, ToolType.AXE, BlockType.WOOD));
		BlockProperties.blocks.put(Material.CARPET, new BlockConfiguration(0.1, ToolType.NONE, BlockType.NA));
		BlockProperties.blocks.put(Material.SNOW, new BlockConfiguration(0.1, ToolType.SHOVEL, BlockType.WOOD));
	}

	private static long getBreakTime(final Material material, final ItemConfiguation itemConfiguration, final int slowDigLevel, final int fastDigLevel) {
		BlockConfiguration blockConfiguration = BlockProperties.blocks.get(material);
		blockConfiguration = blockConfiguration == null ? new BlockProperties.BlockConfiguration(0D, ToolType.NONE, BlockType.NA) : blockConfiguration;
		if (!blockConfiguration.isBreakable()) {
			return Long.MAX_VALUE;
		}
		final double breakSpeed = blockConfiguration.getBreakSpeed() * 30.0;
		double modifier;
		if (blockConfiguration.getToolType() == itemConfiguration.getToolType()) {
			switch (itemConfiguration.getBlockType()) {
				case WOOD:
					modifier = 2.0;
					break;
				case GOLD:
					modifier = 12.0;
					break;
				case STONE:
					modifier = 4.0;
					break;
				case IRON:
					modifier = 6.0;
					break;
				case DIAMOND:
					modifier = 8.0;
					break;
				case NA:
					modifier = 1.0;
					break;
				default:
					modifier = 1.0;
					break;
			}
			if (itemConfiguration.getToolType() == ToolType.SHEARS) {
				modifier = 15.0;
			}
			if (itemConfiguration.getEnchantmentLevel() != 0) {
				modifier += Math.pow(itemConfiguration.getEnchantmentLevel(), 2.0) + 1.0;
			}
			if (fastDigLevel > 0) {
				modifier += modifier * (fastDigLevel * 0.2);
			}
			if (slowDigLevel > 0) {
				switch (slowDigLevel) {
					case 1:
						modifier *= 0.3;
						break;
					case 2:
						modifier *= 0.09;
						break;
					case 3:
						modifier *= 0.0027;
						break;
					default:
						modifier *= 8.1E-4;
						break;
				}
			}
		} else {
			modifier = 1.0;
		}
		final double trueTime = breakSpeed / modifier;
		return Math.round(trueTime + 0.5) * 50;
	}

	@SuppressWarnings("deprecation")
	public static long getBreakTime(final Material material, final Player player) {
		int slow_digging_level = 0;
		int fast_digging_level = 0;
		for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
			if (potionEffect.getType().equals(PotionEffectType.SLOW_DIGGING)) {
				slow_digging_level = potionEffect.getAmplifier() + 1;
			} else {
				if (potionEffect.getType().equals(PotionEffectType.FAST_DIGGING)) {
					fast_digging_level = potionEffect.getAmplifier() + 1;
				}
			}
		}
		return BlockProperties.getBreakTime(material, new ItemConfiguation(player.getItemInHand()), slow_digging_level, fast_digging_level);
	}

	public static ToolType getToolType(final Material material) {
		if (BlockProperties.blocks.containsKey(material)) {
			return BlockProperties.blocks.get(material).getToolType();
		}
		return ToolType.NONE;
	}

	private enum ToolType {
		AXE,
		NONE,
		PICKAXE,
		SHEARS,
		SHOVEL
	}

	private enum BlockType {
		DIAMOND(4),
		GOLD(1),
		IRON(3),
		NA(0),
		STONE(2),
		WOOD(1);
		private final int level;

		BlockType(final int level) {
			this.level = level;
		}

		public static BlockType getBlockType(final Material material) {
			BlockConfiguration blockConfiguration = BlockProperties.blocks.get(material);
			return blockConfiguration == null ? BlockType.NA : blockConfiguration.getBlockType();
		}

		public static boolean isRightLevel(final BlockType block, final BlockType block2) {
			return block.level >= block2.level;
		}
	}

	private static class BlockConfiguration {

		private final BlockType blockType;
		private final boolean breakable;
		private final double breakSpeed;
		private final ToolType toolType;

		BlockConfiguration(final double breakSpeed, final ToolType toolType, final BlockType blockType) {
			this.breakSpeed = breakSpeed;
			this.toolType = toolType;
			this.blockType = blockType;
			this.breakable = true;
		}

		BlockConfiguration(final double breakSpeed) {
			this.breakSpeed = breakSpeed;
			this.toolType = ToolType.NONE;
			this.blockType = BlockType.NA;
			this.breakable = false;
		}

		BlockType getBlockType() {
			return this.blockType;
		}

		double getBreakSpeed() {
			return this.breakSpeed;
		}

		ToolType getToolType() {
			return this.toolType;
		}

		boolean isBreakable() {
			return this.breakable;
		}
	}

	private static class ItemConfiguation {

		private final int enchantmentLevel;
		private final BlockType blockType;
		private final ToolType toolType;

		ItemConfiguation(final ItemStack item) {
			switch (item.getType()) {
				case WOOD_PICKAXE:
					this.toolType = ToolType.PICKAXE;
					this.blockType = BlockType.WOOD;
					break;
				case GOLD_PICKAXE:
					this.toolType = ToolType.PICKAXE;
					this.blockType = BlockType.GOLD;
					break;
				case STONE_PICKAXE:
					this.toolType = ToolType.PICKAXE;
					this.blockType = BlockType.STONE;
					break;
				case IRON_PICKAXE:
					this.toolType = ToolType.PICKAXE;
					this.blockType = BlockType.IRON;
					break;
				case DIAMOND_PICKAXE:
					this.toolType = ToolType.PICKAXE;
					this.blockType = BlockType.DIAMOND;
					break;
				case WOOD_AXE:
					this.toolType = ToolType.AXE;
					this.blockType = BlockType.WOOD;
					break;
				case GOLD_AXE:
					this.toolType = ToolType.AXE;
					this.blockType = BlockType.GOLD;
					break;
				case STONE_AXE:
					this.toolType = ToolType.AXE;
					this.blockType = BlockType.STONE;
					break;
				case IRON_AXE:
					this.toolType = ToolType.AXE;
					this.blockType = BlockType.IRON;
					break;
				case DIAMOND_AXE:
					this.toolType = ToolType.AXE;
					this.blockType = BlockType.DIAMOND;
					break;
				case WOOD_SPADE:
					this.toolType = ToolType.SHOVEL;
					this.blockType = BlockType.WOOD;
					break;
				case GOLD_SPADE:
					this.toolType = ToolType.SHOVEL;
					this.blockType = BlockType.GOLD;
					break;
				case STONE_SPADE:
					this.toolType = ToolType.SHOVEL;
					this.blockType = BlockType.STONE;
					break;
				case IRON_SPADE:
					this.toolType = ToolType.SHOVEL;
					this.blockType = BlockType.IRON;
					break;
				case DIAMOND_SPADE:
					this.toolType = ToolType.SHOVEL;
					this.blockType = BlockType.DIAMOND;
					break;
				case SHEARS:
					this.toolType = ToolType.SHEARS;
					this.blockType = BlockType.NA;
					break;
				default:
					this.toolType = ToolType.NONE;
					this.blockType = BlockType.NA;
			}
			this.enchantmentLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);
		}

		BlockType getBlockType() {
			return this.blockType;
		}

		int getEnchantmentLevel() {
			return this.enchantmentLevel;
		}

		ToolType getToolType() {
			return this.toolType;
		}
	}
}
