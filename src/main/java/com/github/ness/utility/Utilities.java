package com.github.ness.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public final class Utilities {
	public static final List<Material> INSTANT_BREAK = new ArrayList<>();
	public static final List<Material> FOOD = new ArrayList<>();
	public static final List<Material> INTERACTABLE = new ArrayList<>();
	public static List<Material> stepableMaterials;

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static boolean IsSameBlockAround(Player p, Material mat, float add_y, float width) {
		Material new_loc1 = p.getLocation().add(0, add_y, 0).getBlock().getType();

		Material new_loc2 = p.getLocation().add(width, add_y, 0).getBlock().getType();
		Material new_loc3 = p.getLocation().add(-width, add_y, 0).getBlock().getType();

		Material new_loc4 = p.getLocation().add(0, add_y, width).getBlock().getType();
		Material new_loc5 = p.getLocation().add(0, add_y, -width).getBlock().getType();

		Material new_loc6 = p.getLocation().add(-width, add_y, -width).getBlock().getType();
		Material new_loc7 = p.getLocation().add(width, add_y, width).getBlock().getType();

		Material new_loc8 = p.getLocation().add(width, add_y, -width).getBlock().getType();
		Material new_loc9 = p.getLocation().add(-width, add_y, width).getBlock().getType();

		if (new_loc1 == mat && new_loc2 == mat && new_loc3 == mat && new_loc4 == mat && new_loc5 == mat
				&& new_loc6 == mat && new_loc7 == mat && new_loc8 == mat && new_loc9 == mat) {
			return true;
		} else {
			return false;
		}
	}
	
    public static float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }
    
    public static float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }
    
    public static double[] getOffsetFromEntity(Player player, LivingEntity entity) {
        double yawOffset = Math.abs(Utilities.yawTo180F(player.getEyeLocation().getYaw()) - Utilities.yawTo180F(Utilities.getRotations(player.getLocation(), entity.getLocation())[0]));
        double pitchOffset = Math.abs(Math.abs(player.getEyeLocation().getPitch()) - Math.abs(Utilities.getRotations(player.getLocation(), entity.getLocation())[1]));
        return new double[]{yawOffset, pitchOffset};
    }

	public static boolean BlockOverPlayer(Location loc) {
		int onOtherBlockX = 0;
		int onOtherBlockZ = 0;

		loc = loc.clone().add(0, 2, 0);

		if (loc.getX() % 1.0 > 0.7 || (loc.getX() % 1.0 > -0.3 && loc.getX() % 1.0 < 0)) {
			onOtherBlockX = 1;
		} else if ((loc.getX() % 1.0 < 0.3 && loc.getX() % 1.0 > 0) || loc.getX() % 1.0 < -0.7) {
			onOtherBlockX = -1;
		}

		if (loc.getZ() % 1.0 > 0.7 || (loc.getZ() % 1.0 > -0.3 && loc.getZ() % 1.0 < 0)) {
			onOtherBlockZ = 1;
		} else if ((loc.getZ() % 1.0 < 0.3 && loc.getZ() % 1.0 > 0) || loc.getZ() % 1.0 < -0.7) {
			onOtherBlockZ = -1;
		}

		Location toCheckLoc1 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + onOtherBlockZ);
		Location toCheckLoc2 = new Location(loc.getWorld(), loc.getX() + onOtherBlockX, loc.getY(),
				loc.getZ() + onOtherBlockZ);
		Location toCheckLoc3 = new Location(loc.getWorld(), loc.getX() + onOtherBlockX, loc.getY(), loc.getZ());

		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		if (toCheckLoc1.getBlock().getType() != Material.AIR) {
			return true;
		}
		if (toCheckLoc2.getBlock().getType() != Material.AIR) {
			return true;
		}
		if (toCheckLoc3.getBlock().getType() != Material.AIR) {
			return true;
		}
		return false;
	}

	public static boolean onBlock(final Location loc) {
		if (loc.getX() % 1.0 >= 0.690 && loc.getX() % 1.0 <= 0.708) {
			return true;
		}

		if (loc.getX() % 1.0 >= 0.292 && loc.getX() % 1.0 <= 0.310) {
			return true;
		}

		if (loc.getX() % 1.0 <= -0.690 && loc.getX() % 1.0 >= -0.708) {
			return true;
		}

		if (loc.getX() % 1.0 <= -0.292 && loc.getX() % 1.0 >= -0.310) {
			return true;
		}

		if (loc.getZ() % 1.0 >= 0.690 && loc.getZ() % 1.0 <= 0.708) {
			return true;
		}

		if (loc.getZ() % 1.0 >= 0.292 && loc.getZ() % 1.0 <= 0.310) {
			return true;
		}

		if (loc.getZ() % 1.0 <= -0.690 && loc.getZ() % 1.0 >= -0.708) {
			return true;
		}

		if (loc.getZ() % 1.0 <= -0.292 && loc.getZ() % 1.0 >= -0.310) {
			return true;
		}

		return false;
	}

	public static String DeterminateDirection(float yaw) {
		if (yaw < 0.0F) {
			yaw += 360.0F;
		}
		if ((yaw >= 315.0F) || (yaw < 45.0F)) {
			return "sud";
		}
		if (yaw < 135.0F) {
			return "ovest";
		}
		if (yaw < 225.0F) {
			return "nord";
		}
		if (yaw < 315.0F) {
			return "est";
		}
		return "nord";
	}

	public static double roundX(double d) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(5, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static float rotate180(float f) {
		if ((f = (float) (f % 360.0D)) >= 180.0D) {
			f = (float) (f - 360.0D);
		}
		if (f < -180.0D) {
			f = (float) (f + 360.0D);
		}
		return f;
	}

	public static void CommandExecute(String command) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public static Vector VectorReturn(double pitchh, double yav) {
		double pitch = (pitchh + 90.0D) * 3.141592653589793D / 180.0D;
		double yaw = (yav + 90.0D) * 3.141592653589793D / 180.0D;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		Vector vector = new Vector(x, z, y);
		return vector;
	}

	public boolean hasUnderBlock(Player player) {
		Block block = player.getEyeLocation().getBlock().getRelative(BlockFace.UP);
		boolean b = block != null && block.getType().isSolid();
		return b;
	}

	public static boolean isOnStairs(Player player) {
		Material material = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
		boolean b = material.name().toLowerCase().contains("stair");
		return b;
	}

	public static boolean isStairs(Block b) {
		Material material = b.getType();
		return material.name().toLowerCase().contains("stair");
	}

	public static boolean isInLiquid(Player player) {
		boolean b = player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid();
		return b;
	}

	public static boolean isOnWeb(Player player) {
		boolean b = player.getLocation().getBlock() != null
				&& player.getLocation().getBlock().getType() == Material.WEB;
		return b;
	}

	public static int getScoreboard(String nome, String boarded) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		Objective objective = board.getObjective(boarded);
		Score score = objective.getScore(nome);
		int playerscore = score.getScore();
		return playerscore;
	}

	public static void setScoreboard(String nome, String boarded, int variabile) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		Objective objective = board.getObjective(boarded);
		Score score = objective.getScore(nome);
		score.setScore(variabile);
	}

	public static Block getPlayerBlock(Player p) {
		return p.getLocation().getBlock();
	}

	public static Block getPlayerUnderBlock(Player p) {
		return p.getLocation().getBlock().getRelative(BlockFace.DOWN);
	}

	public static Block getPlayerUpperBlock(Player p) {
		return p.getLocation().getBlock().getRelative(BlockFace.UP);
	}

	public static boolean isAscending(Player p) {
		return (p.getVelocity().getY() > 0.0D);
	}

	public static boolean isDescending(Player p) {
		return (p.getVelocity().getY() < 0.0D);
	}

	public static boolean cantStandAtWater(Block block) {
		Block otherBlock = block.getRelative(BlockFace.DOWN);
		boolean isHover = (block.getType() == Material.AIR);
		boolean n = (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.WATER);
		boolean s = (otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.WATER);
		boolean e = (otherBlock.getRelative(BlockFace.EAST).getType() == Material.WATER);
		boolean w = (otherBlock.getRelative(BlockFace.WEST).getType() == Material.WATER);
		boolean ne = (otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.WATER);
		boolean nw = (otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.WATER);
		boolean se = (otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.WATER);
		boolean sw = (otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.WATER);
		return (n && s && e && w && ne && nw && se && sw && isHover);
	}

	public static boolean canStandWithin(Block block) {
		boolean isSand = (block.getType() == Material.SAND);
		boolean isGravel = (block.getType() == Material.GRAVEL);
		boolean solid = (block.getType().isSolid() && !block.getType().name().toLowerCase().contains("door")
				&& !block.getType().name().toLowerCase().contains("fence")
				&& !block.getType().name().toLowerCase().contains("bars")
				&& !block.getType().name().toLowerCase().contains("sign"));

		return (!isSand && !isGravel && !solid);
	}

	public static Vector getRotation(Location one, Location two) {
		double dx = two.getX() - one.getX();
		double dy = two.getY() - one.getY();
		double dz = two.getZ() - one.getZ();
		double distanceXZ = Math.sqrt(dx * dx + dz * dz);
		float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(dy, distanceXZ) * 180.0D / Math.PI);
		return new Vector(yaw, pitch, 0.0F);
	}

	public static boolean cantStandAt(Block block) {
		return (!canStand(block) && cantStandClose(block) && cantStandFar(block));
	}

	public static boolean cantStandAtExp(Location location) {
		return cantStandAt((new Location(location.getWorld(), fixXAxis(location.getX()), location.getY() - 0.01D,
				location.getBlockZ())).getBlock());
	}

	public static boolean cantStandClose(Block block) {
		return (!canStand(block.getRelative(BlockFace.NORTH)) && !canStand(block.getRelative(BlockFace.EAST))
				&& !canStand(block.getRelative(BlockFace.SOUTH)) && !canStand(block.getRelative(BlockFace.WEST)));
	}

	public static boolean cantStandFar(Block block) {
		return (!canStand(block.getRelative(BlockFace.NORTH_WEST)) && !canStand(block.getRelative(BlockFace.NORTH_EAST))
				&& !canStand(block.getRelative(BlockFace.SOUTH_WEST))
				&& !canStand(block.getRelative(BlockFace.SOUTH_EAST)));
	}

	public static boolean canStand(Block block) {
		return (!block.isLiquid() && block.getType() != Material.AIR);
	}

	public static boolean isFullyInWater(Location player) {
		double touchedX = fixXAxis(player.getX());

		if (!(new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ())).getBlock().isLiquid()
				&& !(new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ()))
						.getBlock().isLiquid()) {
			return true;
		}

		return ((new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ())).getBlock().isLiquid()
				&& (new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ())).getBlock()
						.isLiquid());
	}

	public static double fixXAxis(double x) {
		double touchedX = x;
		double rem = touchedX - Math.round(touchedX) + 0.01D;
		if (rem < 0.3D) {
			touchedX = (NumberConversions.floor(x) - 1);
		}
		return touchedX;
	}

	public static boolean isHoveringOverWater(Location player, int blocks) {
		for (int i = player.getBlockY(); i > player.getBlockY() - blocks; i--) {
			Block newloc = (new Location(player.getWorld(), player.getBlockX(), i, player.getBlockZ())).getBlock();
			if (newloc.getType() != Material.AIR) {
				return newloc.isLiquid();
			}
		}

		return false;
	}

	public static boolean isHoveringOverWater(Location player) {
		return isHoveringOverWater(player, 25);
	}

	public static boolean isInstantBreak(Material m) {
		return INSTANT_BREAK.contains(m);
	}

	public static boolean isFood(Material m) {
		return FOOD.contains(m);
	}

	public static boolean isInteractable(Material m) {
		if (m == null)
			return false;

		return INTERACTABLE.contains(m);
	}

	public static boolean sprintFly(Player player) {
		return !(!player.isSprinting() && !player.isFlying());
	}

	public static boolean isOnLilyPad(Player player) {
		Block block = player.getLocation().getBlock();
		Material lily = Material.WATER_LILY;

		return !(block.getType() != lily && block.getRelative(BlockFace.NORTH).getType() != lily
				&& block.getRelative(BlockFace.SOUTH).getType() != lily
				&& block.getRelative(BlockFace.EAST).getType() != lily
				&& block.getRelative(BlockFace.WEST).getType() != lily);
	}

	public static boolean isSubmersed(Player player) {
		return (player.getLocation().getBlock().isLiquid()
				&& player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid());
	}

	public static boolean isInWater(Player player) {
		return !(!player.getLocation().getBlock().isLiquid()
				&& !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
				&& !player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid());
	}

	public static boolean isInWeb(Player player) {
		return !(player.getLocation().getBlock().getType() != Material.WEB
				&& player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.WEB
				&& player.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.WEB);
	}

	public static boolean isClimbableBlock(Block block) {
		return !(block.getType() != Material.VINE && block.getType() != Material.LADDER
				&& block.getType() != Material.WATER && block.getType() != Material.STATIONARY_WATER);
	}

	public static boolean isOnVine(Player player) {
		return (player.getLocation().getBlock().getType() == Material.VINE);
	}

	public static boolean isOnIce(Player p, boolean strict) {
		if (Utility.isOnGround(p) || strict) {

			List<Material> materials = getMaterialsAround(p.getLocation().clone().add(0.0D, -0.001D, 0.0D));
			return !(!materials.contains(Material.ICE) && !materials.contains(Material.PACKED_ICE));
		}

		List<Material> m1 = getMaterialsAround(p.getLocation().clone().add(0.0D, -1.0D, 0.0D));
		List<Material> m2 = getMaterialsAround(p.getLocation().clone().add(0.0D, -2.0D, 0.0D));
		return !(!m1.contains(Material.ICE) && !m1.contains(Material.PACKED_ICE) && !m2.contains(Material.ICE)
				&& !m2.contains(Material.PACKED_ICE));
	}

	public static boolean isOnSteps(Player p) {
		List<Material> materials = getMaterialsAround(p.getLocation().clone().add(0.0D, -0.001D, 0.0D));
		for (Material m : materials) {
			if (isStepable(m))
				return true;
		}
		return false;
	}

	public static List<Material> getMaterialsAround(Location loc) {
		List<Material> result = new ArrayList<>();
		result.add(loc.getBlock().getType());
		result.add(loc.clone().add(0.3D, 0.0D, -0.3D).getBlock().getType());
		result.add(loc.clone().add(-0.3D, 0.0D, -0.3D).getBlock().getType());
		result.add(loc.clone().add(0.3D, 0.0D, 0.3D).getBlock().getType());
		result.add(loc.clone().add(-0.3D, 0.0D, 0.3D).getBlock().getType());
		return result;
	}

	public static boolean isAround(Location loc, Material mat) {
		Block blockDown = loc.getBlock().getRelative(BlockFace.DOWN);

		ArrayList<Material> materials = new ArrayList<>();
		materials.add(blockDown.getType());
		materials.add(blockDown.getRelative(BlockFace.NORTH).getType());
		materials.add(blockDown.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(blockDown.getRelative(BlockFace.EAST).getType());
		materials.add(blockDown.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(blockDown.getRelative(BlockFace.SOUTH).getType());
		materials.add(blockDown.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(blockDown.getRelative(BlockFace.WEST).getType());
		materials.add(blockDown.getRelative(BlockFace.NORTH_WEST).getType());

		Block blockDown2 = loc.getBlock().getRelative(BlockFace.DOWN, 2);
		materials.add(blockDown2.getType());
		materials.add(blockDown2.getRelative(BlockFace.NORTH).getType());
		materials.add(blockDown2.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(blockDown2.getRelative(BlockFace.EAST).getType());
		materials.add(blockDown2.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(blockDown2.getRelative(BlockFace.SOUTH).getType());
		materials.add(blockDown2.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(blockDown2.getRelative(BlockFace.WEST).getType());
		materials.add(blockDown2.getRelative(BlockFace.NORTH_WEST).getType());

		Block block = loc.getBlock();
		materials.add(block.getType());
		materials.add(block.getRelative(BlockFace.NORTH).getType());
		materials.add(block.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(block.getRelative(BlockFace.EAST).getType());
		materials.add(block.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(block.getRelative(BlockFace.SOUTH).getType());
		materials.add(block.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(block.getRelative(BlockFace.WEST).getType());
		materials.add(block.getRelative(BlockFace.NORTH_WEST).getType());

		Block blockUp = loc.getBlock().getRelative(BlockFace.UP);
		materials.add(blockUp.getType());
		materials.add(blockUp.getRelative(BlockFace.NORTH).getType());
		materials.add(blockUp.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(blockUp.getRelative(BlockFace.EAST).getType());
		materials.add(blockUp.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(blockUp.getRelative(BlockFace.SOUTH).getType());
		materials.add(blockUp.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(blockUp.getRelative(BlockFace.WEST).getType());
		materials.add(blockUp.getRelative(BlockFace.NORTH_WEST).getType());

		for (Material m : materials) {
			if (m != mat)
				return false;
		}
		return true;
	}

	public static Location getPlayerStandOnBlockLocation(Location locationUnderPlayer, Material mat) {
		Location b11 = locationUnderPlayer.clone().add(0.3D, 0.0D, -0.3D);
		if (b11.getBlock().getType() != mat) {
			return b11;
		}
		Location b12 = locationUnderPlayer.clone().add(-0.3D, 0.0D, -0.3D);
		if (b12.getBlock().getType() != mat) {
			return b12;
		}
		Location b21 = locationUnderPlayer.clone().add(0.3D, 0.0D, 0.3D);
		if (b21.getBlock().getType() != mat) {
			return b21;
		}
		Location b22 = locationUnderPlayer.clone().add(-0.3D, 0.0D, 0.3D);
		if (b22.getBlock().getType() != mat) {
			return b22;
		}
		return locationUnderPlayer;
	}

	public static boolean isInBlock(Player p, Material block) {
		Location loc = p.getLocation().add(0.0D, 0.0D, 0.0D);
		return (getPlayerStandOnBlockLocation(loc, block).getBlock().getType() == block);
	}

	@SuppressWarnings("deprecation")
	public static boolean isOnWater(Player p, double depth) {
		Location loc = p.getLocation().subtract(0.0D, depth, 0.0D);
		return !(getPlayerStandOnBlockLocation(loc, Material.STATIONARY_WATER).getBlock()
				.getType() != Material.STATIONARY_WATER && Bukkit.getVersion().contains("1.13")
				|| getPlayerStandOnBlockLocation(loc, Material.STATIONARY_WATER).getBlock().getType().getId() != 8);
	}

	public static boolean isOnEntity(Player p, EntityType type) {
		for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 1.0D, 1.0D, 1.0D)) {
			if (e.getType() == type && e.getLocation().getY() < p.getLocation().getY())
				return true;
		}
		return false;
	}

	public static List<Material> getStepableMaterials() {
		return stepableMaterials;
	}

	public static boolean isStepable(Material m) {
		return getStepableMaterials().contains(m);
	}

	public static boolean isStepable(Block b) {
		return isStepable(b.getType());
	}

	public static boolean canReallySeeEntity(Player p, LivingEntity e) {
		BlockIterator bl = new BlockIterator((LivingEntity) p, 7);

		boolean found = false;

		double md = 1.0D;

		if (e.getType() == EntityType.WITHER) {

			md = 9.0D;
		} else if (e.getType() == EntityType.ENDERMAN) {

			md = 5.0D;
		} else {

			md += e.getEyeHeight();
		}

		while (bl.hasNext()) {

			found = true;

			double d = bl.next().getLocation().distanceSquared(e.getLocation());

			if (d <= md) {
				return true;
			}
		}

		bl = null;

		if (!found) {
			return true;
		}

		return false;
	}

	public static LivingEntity getTarget(Player player) {
		int range = 8;
		ArrayList<LivingEntity> livingE = new ArrayList<>();

		for (Entity e : player.getNearbyEntities(range, range, range)) {
			if (e instanceof LivingEntity) {
				livingE.add((LivingEntity) e);
			}
		}

		LivingEntity target = null;
		BlockIterator bItr = new BlockIterator((LivingEntity) player, range);

		double md = Double.MAX_VALUE;

		while (bItr.hasNext()) {
			Block block = bItr.next();
			int bx = block.getX();
			int by = block.getY();
			int bz = block.getZ();

			for (LivingEntity e : livingE) {
				Location loc = e.getLocation();
				double ex = loc.getX();
				double ey = loc.getY();
				double ez = loc.getZ();
				double d = loc.distanceSquared(player.getLocation());
				if (e.getType() == EntityType.HORSE) {

					if (bx - 1.2D <= ex && ex <= bx + 2.2D && bz - 1.2D <= ez && ez <= bz + 2.2D && by - 2.5D <= ey
							&& ey <= by + 4.5D && d < md) {
						md = d;
						target = e;
					}

					continue;
				}

				if (bx - 0.8D <= ex && ex <= bx + 1.85D && bz - 0.8D <= ez && ez <= bz + 1.85D && by - 2.5D <= ey
						&& ey <= by + 4.5D && d < md) {
					md = d;
					target = e;
				}
			}
		}

		livingE.clear();
		return target;
	}
}
