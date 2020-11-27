package com.github.ness.data;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.ness.blockgetter.MaterialAccess;

import lombok.Getter;

public class ImmutableBlock {
    @Getter
    private final int x, y, z;
    @Getter
    private final String type;
    @Getter
    private final boolean occluding;
    @Getter
    private final boolean solid;

    public ImmutableBlock(int x, int y, int z, String type, boolean solid, boolean occluding) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.solid = solid;
        this.occluding = occluding;
    }

    public static ImmutableBlock of(Block b, MaterialAccess access) {
        Material m = access.getMaterial(b);
        return new ImmutableBlock(b.getX(), b.getY(), b.getZ(), m.name(), m.isSolid(),
                m.isOccluding());
    }

}
