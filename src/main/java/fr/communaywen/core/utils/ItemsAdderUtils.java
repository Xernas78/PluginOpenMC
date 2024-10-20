package fr.communaywen.core.utils;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderUtils {

    public static ItemStack getNonNullCustomStack(String customStackName) {
        return CustomStack.getInstance(customStackName) == null ? new ItemStack(Material.BARRIER) : CustomStack.getInstance(customStackName).getItemStack();
    }

}
