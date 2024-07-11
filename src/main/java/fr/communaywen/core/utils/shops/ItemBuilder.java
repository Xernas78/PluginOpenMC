package fr.communaywen.core.utils.shops;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemBuilder {
    public ItemStack getCategory(String name, Material material){ // Donne un objet qui sera dans la liste des cats
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack getItem(Material material, int buyPrice, int sellPrice){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(material.toString());
        meta.setLore(Arrays.asList(""));
        // Le lore c'est (avec les couleurs vert et rouge je sais plus
        // Achat: 123$
        // Vente: 456£

        item.setItemMeta(meta);
        return item;
    }
}
