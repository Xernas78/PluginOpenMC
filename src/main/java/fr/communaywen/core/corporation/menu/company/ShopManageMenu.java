package fr.communaywen.core.corporation.menu.company;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.communaywen.core.corporation.Company;
import fr.communaywen.core.corporation.CompanyManager;
import fr.communaywen.core.corporation.PlayerShopManager;
import fr.communaywen.core.corporation.Shop;
import fr.communaywen.core.corporation.menu.shop.ShopMenu;
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

public class ShopManageMenu extends PaginatedMenu {

    private final Company company;
    private final CompanyManager companyManager;
    private final PlayerShopManager playerShopManager;

    public ShopManageMenu(Player owner, Company company, CompanyManager companyManager, PlayerShopManager playerShopManager) {
        super(owner);
        this.company = company;
        this.companyManager = companyManager;
        this.playerShopManager = playerShopManager;
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
        for (Shop shop : company.getShops()) {
            items.add(shop.getIcon(this, false).setNextMenu(new ShopMenu(getOwner(), companyManager, playerShopManager, shop, 0)));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName(ChatColor.GRAY + "Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.GREEN + "Page suivante"));
        if ((getPage() == 0 && isLastPage()) || company.getShops().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.setDisplayName(ChatColor.RED + "Retour"))
                    .setNextMenu(new CompanyMenu(getOwner(), company, false)));
            buttons.put(50, nextPageButton);
        } else {
            buttons.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.RED + "Page précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        return "Shop Management";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
