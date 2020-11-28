package com.github.ness.check.misc;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.NessInventoryClickEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class ChestStealer extends Check {
    
    /**
     * @author MatuloM
     */
    
    private long moveInvItemsLastTime;
    private int movedInvItems;
    private Material lastItemType = Material.AIR;


    public ChestStealer(NessPlayer player) {
        super(ChestStealer.class, player);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    public void onBukkitEvent(NessInventoryClickEvent e) {
        NessPlayer nessPlayer = player();
        if (player().isNot(e.getWhoClicked()))
            return;
        final Inventory i1 = e.getWhoClicked().getInventory();
        final Inventory i2 = e.getInventory();
        if(nessPlayer.getBukkitPlayer().getGameMode().equals(GameMode.CREATIVE) || e.getCurrentItem() == null) {
            return;
        }
        final Material itemType = e.getCurrentItem().getType();
        if (i1 != i2 && itemType != Material.AIR) {
            if (!lastItemType.equals(itemType)) {
                movedInvItems++;
                if (movedInvItems > 4) {

                    //if(player().setViolation(new Violation("ChestStealer", "movedInventoryItems: " + movedInvItems))) e.setCancelled(true);
                    flag(" movedInventoryItems: " + movedInvItems, e);
                    movedInvItems = 0;
                }
                final long now = System.currentTimeMillis();
                final long result = now - moveInvItemsLastTime;
                if (result < 80) {
                    flag(" timeBetweenMovedItems: " + result, e);
                    //if(player().setViolation(new Violation("ChestStealer", "timeBetweenMovedItems: " + result))) e.setCancelled(true);
                }
                moveInvItemsLastTime = System.currentTimeMillis();
            }
            lastItemType = itemType;
        }
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
        // TODO Auto-generated method stub
        
    }

}
