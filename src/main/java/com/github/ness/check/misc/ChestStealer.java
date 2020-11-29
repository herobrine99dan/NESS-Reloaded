package com.github.ness.check.misc;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;
import com.github.ness.packets.event.bukkit.NessInventoryClickEvent;

public class ChestStealer extends Check {
    
    /**
     * @author MatuloM
     */
    
    private long moveInvItemsLastTime;
    private int movedInvItems;
    private Material lastItemType = Material.AIR;


    public ChestStealer(NessPlayer player, CheckManager manager) {
        super(ChestStealer.class, player, true, 500, manager);
    }
    
    @Override
    public void checkAsyncPeriodic() {
        movedInvItems = 0;
    }

    @Override
    public void onFlying(FlyingEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onBukkitEvent(NessBukkitEvent e) {
        NessPlayer nessPlayer = player();
        if(!(e instanceof NessInventoryClickEvent)) {
            return;
        }
        InventoryClickEvent event = (InventoryClickEvent)e.getEvent();
        if (player().isNot(event.getWhoClicked()))
            return;
        final Inventory i1 = event.getWhoClicked().getInventory();
        final Inventory i2 = event.getInventory();
        if(nessPlayer.getBukkitPlayer().getGameMode().equals(GameMode.CREATIVE) || event.getCurrentItem() == null) {
            return;
        }
        final Material itemType = event.getCurrentItem().getType();
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
                System.out.println(result);
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
