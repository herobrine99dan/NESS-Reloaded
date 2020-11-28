package com.github.ness.data;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.ness.blockgetter.MaterialAccess;

import lombok.Getter;

public class ImmutableBlock {
    @Getter
    private final ImmutableLoc loc;
    @Getter
    private final String type;
    @Getter
    private final boolean occluding;
    @Getter
    private final boolean solid;

    public ImmutableBlock(ImmutableLoc loc, String type, boolean solid, boolean occluding) {
        this.loc = loc;
        this.type = type;
        this.solid = solid;
        this.occluding = occluding;
    }

    public static ImmutableBlock of(Block b, MaterialAccess access) {
        Material m = access.getMaterial(b);
        ImmutableLoc loc = new ImmutableLoc(b.getWorld().getName(), b.getX(), b.getY(), b.getZ(), 0, 0, false);
        return new ImmutableBlock(loc, m.name(), m.isSolid(), m.isOccluding());
    }

}
