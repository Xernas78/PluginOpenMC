package fr.communaywen.core.corporation;

import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.utils.world.WorldUtils;
import fr.communaywen.core.utils.world.Yaw;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopBlocksManager {

    private final Map<UUID, Shop.Multiblock> multiblocks = new HashMap<>();
    private final Map<Location, Shop> shopsByLocation = new HashMap<>();

    private final AywenCraftPlugin plugin;

    public ShopBlocksManager(AywenCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerMultiblock(Shop shop, Shop.Multiblock multiblock) {
        multiblocks.put(shop.getUuid(), multiblock);
        Location stockLoc = multiblock.getStockBlock();
        Location cashLoc = multiblock.getCashBlock();
        shopsByLocation.put(stockLoc, shop);
        shopsByLocation.put(cashLoc, shop);
    }

    public Shop.Multiblock getMultiblock(UUID uuid) {
        return multiblocks.get(uuid);
    }

    public Shop getShop(Location location) {
        return shopsByLocation.get(location);
    }

    public void placeShop(Shop shop, Player player, boolean isCompany) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Block stockBlock = multiblock.getStockBlock().getBlock();
        Yaw yaw = WorldUtils.getYaw(player);
        //TODO ItemsAdder cash register
        cashBlock.setType(Material.OAK_SIGN);
        BlockData cashData = cashBlock.getBlockData();
        if (cashData instanceof Directional directional) {
            directional.setFacing(yaw.getOpposite().toBlockFace());
            cashBlock.setBlockData(directional);
        }
    }

    public boolean removeShop(Shop shop) {
        Shop.Multiblock multiblock = multiblocks.get(shop.getUuid());
        if (multiblock == null) {
            return false;
        }
        Block cashBlock = multiblock.getCashBlock().getBlock();
        Block stockBlock = multiblock.getStockBlock().getBlock();
        //TODO ItemsAdder cash register
        if (cashBlock.getType() != Material.OAK_SIGN || stockBlock.getType() != Material.BARREL) {
            return false;
        }
        multiblocks.remove(shop.getUuid());
        new BukkitRunnable() {
            @Override
            public void run() {
                shopsByLocation.forEach((location, shop1) -> {
                    if (shop1.getUuid().equals(shop.getUuid())) shopsByLocation.remove(location);
                });
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

}
