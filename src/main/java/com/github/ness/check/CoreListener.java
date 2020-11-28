package com.github.ness.check;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ness.NessPlayer;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.ReceivedPacketEvent;

public class CoreListener implements Listener {

    private CheckManager manager;

    public CoreListener(CheckManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        manager.makeNessPlayer(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        NessPlayer nessPlayer = manager.getNessPlayer(e.getPlayer().getUniqueId());
        nessPlayer.updateMovementValue(
                new MovementValues(e.getPlayer(), ImmutableLoc.of(e.getTo()), ImmutableLoc.of(e.getFrom()),
                this.manager.getNess().getMaterialAccess()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent e) {
        manager.getNessPlayer(e.getPlayer().getUniqueId()).cleanFields();
        manager.removeNessPlayer(e.getPlayer());
    }

}
