package fr.communaywen.core.utils.shops;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Categories {
    public void openInventory(Player player){
        Inventory inventory = Bukkit.createInventory(null, 54, "§8Shop");

         //

        player.openInventory(inventory);
    }
}
