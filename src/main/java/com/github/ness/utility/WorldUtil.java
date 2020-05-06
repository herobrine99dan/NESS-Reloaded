package com.github.ness.utility;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * @author Elg
 */
public final class WorldUtil {

    public static final int INVALID_LOCATION = Integer.MIN_VALUE;
    public static final int LOCATION_ERR = -1;

    /**
     * @param location
     *     The locations (only {@code x} and {@code z} matters)
     *
     * @return The highest block that is "valid" or null if the location is null or no block can be found
     */
    public static Block highestValidBlock(final Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        final int y = highestValidBlockY(location);
        //return null if there is no valid location
        if (y == INVALID_LOCATION || y == LOCATION_ERR) {
            return null;
        }
        return location.getWorld().getBlockAt(location.getBlockX(), y, location.getBlockZ());
    }

    public static int highestValidBlockY(final Location location) {
        if (location == null || location.getWorld() == null) {
//            Logger.log(Severity.FINEST,
//                       "Could not validate the location due it being invalid (either null or the world is null) |  " +
//                       "location: " + location);
            return LOCATION_ERR;
        }
        //get the block that minecraft says is the highest block
        final Block vanillaHighestBlock = location.getWorld().getHighestBlockAt(location);

        //validate the block
        return getValidHeight(vanillaHighestBlock);
    }

    /**
     * This method looks at the material of the block and either <ol> <li> If {@link #isNotGround(Material)} returns
     * true it will loop blocks until it hits any block that does not return true for {@link #isNotGround(Material)}.
     * </li> <li> If the block is a liquid ({@link #isLiquid(Material)} returns true) or lilly pad then the block is
     * invalid. </li> <li>Otherwise return the Y coordinate at the given block.</li> </ol>
     *
     * @param block
     *     Block to check
     *
     * @return The y location of a valid block or {@link #INVALID_LOCATION} if the location is invalid
     */
    public static int getValidHeight(final Block block) {
        if (block == null) {
//            Logger.log(Severity.FINEST, "Tried to verify height for null block");
            return LOCATION_ERR;
        }

        Block checkBlock = blockBelow(block);
//        Logger.log(Severity.FINEST, "block below is " + checkBlock);
        int y = checkBlock.getY();
        final Material orgMaterial = checkBlock.getType();

        if (isNotGround(orgMaterial)) {
            //Get the first valid block on solid ground
            while (isNotGround(checkBlock.getType())) {
                if (checkBlock.getY() < 0) {
                    return INVALID_LOCATION;
                } //hit rock bottom
                checkBlock = blockBelow(checkBlock);
            }
            y = checkBlock.getY();
        }
        if (isLiquid(orgMaterial) || orgMaterial == Material.WATER_LILY) {
            return INVALID_LOCATION;
        }

        //Add one to y since we removed one one when y is first defined
        y++;
//        Logger.log(Severity.FINEST, "returning " + y);
        return y;
    }

    /**
     * @return The block below {@code block}
     */
    private static Block blockBelow(final Block block) {
        return block.getRelative(BlockFace.DOWN);
    }

    /**
     * @param material
     *     The material to check
     *
     * @return {@code True} if {@code material} is flammable, air, cactus or sugar cane block
     */
    public static boolean isNotGround(final Material material) {
        return material.isFlammable() || material == Material.AIR || material == Material.CACTUS ||
               material == Material.SUGAR_CANE_BLOCK;
    }

    public static boolean isLiquid(final Material mat) {
        return mat == Material.LAVA || mat == Material.STATIONARY_LAVA || mat == Material.WATER ||
               mat == Material.STATIONARY_WATER;
    }
}
