package fr.communaywen.core.corporation.listener;

import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.corporation.CompanyManager;
import fr.communaywen.core.corporation.PlayerShopManager;
import fr.communaywen.core.corporation.Shop;
import fr.communaywen.core.corporation.menu.shop.ShopMenu;
import org.bukkit.block.Barrel;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class ShopListener implements Listener {

    private final CompanyManager companyManager;
    private final PlayerShopManager playerShopManager;

    public ShopListener(CompanyManager companyManager, PlayerShopManager playerShopManager) {
        this.companyManager = companyManager;
        this.playerShopManager = playerShopManager;
    }

    //TODO ItemsAdder caisse

    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign sign) {
            if (sign.getPersistentDataContainer().has(AywenCraftPlugin.COMPANY_SHOP_KEY, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
            if (sign.getPersistentDataContainer().has(AywenCraftPlugin.PLAYER_SHOP_KEY, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
        if (event.getBlock().getState() instanceof Barrel barrel) {
            if (barrel.getPersistentDataContainer().has(AywenCraftPlugin.COMPANY_SHOP_KEY, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
            if (barrel.getPersistentDataContainer().has(AywenCraftPlugin.PLAYER_SHOP_KEY, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onShopClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getState() instanceof Sign sign) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            Shop shop;
            if (sign.getPersistentDataContainer().has(AywenCraftPlugin.COMPANY_SHOP_KEY, PersistentDataType.STRING)) {
                UUID shopUUID = UUID.fromString(Objects.requireNonNull(sign.getPersistentDataContainer().get(AywenCraftPlugin.COMPANY_SHOP_KEY, PersistentDataType.STRING)));
                shop = companyManager.getAnyShop(shopUUID);
            }
            else if (sign.getPersistentDataContainer().has(AywenCraftPlugin.PLAYER_SHOP_KEY, PersistentDataType.STRING)) {
                UUID shopUUID = UUID.fromString(Objects.requireNonNull(sign.getPersistentDataContainer().get(AywenCraftPlugin.PLAYER_SHOP_KEY, PersistentDataType.STRING)));
                shop = playerShopManager.getShopByUUID(shopUUID);
            }
            else {
                return;
            }
            if (shop == null) {
                return;
            }
            event.setCancelled(true);
            ShopMenu menu = new ShopMenu(event.getPlayer(), companyManager, playerShopManager, shop, 0);
            menu.open();
        }
    }
}
