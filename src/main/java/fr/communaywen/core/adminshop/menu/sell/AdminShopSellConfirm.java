package fr.communaywen.core.adminshop.menu.sell;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.adminshop.menu.category.ShopType;
import fr.communaywen.core.adminshop.shopinterfaces.BaseItems;
import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
import fr.communaywen.core.economy.EconomyManager;
import fr.communaywen.core.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Credit("Axeno")
@Feature("AdminShop")
public class AdminShopSellConfirm extends Menu {
    private final BaseItems items;
    private final int quantity;

    public AdminShopSellConfirm(Player player, BaseItems items, int quantity) {
        super(player);
        this.items = items;
        this.quantity = quantity;
    }

    @Override
    public @NotNull String getName() {
        return "§6Confirmer la vente";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        for (int i = 0; i < getInventorySize().getSize(); i++) {
            content.put(i, new ItemBuilder(this, Material.BLACK_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" ")));
        }

        content.put(2, new ItemBuilder(this, Material.GREEN_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§aConfirmer la vente");
        }).setOnClick(event -> {
            if (!ItemUtils.hasEnoughItems(getOwner(), Material.getMaterial(items.named()), quantity)) {
                getOwner().sendMessage(ChatColor.RED + "Vous n'avez pas assez d'items dans votre inventaire !");
                return;
            }

            EconomyManager economy = AywenCraftPlugin.getInstance().getManagers().getEconomyManager();
            double totalAmount;
            if(items.getType() == ShopType.SELL_BUY) totalAmount = (items.getPrize() / 2) * quantity;
            else totalAmount = items.getPrize() * quantity;

<<<<<<< HEAD
            removeItemsFromInventory(getOwner(), Material.getMaterial(items.named()), quantity);
            economy.addBalance(getOwner().getUniqueId(), totalAmount);
=======
            ItemUtils.removeItemsFromInventory(getOwner(), Material.getMaterial(items.named()), quantity);
            economy.addBalance(getOwner(), totalAmount);
>>>>>>> upstream/main
            getOwner().sendMessage("§aVente confirmée !");
            getOwner().sendMessage("  §4- §c" + quantity + " " + items.getName() + " §7pour §a" + String.format("%.2f", totalAmount) + "$");

            getOwner().closeInventory();
        }));

        content.put(4, new ItemBuilder(this, Material.getMaterial(items.named()), itemMeta -> {
            itemMeta.setDisplayName(items.getName());
            double prizes = 0;
            if(items.getType() == ShopType.SELL_BUY) prizes = (items.getPrize() / 2);
            else prizes = items.getPrize();
            double finalPrize = prizes * quantity;
            itemMeta.setLore(Arrays.asList(
                    "§7Quantité: §e" + quantity,
                    "§7Prix total: §e" + String.format("%.2f", finalPrize) + "$"
            ));
        }));

        content.put(6, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.setDisplayName("§cAnnuler");
        }).setOnClick(event -> {
            getOwner().closeInventory();
        }));

        return content;
    }
}