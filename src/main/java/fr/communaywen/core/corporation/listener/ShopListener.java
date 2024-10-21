package fr.communaywen.core.corporation.listener;

import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.corporation.CompanyManager;
import fr.communaywen.core.corporation.PlayerShopManager;
import fr.communaywen.core.corporation.Shop;
import fr.communaywen.core.corporation.ShopBlocksManager;
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
    private final ShopBlocksManager shopBlocksManager;

    public ShopListener(CompanyManager companyManager, PlayerShopManager playerShopManager, ShopBlocksManager shopBlocksManager) {
        this.companyManager = companyManager;
        this.playerShopManager = playerShopManager;
        this.shopBlocksManager = shopBlocksManager;
    }

    //TODO ItemsAdder caisse

    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {
        if (shopBlocksManager.getShop(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
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
            Shop shop = shopBlocksManager.getShop(event.getClickedBlock().getLocation());
            if (shop == null) {
                return;
            }
            event.setCancelled(true);
            ShopMenu menu = new ShopMenu(event.getPlayer(), companyManager, playerShopManager, shop, 0);
            menu.open();
        }
    }
}
