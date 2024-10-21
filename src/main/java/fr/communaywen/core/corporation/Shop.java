package fr.communaywen.core.corporation;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
import fr.communaywen.core.economy.EconomyManager;
import fr.communaywen.core.teams.utils.MethodState;
import fr.communaywen.core.utils.InverseStack;
import fr.communaywen.core.utils.ItemUtils;
import fr.communaywen.core.utils.world.WorldUtils;
import fr.communaywen.core.utils.world.Yaw;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Credit("Xernas")
@Feature("Shops")
@Getter
public class Shop {

    private final ShopOwner owner;
    private final EconomyManager economyManager;
    private final ShopBlocksManager blocksManager;
    private final List<ShopItem> items = new ArrayList<>();
    private final List<ShopItem> sales = new ArrayList<>();
    private final Map<Long, Supply> suppliers = new HashMap<>();
    private final int index;
    private final UUID uuid = UUID.randomUUID();

    private double turnover = 0;

    public Shop(ShopOwner owner, int index, EconomyManager economyManager, ShopBlocksManager blocksManager) {
        this.owner = owner;
        this.index = index;
        this.economyManager = economyManager;
        this.blocksManager = blocksManager;
    }

    public void checkStock() {
        Multiblock multiblock = blocksManager.getMultiblock(getUuid());
        if (multiblock != null) {
            return;
        }
        Block stockBlock = multiblock.getStockBlock().getBlock();
        if (stockBlock.getType() != Material.BARREL) {
            blocksManager.removeShop(this);
            return;
        }
        if (stockBlock.getState() instanceof Barrel barrel) {
            Inventory inventory = barrel.getInventory();
            for (ItemStack item : inventory.getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }
                PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                if (dataContainer.has(AywenCraftPlugin.SUPPLIER_KEY, PersistentDataType.STRING)) {
                    String supplierUUID = dataContainer.get(AywenCraftPlugin.SUPPLIER_KEY, PersistentDataType.STRING);
                    if (supplierUUID == null) {
                        continue;
                    }
                    List<UUID> possibleSuppliers = new ArrayList<>();
                    if (owner.isCompany()) {
                        possibleSuppliers.addAll(owner.getCompany().getAllMembers());
                    }
                    if (owner.isPlayer()) possibleSuppliers.add(owner.getPlayer());
                    if (!possibleSuppliers.contains(UUID.fromString(supplierUUID))) {
                        continue;
                    }
                    boolean supplied = supply(item, UUID.fromString(supplierUUID));
                    if (supplied) inventory.remove(item);
                }
            }
        }
    }

    public String getName() {
        return owner.isCompany() ? ("Shop #" + index) : Bukkit.getOfflinePlayer(owner.getPlayer()).getName() + "'s Shop";
    }

    public UUID getSupremeOwner() {
        return owner.isCompany() ? owner.getCompany().getOwner().getPlayer() : owner.getPlayer();
    }

    public boolean isOwner(UUID uuid) {
        if (owner.isCompany()) {
            return owner.getCompany().isOwner(uuid);
        }
        return owner.getPlayer().equals(uuid);
    }

    public boolean addItem(ItemStack itemStack, double price) {
        ShopItem item = new ShopItem(itemStack, price);
        for (ShopItem shopItem : items) {
            if (shopItem.getItem().isSimilar(itemStack)) {
                return true;
            }
        }
        items.add(item);
        return false;
    }

    public ShopItem getItem(int index) {
        return items.get(index);
    }

    public void removeItem(ShopItem item) {
        items.remove(item);
    }

    public boolean supply(ItemStack item, UUID supplier) {
        for (ShopItem shopItem : items) {
            if (shopItem.getItem().isSimilar(item)) {
                shopItem.setAmount(shopItem.getAmount() + item.getAmount());
                suppliers.put(System.currentTimeMillis(), new Supply(supplier, shopItem.getItemID(), item.getAmount()));
                return true;
            }
        }
        return false;
    }

    public MethodState buy(ShopItem item, int amount, Player buyer) {
        if (isFull(buyer)) {
            return MethodState.SPECIAL;
        }
        if (amount > item.getAmount()) {
            return MethodState.WARNING;
        }
        //TODO Remettre ça
//        if (isOwner(buyer.getUniqueId())) {
//            return MethodState.FAILURE;
//        }
        item.setAmount(item.getAmount() - amount);
        turnover += item.getPrice(amount);
        if (owner.isCompany()) {
            int amountToBuy = amount;
            double price = item.getPrice(amount);
            double companyCut = price * owner.getCompany().getCut();
            double suppliersCut = price - companyCut;
            boolean supplied = false;
            List<Supply> supplies = new ArrayList<>();
            for (Map.Entry<Long, Supply> entry : suppliers.entrySet()) {
                if (entry.getValue().getItemId().equals(item.getItemID())) {
                    long latest = 0;
                    if (entry.getKey() > latest) {
                        latest = entry.getKey();
                        supplies.add(entry.getValue());
                    }
                }
            }
            if (!supplies.isEmpty()) {
                supplied = true;
                for (Supply supply : supplies) {
                    if (amountToBuy == 0) break;
                    if (amountToBuy >= supply.getAmount()) {
                        amountToBuy -= supply.getAmount();
                        removeLatestSupply();
                        double supplierCut = suppliersCut * ((double) supply.getAmount() / amount);
                        economyManager.addBalance(supply.getSupplier(), supplierCut);
                    }
                    else {
                        supply.setAmount(supply.getAmount() - amountToBuy);
                        double supplierCut = suppliersCut * ((double) amountToBuy / amount);
                        economyManager.addBalance(supply.getSupplier(), supplierCut);
                        break;
                    }
                }
            }
            if (!supplied) {
                return MethodState.ESCAPE;
            }
            owner.getCompany().deposit(companyCut, buyer, "Vente", getName(), economyManager);
        }
        else {
            if (!economyManager.withdrawBalance(buyer.getUniqueId(), item.getPrice(amount))) return MethodState.ERROR;
            economyManager.addBalance(owner.getPlayer(), item.getPrice(amount));
        }
        //TODO Give certain amount of that item to the buyer
        ItemStack toGive = item.getItem().clone();
        toGive.setAmount(amount);
        List<ItemStack> stacks = ItemUtils.splitAmountIntoStack(toGive);
        for (ItemStack stack : stacks) {
            buyer.getInventory().addItem(stack);
        }
        sales.add(item.copy().setAmount(amount));
        return MethodState.SUCCESS;
    }

    private Supply removeLatestSupply() {
        long latest = 0;
        Supply supply = null;
        for (Map.Entry<Long, Supply> entry : suppliers.entrySet()) {
            if (entry.getKey() > latest) {
                latest = entry.getKey();
                supply = entry.getValue();
            }
        }
        if (supply != null) {
            suppliers.remove(latest);
        }
        return supply;
    }

    public ItemBuilder getIcon(Menu menu, boolean fromShopMenu) {
        return new ItemBuilder(menu, fromShopMenu ? Material.GOLD_INGOT : Material.BARREL, itemMeta -> {
            itemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + (fromShopMenu ? "Informations" : getName()));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "■ Chiffre d'affaires: " + EconomyManager.formatValue(turnover) + "€");
            lore.add(ChatColor.GRAY + "■ Ventes: " + ChatColor.WHITE + sales.size());
            if (!fromShopMenu)
                lore.add(ChatColor.GRAY + "■ Cliquez pour accéder au shop");
            itemMeta.setLore(lore);
        });
    }

    public int getAllItemsAmount() {
        int amount = 0;
        for (ShopItem item : items) {
            amount += item.getAmount();
        }
        return amount;
    }

    private boolean isFull(Player player) {
        for (ItemStack item : getContentsWithoutArmorOrExtras(player)) {
            if (item == null || item.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }

    private ItemStack[] getContentsWithoutArmorOrExtras(Player player) {
        // Get player's inventory contents without armor or extras
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] inventory = new ItemStack[36];
        System.arraycopy(contents, 0, inventory, 0, 36);
        return inventory;
    }

    public static UUID getShopPlayerLookingAt(Player player, ShopBlocksManager shopBlocksManager, boolean onlyCash) {
        Block targetBlock = player.getTargetBlockExact(5);
        //TODO ItemsAdder cash register
        if (targetBlock == null || (targetBlock.getType() != Material.BARREL && targetBlock.getType() != Material.OAK_SIGN)) {
            return null;
        }
        if (onlyCash) {
            if (targetBlock.getType() != Material.OAK_SIGN) {
                return null;
            }
        }
        Shop shop = shopBlocksManager.getShop(targetBlock.getLocation());
        if (shop == null) {
            return null;
        }
        return shop.getUuid();
    }

    public static List<Shop> getAllShops(CompanyManager companyManager, PlayerShopManager playerShopManager) {
        List<Shop> shops = new ArrayList<>();
        for (Company company : companyManager.getCompanies()) {
            shops.addAll(company.getShops());
        }
        shops.addAll(playerShopManager.getPlayerShops().values());
        return shops;
    }

    @Getter
    public static class Multiblock {

        private final Location stockBlock;
        private final Location cashBlock;

        public Multiblock(Location stockBlock, Location cashBlock) {
            this.stockBlock = stockBlock;
            this.cashBlock = cashBlock;
        }

    }

}
