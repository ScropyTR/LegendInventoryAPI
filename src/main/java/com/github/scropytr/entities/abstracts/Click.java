package com.github.scropytr.entities.abstracts;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public interface Click {

    void onClick(InventoryClickEvent clickEvent);

}
