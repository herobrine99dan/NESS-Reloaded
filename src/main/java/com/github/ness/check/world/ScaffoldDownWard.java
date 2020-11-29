package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;
import com.github.ness.packets.event.bukkit.NessPlayerInteractEvent;

public class ScaffoldDownWard extends Check {

    public ScaffoldDownWard(NessPlayer nessPlayer, CheckManager manager) {
        super(ScaffoldDownWard.class, nessPlayer, manager);
    }

    @Override
    public void onBukkitEvent(NessBukkitEvent e) {
        if (!(e instanceof NessPlayerInteractEvent)) {
            return;
        }
        PlayerInteractEvent event = (PlayerInteractEvent) e.getEvent();
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block below = p.getLocation().subtract(0, 1, 0).getBlock();
            if (event.getClickedBlock().equals(below) && event.getBlockFace().equals(BlockFace.DOWN)) {
                this.flag(e);
            }
        }
    }

}
