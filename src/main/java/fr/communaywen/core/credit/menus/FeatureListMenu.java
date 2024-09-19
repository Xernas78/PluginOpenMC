package fr.communaywen.core.credit.menus;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.communaywen.core.credit.FeatureData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureListMenu extends PaginatedMenu {

    private final List<FeatureData> features;

    public FeatureListMenu(Player owner, List<FeatureData> features) {
        super(owner);
        this.features = features;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (FeatureData feature : features) {
            items.add(new ItemBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GOLD + feature.getFeature());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "■ Développeurs :");
                for (String developer : feature.getDevelopers()) lore.add(ChatColor.GRAY + "DEV - " + developer);
                lore.add(ChatColor.GRAY + "■ Collaborateurs :");
                for (String collaborator : feature.getCollaborators()) lore.add(ChatColor.GRAY + "COL - " + collaborator);
                itemMeta.setLore(lore);
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName(ChatColor.GRAY + "Fermer"))
                .setCloseButton());
        map.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.RED + "Page précédente"))
                .setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.GREEN + "Page suivante"))
                .setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Liste des features";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
