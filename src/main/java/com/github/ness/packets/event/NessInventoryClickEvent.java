package com.github.ness.packets.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.ness.NessPlayer;

public class NessInventoryClickEvent extends NessEvent {
    
    InventoryClickEvent event;
    
    public NessInventoryClickEvent(InventoryClickEvent event, NessPlayer player) {
        super(player);
        this.event = event;
    }

    public HumanEntity getWhoClicked() {
        // TODO Auto-generated method stub
        return event.getWhoClicked();
    }

    public Inventory getInventory() {
        // TODO Auto-generated method stub
        return event.getInventory();
    }

    public ItemStack getCurrentItem() {
        return event.getCurrentItem();
    }

    @Override
    public NessPlayer getNessPlayer() {
        // TODO Auto-generated method stub
        return null;
    }

}
