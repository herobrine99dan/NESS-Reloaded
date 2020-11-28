package com.github.ness.packets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.Packet.Direction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.SendedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.packets.event.bukkit.NessBlockBreakEvent;
import com.github.ness.packets.event.bukkit.NessBlockPlaceEvent;
import com.github.ness.packets.event.bukkit.NessInventoryClickEvent;
import com.github.ness.packets.event.bukkit.NessPlayerInteractEvent;

import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.annotation.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.event.priority.PacketEventPriority;

public class NessPacketListener extends PacketListenerDynamic implements Listener {
    private NessAnticheat ness;

    public NessPacketListener(NessAnticheat ness) {
        super(PacketEventPriority.LOWEST);
        this.ness = ness;
    }

    @PacketHandler
    public void onPacketReceive(PacketReceiveEvent e) {
        Packet packet = new Packet(Direction.RECEIVE, e.getNMSPacket(), e.getPacketId());
        ReceivedPacketEvent event;
        if (packet.isPosition() || packet.isFlying() || packet.isRotation()) {
            event = new FlyingEvent(ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId()), packet);
        } else if (packet.isUseEntity()) {
            event = new UseEntityEvent(ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId()), packet);
        } else {
            event = new ReceivedPacketEvent(ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId()), packet);
        }
        event.process();
        ness.getCheckManager().onEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        NessPlayer nessPlayer = this.ness.getCheckManager().getNessPlayer(e.getWhoClicked().getUniqueId());
        NessInventoryClickEvent event = new NessInventoryClickEvent(e, nessPlayer);
        ness.getCheckManager().onEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler
    public void onPlaces(BlockPlaceEvent e) {
        NessPlayer nessPlayer = this.ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId());
        NessBlockPlaceEvent event = new NessBlockPlaceEvent(e, nessPlayer);
        ness.getCheckManager().onEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        NessPlayer nessPlayer = this.ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId());
        NessBlockBreakEvent event = new NessBlockBreakEvent(e, nessPlayer);
        ness.getCheckManager().onEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        NessPlayer nessPlayer = this.ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId());
        NessPlayerInteractEvent event = new NessPlayerInteractEvent(nessPlayer, e);
        ness.getCheckManager().onEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @PacketHandler
    public void onPacketSend(PacketSendEvent e) {
        Packet packet = new Packet(Direction.SEND, e.getNMSPacket(), e.getPacketId());
        SendedPacketEvent event = new SendedPacketEvent(ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId()), packet);
        if(event.getNessPlayer() == null) {
            return;
        }
        if(event.getNessPlayer().getChecksInitialized().get()) {
            ness.getCheckManager().onEvent(event);
        }
        e.setCancelled(event.isCancelled());
    }

}
