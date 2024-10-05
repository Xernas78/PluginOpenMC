package fr.communaywen.core.corporation.menu.company;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.communaywen.core.corporation.Company;
import fr.communaywen.core.corporation.data.TransactionData;
import fr.communaywen.core.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyBankTransactionsMenu extends PaginatedMenu {

    private final Company company;

    public CompanyBankTransactionsMenu(Player owner, Company company) {
        super(owner);
        this.company = company;
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
        List<Long> timestamps = new ArrayList<>(company.getTransactions().getQueue().keySet());
        List<TransactionData> transactions = new ArrayList<>(company.getTransactions().getQueue().values());
        for (int i = 0; i < timestamps.size(); i++) {
            long timestamp = timestamps.get(i);
            TransactionData transaction = transactions.get(i);
            int finalI = i;
            items.add(new ItemBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.setDisplayName(ChatColor.YELLOW + "Transaction #" + finalI);
                List<String> lore = new ArrayList<>(List.of(
                        ChatColor.GRAY + "■ Date: " + ChatColor.WHITE + new SimpleDateFormat("MM/dd/yyyy").format(timestamp),
                        ChatColor.GRAY + "■ Nature: " + ChatColor.WHITE + transaction.nature(),
                        ChatColor.GRAY + "■ Par: " + ChatColor.WHITE + Bukkit.getOfflinePlayer(transaction.sender()).getName()
                ));
                if (transaction.place() != null && !transaction.place().isEmpty()) {
                    lore.add(ChatColor.GRAY + "■ Lieu: " + ChatColor.WHITE + transaction.place());
                }
                lore.add(ChatColor.GRAY + "■ Montant: " + EconomyManager.formatValue(transaction.value()));
                itemMeta.setLore(lore);
            }));
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
        return "Transactions de l'entreprise";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
