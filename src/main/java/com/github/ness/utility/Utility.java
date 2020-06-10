package com.github.ness.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Utility {
	public static String debug = "null";

	public static String getNMS() {
		String v = Bukkit.getServer().getClass().getPackage().getName();
		return v.substring(v.lastIndexOf('.') + 1);
	}
	
	public static boolean hasHorseNear(Player p,int range) {
		for(Entity e : p.getNearbyEntities(range, range, range)) {
			if(e instanceof Vehicle) {
				return true;
			}
		}
		return false;
	}

	public static boolean isOnGround(Location loc) {
		if (loc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
			return true;
		}
		Location a = loc;
		a.setY(a.getY() - 0.5);
		if (a.getBlock().getType() != Material.AIR) {
			return true;
		}
		a = loc;
		a.setY(a.getY() + 0.5);
		return a.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR;
	}

	public static boolean isOnGroundBypassNoFall(Player p) {
		Block by = (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()))
				.getBlock();
		Block bx = (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - 1.0D,
				p.getLocation().getZ())).getBlock();

		Block b1 = (new Location(p.getWorld(), p.getLocation().getX() + 1.0D, p.getLocation().getY() - 1.0D,
				p.getLocation().getZ() - 1.0D)).getBlock();
		Block b2 = (new Location(p.getWorld(), p.getLocation().getX() + 1.0D, p.getLocation().getY() - 1.0D,
				p.getLocation().getZ() + 1.0D)).getBlock();
		Block b3 = (new Location(p.getWorld(), p.getLocation().getX() - 1.0D, p.getLocation().getY() - 1.0D,
				p.getLocation().getZ() - 1.0D)).getBlock();
		Block b4 = (new Location(p.getWorld(), p.getLocation().getX() - 1.0D, p.getLocation().getY() - 1.0D,
				p.getLocation().getZ() + 1.0D)).getBlock();

		Block x1 = (new Location(p.getWorld(), p.getLocation().getX() + 1.0D, p.getLocation().getY() - 1.0D,
				p.getLocation().getZ())).getBlock();
		Block x2 = (new Location(p.getWorld(), p.getLocation().getX() - 1.0D, p.getLocation().getY() - 1.0D,
				p.getLocation().getZ())).getBlock();
		Block x3 = (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - 1.0D,
				p.getLocation().getZ() + 1.0D)).getBlock();
		Block x4 = (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - 1.0D,
				p.getLocation().getZ() - 1.0D)).getBlock();

		Block z1 = (new Location(p.getWorld(), p.getLocation().getX() + 1.0D, p.getLocation().getY(),
				p.getLocation().getZ())).getBlock();
		Block z2 = (new Location(p.getWorld(), p.getLocation().getX() - 1.0D, p.getLocation().getY(),
				p.getLocation().getZ())).getBlock();
		Block z3 = (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
				p.getLocation().getZ() + 1.0D)).getBlock();
		Block z4 = (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
				p.getLocation().getZ() - 1.0D)).getBlock();
		if (bx.getType() == Material.AIR && b1.getType() == Material.AIR && b2.getType() == Material.AIR
				&& b3.getType() == Material.AIR && b4.getType() == Material.AIR && by.getType() == Material.AIR) {
			if (z1.getType() == Material.AIR && z2.getType() == Material.AIR && z3.getType() == Material.AIR
					&& z4.getType() == Material.AIR) {
				if (x1.getType() == Material.AIR && x2.getType() == Material.AIR && x3.getType() == Material.AIR
						&& x4.getType() == Material.AIR) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean groundAround(final Location loc) {
		for (int radius = 2, x = -radius; x < radius; ++x) {
			for (int y = -radius; y < radius; ++y) {
				for (int z = -radius; z < radius; ++z) {
					final Material mat = loc.getWorld().getBlockAt(loc.add(x, y, z)).getType();
					if (mat.isSolid() || mat.name().toLowerCase().contains("lava")
							|| mat.name().toLowerCase().contains("water")) {
						loc.subtract(x, y, z);
						return true;
					}
					loc.subtract(x, y, z);
				}
			}
		}
		return false;
	}

	public static int getPing(final Player player) {
		int ping = 900;
		try {
			final Object entityPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player,
					new Object[0]);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception ex) {
		}
		return ping;
	}

	public static void setPing(Player player, int ping) {
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."
					+ Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".entity.CraftPlayer");
			Object handle = craftPlayerClass.getMethod("getHandle", new Class[0]).invoke(craftPlayerClass.cast(player),
					new Object[0]);
			handle.getClass().getDeclaredField("ping").set(handle, Integer.valueOf(50));
		} catch (Exception exception) {

		}
	}

	public static Integer distToBlock(final Location loc) {
		Location res;
		int yDif;
		for (res = loc, yDif = -1; !res.subtract(0.0, yDif, 0.0).getBlock().getType().isSolid()
				&& res.subtract(0.0, yDif, 0.0).getY() > 0.0; ++yDif) {
			res.add(0.0, yDif, 0.0);
		}
		return yDif;
	}

	public static boolean redstoneAround(final Location loc) {
		for (int radius = 2, x = -radius; x < radius; ++x) {
			for (int y = -radius; y < radius; ++y) {
				for (int z = -radius; z < radius; ++z) {
					final Material mat = loc.getWorld().getBlockAt(loc.add(x, y, z)).getType();
					if (mat.toString().toLowerCase().contains("piston")) {
						loc.subtract(x, y, z);
						return true;
					}
					loc.subtract(x, y, z);
				}
			}
		}
		return false;
	}

	public static boolean hasPermissionBypass(Player p, String hack) {
		return p.hasPermission("ness.bypass." + hack) || p.hasPermission("ness.bypass.*");
	}

	public static List<Block> getBlocksAround(Location loc, double y) {
		List<Block> result = new ArrayList<>();
		result.add(loc.getBlock());
		result.add(loc.clone().add(0.5D, y, -0.5D).getBlock());
		result.add(loc.clone().add(0.5D, y, 0.0D).getBlock());
		result.add(loc.clone().add(-0.5D, y, 0.0D).getBlock());
		result.add(loc.clone().add(0.0D, y, -0.5D).getBlock());
		result.add(loc.clone().add(0.0D, y, 0.5D).getBlock());
		result.add(loc.clone().add(-0.5D, y, -0.5D).getBlock());
		result.add(loc.clone().add(0.5D, y, 0.5D).getBlock());
		result.add(loc.clone().add(-0.5D, y, 0.5D).getBlock());
		return result;
	}

	public static String randomString() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		return generatedString;
	}

	public static int getmcd(int n1, int n2) {
		if (n2 == 0) {
			return n1;
		}
		return getmcd(n2, n1 % n2);
	}

	public static double getmcd(double n1, double n2) {
		if (n2 == 0.0) {
			return n1;
		}
		return getmcd(n2, n1 % n2);
	}

	public static float mcdRational(float a, float b) {
		if (a == 0) {
			return b;
		}
		float ans = b / a;
		float error = Math.max(b, a) * 1E-3F;
		int quotient = (int) (ans + error);
		float remainder = ((b / a) - quotient) * a;
		if (Math.abs(remainder) < Math.max(a, b) * 1E-3F)
			remainder = 0;
		return mcdRational(remainder, a);
	}

	public static float mcdRational(List<Float> numbers) {
		float result = numbers.get(0);
		for (int i = 1; i < numbers.size(); i++) {
			result = mcdRational(numbers.get(i), result);
		}
		return result;
	}

	public static double getMaxSpeed(Location From, Location To) {
		double maxSpeed, xSpeed = Math.abs(From.getX() - To.getX());
		double zSpeed = Math.abs(From.getZ() - To.getZ());
		double xzSpeed = Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);

		if (xSpeed >= zSpeed) {
			maxSpeed = xSpeed;
		} else {
			maxSpeed = xSpeed;
		}
		if (maxSpeed < xzSpeed) {
			maxSpeed = xzSpeed;
		}
		return maxSpeed;
	}

	public static boolean hasflybypass(Player player) {
		ItemStack[] armor = player.getInventory().getArmorContents();
		if (!Bukkit.getVersion().contains("1.8")) {
			boolean haselytra = false;
			if (!(armor == null) && !(armor[2] == null)) {
				haselytra = armor[2].getType().equals(Material.ELYTRA);
			}
			if (player.hasPotionEffect(PotionEffectType.LEVITATION) || player.isGliding() || haselytra
					|| player.isFlying()) {
				return true;
			} else {
				return false;
			}
		} else {
			if (player.isFlying()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean isPureAscii(String v) {
		return StandardCharsets.US_ASCII.newEncoder().canEncode(v.replaceAll("¡", "!").replaceAll("¿", "?"));
	}

	public static boolean hasBlock(Player p, Material m) {
		boolean done = false;
		for (int i = 0; i < 256; i++) {
			Location loc = p.getLocation();
			loc.setY(i);
			if (loc.getBlock().getType().equals(m)) {
				done = true;
			}
		}
		return done;
	}

	public static boolean SpecificBlockNear(Location loc, Material m) {
		for (Block b : Utility.getSurrounding(loc.getBlock(), true)) {
			if (b.getType().equals(m)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkGround(double y) {

		if (y % (1D / 64D) == 0) {
			return true;
		}

		return false;
	}

	public static double calcDamage(double dist) {
		return (dist * 0.5) - 1.5;
	}

	public static double getRotations(Player player) {
		return -Math.atan2(player.getLocation().getY() + player.getEyeHeight(),
				Math.sqrt(player.getLocation().getX() * player.getLocation().getX()
						+ player.getLocation().getZ() * player.getLocation().getZ()))
				* 180.0D / Math.PI;
	}

	public static boolean onSteps(Location loc) {
		ArrayList<Block> list = Utility.getSurrounding(loc.getBlock(), true);
		boolean bypass = false;
		for (Block b : list) {
			String material = b.getType().name().toLowerCase();
			if (material.contains("anvil")) {
				bypass = true;
			} else if (material.contains("stair")) {
				bypass = true;
			} else if (material.contains("slab")) {
				bypass = true;
			} else if (material.contains("step")) {
				bypass = true;
			} else if (material.contains("carpet")) {
				bypass = true;
			} else if (material.contains("snow")) {
				bypass = true;
			}
		}
		return bypass;
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

	public static double getDistance3D(Location one, Location two) {
		double toReturn = 0.0D;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double ySqr = (two.getY() - one.getY()) * (two.getY() - one.getY());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + ySqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
	}

	public static double clamp180(double theta) {
		theta %= 360.0D;
		if (theta >= 180.0D) {
			theta -= 360.0D;
		}
		if (theta < -180.0D) {
			theta += 360.0D;
		}
		return theta;
	}

	public static float clamp180(float theta) {
		theta %= 360.0D;
		if (theta >= 180.0D) {
			theta -= 360.0D;
		}
		if (theta < -180.0D) {
			theta += 360.0D;
		}
		return theta;
	}

	public static double getHorizontalDistance(Location one, Location two) {
		double toReturn = 0.0D;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
	}

	public static boolean flagyStuffNear(Location loc) {
		boolean nearBlocks = false;
		Iterator<Block> var3 = Utility.getSurrounding(loc.getBlock(), true).iterator();

		while (var3.hasNext()) {
			Block bl = var3.next();
			if (bl.getType().name().toLowerCase().contains("step")) {
				nearBlocks = true;

				break;
			}
		}
		var3 = Utility.getSurrounding(loc.getBlock(), false).iterator();

		while (var3.hasNext()) {
			Block bl = var3.next();
			if (bl.getType().name().toLowerCase().contains("step")) {
				nearBlocks = true;

				break;
			}
		}
		if (isBlock(loc.getBlock().getRelative(BlockFace.DOWN), new Material[] { Material.STEP, Material.BED,
				Material.DOUBLE_STEP, Material.WOOD_DOUBLE_STEP, Material.WOOD_STEP })) {
			nearBlocks = true;
		}

		return nearBlocks;
	}

	public static int getPotionEffectLevel(Player p, PotionEffectType pet) {
		Iterator<PotionEffect> var4 = p.getActivePotionEffects().iterator();

		while (var4.hasNext()) {
			PotionEffect pe = var4.next();
			if (pe.getType().getName().equals(pet.getName())) {
				return pe.getAmplifier() + 1;
			}
		}

		return 0;
	}

	public static boolean isBlock(Block block, Material[] materials) {
		Material type = block.getType();
		Material[] arrayOfMaterial = materials;
		int j = materials.length;

		for (int i = 0; i < j; i++) {
			Material m = arrayOfMaterial[i];
			if (m == type) {
				return true;
			}
		}

		return false;
	}

	public static double around(double i, int places) {
		String around;
		try {
			if (Double.toString(i).length() > places - 2) {
				if (!Double.toString(i).contains("-")) {
					around = Double.toString(i).substring(0, places - 1);
				} else {
					around = Double.toString(i).substring(0, places);
				}
			} else {
				around = Math.round(i) + "";
			}
		} catch (Exception e) {
			return i;
		}
		return Double.parseDouble(around);
	}

	public static void createChecks(PlayerMoveEvent e) {
		Location From = e.getFrom();
		Player p = e.getPlayer();
		Location To = e.getTo();
		double maxSpeed = Utility.getMaxSpeed(From, To);
		double ydist = From.getY() - To.getY();
		p.sendMessage("-------------------------------------");
		p.sendMessage("xz distance= " + Utility.around(maxSpeed, 6) + " y distance="
				+ Double.toString(Utility.around(ydist, 6)));
		p.sendMessage(
				"FallDistance= " + Utility.around(p.getFallDistance(), 6) + " NoDamageTicks= " + p.getNoDamageTicks());
		// p.sendMessage("YSin= " + Utility.around(Math.sin(ydist), 6) + " YCos= " +
		// Utility.around(Math.cos(ydist), 6)
		// + " YDist%= " + Utility.around(ydist%1.0, 6));
		p.sendMessage("YDist%= " + Utility.around(ydist % 0.5, 6));
		// p.sendMessage("Velocity= " + Utility.around(p.getVelocity().getX(), 6) + " "
		// + Utility.around(p.getVelocity().getY(), 6) + " " +
		// Utility.around(p.getVelocity().getZ(), 6));
		// p.sendMessage("EyeHeight= "+p.getEyeHeight());
		p.sendMessage("-------------------------------------");
		// p.sendMessage("EyeLocation= "+p.getEyeLocation());
	}

	public static Location getEyeLocation(Player player) {
		final Location eye = player.getLocation();
		eye.setY(eye.getY() + player.getEyeHeight());
		return eye;
	}

	public static boolean isInWater(Player player) {
		final Material m = player.getLocation().getBlock().getType();
		return m.name().toLowerCase().contains("water");
	}

	public static boolean isInAir(Player player) {
		for (Block block : Utility.getSurrounding(player.getLocation().getBlock(), false)) {
			if (block.getType() == Material.AIR) {
				return true;
			}
		}
		return player.getLocation().getBlock().getType() == Material.AIR;
	}

	public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		if (diagonals) {
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					for (int z = -2; z <= 2; z++) {
						if ((x != 0) || (y != 0) || (z != 0)) {
							blocks.add(block.getRelative(x, y, z));
						}
					}
				}
			}
		} else {
			blocks.add(block.getRelative(BlockFace.UP));
			blocks.add(block.getRelative(BlockFace.DOWN));
			blocks.add(block.getRelative(BlockFace.NORTH));
			blocks.add(block.getRelative(BlockFace.SOUTH));
			blocks.add(block.getRelative(BlockFace.EAST));
			blocks.add(block.getRelative(BlockFace.WEST));
		}
		return blocks;
	}

	public static double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static boolean isOnSlime(Player p) {
		return Utilities.getPlayerUnderBlock(p).getType().equals(Material.SLIME_BLOCK);
	}

	public static boolean isOnSpecificBlock(Player p, Material type) {
		return Utilities.getPlayerUnderBlock(p).getType().equals(type);
	}

	public static Vector getHorizontalVector(final Vector v) {
		v.setY(0);
		return v;
	}

	public static boolean blockAdjacentIsSolid(Location loc) {
		Location check = loc.clone();
		Set<Block> sample = new HashSet<>();
		sample.add(getBlock(check.add(0, 0, 0.3)));
		sample.add(getBlock(check.add(0.3, 0, 0)));
		sample.add(getBlock(check.add(0, 0, -0.3)));
		sample.add(getBlock(check.add(0, 0, -0.3)));
		sample.add(getBlock(check.add(-0.3, 0, 0)));
		sample.add(getBlock(check.add(-0.3, 0, 0)));
		sample.add(getBlock(check.add(0, 0, 0.3)));
		sample.add(getBlock(check.add(0, 0, 0.3)));
		for (Block b : sample) {
			if (b != null && b.getType().isSolid())
				return true;
		}
		return false;
	}

	public static boolean blockAdjacentIsLiquid(Location loc) {
		Location check = loc.clone();
		Set<Block> sample = new HashSet<>();
		sample.add(getBlock(check.add(0, 0, 0.3)));
		sample.add(getBlock(check.add(0.3, 0, 0)));
		sample.add(getBlock(check.add(0, 0, -0.3)));
		sample.add(getBlock(check.add(0, 0, -0.3)));
		sample.add(getBlock(check.add(-0.3, 0, 0)));
		sample.add(getBlock(check.add(-0.3, 0, 0)));
		sample.add(getBlock(check.add(0, 0, 0.3)));
		sample.add(getBlock(check.add(0, 0, 0.3)));
		for (Block b : sample) {
			if (b != null && b.isLiquid())
				return true;
		}
		return false;

	}

	public static boolean matIsAdjacent(Location loc, Material material) {
		Location check = loc.clone();
		return getBlock(check.add(0, 0, 0.3)).getType() == material
				|| getBlock(check.add(0.3, 0, 0)).getType() == material
				|| getBlock(check.add(0, 0, -0.3)).getType() == material
				|| getBlock(check.add(0, 0, -0.3)).getType() == material
				|| getBlock(check.add(-0.3, 0, 0)).getType() == material
				|| getBlock(check.add(-0.3, 0, 0)).getType() == material
				|| getBlock(check.add(0, 0, 0.3)).getType() == material
				|| getBlock(check.add(0, 0, 0.3)).getType() == material;
	}

	public static Block getBlock(Location loc) {
		return loc.getBlock();
	}

	public static Vector getVerticalVector(final Vector v) {
		v.setX(0);
		v.setZ(0);
		return v;
	}

	public static String decrypt(String strEncrypted) {
		String strData = "";

		try {
			byte[] decoded = Base64.getDecoder().decode(strEncrypted);
			strData = (new String(decoded, StandardCharsets.UTF_8) + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return strData;
	}

	public static String ArrayToString(String[] list) {
		String string = "";
		for (final String key : list) {
			string = String.valueOf(string) + key + ",";
		}
		if (string.length() != 0) {
			return string.substring(0, string.length() - 1);
		}
		return null;
	}

	public static String ArrayToString(List<String> list) {
		String string = "";
		for (final String key : list) {
			string = String.valueOf(string) + key + ",";
		}
		if (string.length() != 0) {
			return string.substring(0, string.length() - 1);
		}
		return null;
	}

	public static String[] StringToArray(String string, String split) {
		return string.split(split);
	}

	public static double getFraction(final double value) {
		return value % 1.0;
	}

	public static List<Entity> getNearbyRidables(Location loc, double distance) {
		final List<Entity> entities = new ArrayList<>();
		for (final Entity entity : new ArrayList<>(loc.getWorld().getEntities())) {
			if (!entity.getType().equals(EntityType.HORSE) && !entity.getType().equals(EntityType.BOAT)) {
				continue;
			}
			Bukkit.getServer()
					.broadcastMessage(new StringBuilder(String.valueOf(entity.getLocation().distance(loc))).toString());
			if (entity.getLocation().distance(loc) > distance) {
				continue;
			}
			entities.add(entity);
		}
		return entities;
	}

	public static ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> list = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			list.add(player);
		}
		return list;
	}

	public static List<Entity> getEntities(final World world) {
		return world.getEntities();
	}

	public static boolean hasKbBypass(Player player) {
		if (player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(-1.0D, 0.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(0.0D, 0.0D, -1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(1.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(0.0D, 1.0D, 1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(-1.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(0.0D, 1.0D, -1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(1.0D, 2.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(0.0D, 2.0D, 1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(0.0D, 2.0D, -1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(-1.0D, 2.0D, 0.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(1.0D, 2.0D, 1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getLocation().add(-1.0D, 2.0D, -1.0D).getBlock().getType() != Material.AIR)
			return true;
		if (player.getVehicle() != null)
			return true;
		return false;
	}

}
