package fr.communaywen.core.corporation.menu.company;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import dev.xernas.menulib.utils.StaticSlots;
import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.corporation.Company;
import fr.communaywen.core.corporation.data.MerchantData;
import fr.communaywen.core.teams.menu.TeamMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CompanyMenu extends PaginatedMenu {

    private final Company company;
    private final boolean isBackButton;

    public CompanyMenu(Player owner, Company company, boolean isBackButton) {
        super(owner);
        this.company = company;
        this.isBackButton = isBackButton;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.combine(StaticSlots.STANDARD, List.of(12, 13, 14));
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        Set<UUID> merchants = company.getMerchants().keySet();
        List<ItemStack> items = new ArrayList<>();
        for (UUID merchant : merchants) {
            items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(merchant), itemMeta -> {
                itemMeta.setDisplayName(ChatColor.YELLOW + Bukkit.getOfflinePlayer(merchant).getName());
                MerchantData merchantData = company.getMerchants().get(merchant);
                itemMeta.setLore(List.of(
                        ChatColor.GRAY + "■ A déposé " + ChatColor.GREEN + merchantData.getAllDepositedItemsAmount() + " items",
                        ChatColor.GRAY + "■ A gagné " + ChatColor.GREEN + merchantData.getMoneyWon() + "€"
                ));
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        ItemBuilder closeButton = new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName(ChatColor.GRAY + "Fermer")).setCloseButton();
        ItemBuilder backButton = new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.setDisplayName(ChatColor.GRAY + "Retour")).setBackButton();
        buttons.put(49, isBackButton ? backButton : closeButton);
        buttons.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.RED + "Page précédente"))
                .setPreviousPageButton());
        buttons.put(50, new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.GREEN + "Page suivante"))
                .setNextPageButton());
        ItemStack ownerItem;
        if (company.getOwner().isPlayer()) {
            ownerItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                itemMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GOLD + Bukkit.getOfflinePlayer(company.getOwner().getPlayer()).getName());
                itemMeta.setLore(List.of(
                        ChatColor.GRAY + "■ - Joueur -",
                        ChatColor.GRAY + "■ Marchants : " + company.getMerchants().size()
                ));
            });
        } else {
            ownerItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                itemMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GOLD + company.getOwner().getTeam().getName());
                itemMeta.setLore(List.of(
                        ChatColor.GRAY + "■ - Team -",
                        ChatColor.GRAY + "■ Marchants : " + company.getMerchants().size()
                ));
            }).setNextMenu(new TeamMenu(getOwner(), company.getOwner().getTeam(), true));
        }
        buttons.put(4, ownerItem);
        ItemBuilder bankButton = new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName(ChatColor.GOLD + "Banque d'entreprise");
            itemMeta.setLore(List.of(
                    ChatColor.GRAY + "■ Solde: " + ChatColor.GREEN + company.getBalance() + "€",
                    ChatColor.GRAY + "■ Chiffre d'affaires: " + ChatColor.GREEN + company.getTurnover() + "€",
                    ChatColor.GRAY + "■ Cliquez pour voir les transactions"
            ));
        });
        ItemBuilder shopsButton = new ItemBuilder(this, Material.BARREL, itemMeta -> {
            itemMeta.setDisplayName(ChatColor.GOLD + "Shops");
            itemMeta.setLore(List.of(
                    ChatColor.GRAY + "■ Nombre: " + ChatColor.GREEN + company.getShops().size(),
                    ChatColor.GRAY + "■ Cliquez pour voir les shops"
            ));
        });
        if (company.isIn(getOwner().getUniqueId())) {
            buttons.put(26, bankButton.setNextMenu(new CompanyBankTransactionsMenu(getOwner(), company)));
            buttons.put(35, shopsButton.setNextMenu(new ShopManageMenu(getOwner(), company, AywenCraftPlugin.getInstance().getManagers().getCompanyManager(), AywenCraftPlugin.getInstance().getManagers().getPlayerShopManager())));
        }
        else {
            buttons.put(26, bankButton);
            buttons.put(35, shopsButton);
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        return company.getName();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
